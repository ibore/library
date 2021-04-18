package me.ibore.permissions

import android.app.Activity
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import me.ibore.exception.ClientException
import java.util.*
import kotlin.math.pow

/**
 * 权限请求工具类
 */
internal object PermissionUtils {
    /** 来源于 ApplicationInfo.PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE  */
    private const val PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE = 1 shl 29

    /**
     * 是否是 Android 11 及以上版本
     */
    val isAndroid11: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    /**
     * 是否是 Android 10 及以上版本
     */
    val isAndroid10: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * 是否是 Android 9.0 及以上版本
     */
    val isAndroid9: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    /**
     * 是否是 Android 8.0 及以上版本
     */
    val isAndroid8: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /**
     * 是否是 Android 7.0 及以上版本
     */
    val isAndroid7: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    /**
     * 是否是 Android 6.0 及以上版本
     */
    val isAndroid6: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    fun getManifestPermissions(context: Context): List<String?>? {
        return try {
            val requestedPermissions = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            ).requestedPermissions
            // 当清单文件没有注册任何权限的时候，那么这个数组对象就是空的
            // https://github.com/getActivity/XXPermissions/issues/35
            asArrayList(*requestedPermissions)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 是否有存储权限
     */
    fun isGrantedStoragePermission(context: Context): Boolean {
        return if (isAndroid11) {
            Environment.isExternalStorageManager()
        } else XPermissions.isGrantedPermission(context, *Permission.GROUP_STORAGE)
    }

    /**
     * 是否有安装权限
     */
    fun isGrantedInstallPermission(context: Context): Boolean {
        return if (isAndroid8) {
            context.packageManager.canRequestPackageInstalls()
        } else true
    }

    /**
     * 是否有悬浮窗权限
     */
    fun isGrantedWindowPermission(context: Context?): Boolean {
        return if (isAndroid6) {
            Settings.canDrawOverlays(context)
        } else true
    }

    /**
     * 是否有通知栏权限
     */
    fun isGrantedNotifyPermission(context: Context): Boolean {
        if (isAndroid7) {
            return context.getSystemService(NotificationManager::class.java).areNotificationsEnabled()
        }
        if (isAndroid6) {
            // 参考 Support 库中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled()
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            return try {
                val method = appOps.javaClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String::class.java)
                val field = appOps.javaClass.getDeclaredField("OP_POST_NOTIFICATION")
                val value = field[Int::class.java] as Int
                method.invoke(appOps, value, context.applicationInfo.uid, context.packageName) as Int == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                e.printStackTrace()
                true
            }
        }
        return true
    }

    /**
     * 是否有系统设置权限
     */
    fun isGrantedSettingPermission(context: Context): Boolean {
        return if (isAndroid6) {
            Settings.System.canWrite(context)
        } else true
    }

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    fun containsSpecialPermission(permissions: List<String>): Boolean {
        if (permissions.isEmpty()) {
            return false
        }
        for (permission in permissions) {
            if (isSpecialPermission(permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String?): Boolean {
        return Permission.MANAGE_EXTERNAL_STORAGE == permission || Permission.REQUEST_INSTALL_PACKAGES == permission
                || Permission.SYSTEM_ALERT_WINDOW == permission || Permission.NOTIFICATION_SERVICE == permission
                || Permission.WRITE_SETTINGS == permission
    }

    /**
     * 判断某些权限是否全部被授予
     */
    fun isGrantedPermissions(context: Context, permissions: List<String>): Boolean {
        // 如果是安卓 6.0 以下版本就直接返回 true
        if (!isAndroid6) {
            return true
        }
        for (permission in permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * 获取没有授予的权限
     */
    fun getDeniedPermissions(context: Context, permissions: List<String>): List<String> {
        val deniedPermission: MutableList<String> = ArrayList(permissions.size)
        // 如果是安卓 6.0 以下版本就默认授予
        if (!isAndroid6) {
            return deniedPermission
        }
        for (permission in permissions) {
            if (!isGrantedPermission(context, permission)) {
                deniedPermission.add(permission)
            }
        }
        return deniedPermission
    }

    /**
     * 判断某个权限是否授予
     */
    fun isGrantedPermission(context: Context, permission: String): Boolean {
        // 如果是安卓 6.0 以下版本就默认授予
        if (!isAndroid6) {
            return true
        }
        // 检测存储权限
        if (Permission.MANAGE_EXTERNAL_STORAGE == permission) {
            return isGrantedStoragePermission(context)
        }
        // 检测安装权限
        if (Permission.REQUEST_INSTALL_PACKAGES == permission) {
            return isGrantedInstallPermission(context)
        }
        // 检测悬浮窗权限
        if (Permission.SYSTEM_ALERT_WINDOW == permission) {
            return isGrantedWindowPermission(context)
        }
        // 检测通知栏权限
        if (Permission.NOTIFICATION_SERVICE == permission) {
            return isGrantedNotifyPermission(context)
        }
        // 检测系统权限
        if (Permission.WRITE_SETTINGS == permission) {
            return isGrantedSettingPermission(context)
        }
        // 检测 10.0 的三个新权限
        if (!isAndroid10) {
            if (Permission.ACCESS_BACKGROUND_LOCATION == permission || Permission.ACCESS_MEDIA_LOCATION == permission) {
                return true
            }
            if (Permission.ACTIVITY_RECOGNITION == permission) {
                return context.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED
            }
        }
        // 检测 9.0 的一个新权限
        if (!isAndroid9) {
            if (Permission.ACCEPT_HANDOVER == permission) {
                return true
            }
        }
        // 检测 8.0 的两个新权限
        if (!isAndroid8) {
            if (Permission.ANSWER_PHONE_CALLS == permission) {
                return true
            }
            if (Permission.READ_PHONE_NUMBERS == permission) {
                return context.checkSelfPermission(Permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
            }
        }
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 获取某个权限的状态
     *
     * @return        已授权返回  [PackageManager.PERMISSION_GRANTED]
     * 未授权返回  [PackageManager.PERMISSION_DENIED]
     */
    fun getPermissionStatus(context: Context, permission: String): Int {
        return if (isGrantedPermission(
                context,
                permission
            )
        ) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permissions            请求的权限
     */
    fun isPermissionPermanentDenied(activity: Activity, permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (isPermissionPermanentDenied(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 判断某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permission            请求的权限
     */
    fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (!isAndroid6) {
            return false
        }

        // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
        if (isSpecialPermission(permission)) {
            return false
        }

        // 检测 10.0 的三个新权限
        if (!isAndroid10) {
            if (Permission.ACCESS_BACKGROUND_LOCATION == permission || Permission.ACCESS_MEDIA_LOCATION == permission) {
                return false
            }
            if (Permission.ACTIVITY_RECOGNITION == permission) {
                return activity.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission)
            }
        }

        // 检测 9.0 的一个新权限
        if (!isAndroid9) {
            if (Permission.ACCEPT_HANDOVER == permission) {
                return false
            }
        }

        // 检测 8.0 的两个新权限
        if (!isAndroid8) {
            if (Permission.ANSWER_PHONE_CALLS == permission) {
                return true
            }
            if (Permission.READ_PHONE_NUMBERS == permission) {
                return activity.checkSelfPermission(Permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission)
            }
        }
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED && !activity.shouldShowRequestPermissionRationale(
            permission
        )
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    fun getDeniedPermissions(
        permissions: Array<String>,
        grantResults: IntArray
    ): MutableList<String> {
        val deniedPermissions: MutableList<String> = ArrayList()
        for (i in grantResults.indices) {
            // 把没有授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i])
            }
        }
        return deniedPermissions
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions       需要请求的权限组
     * @param grantResults      允许结果组
     */
    fun getGrantedPermissions(
        permissions: Array<String>,
        grantResults: IntArray
    ): MutableList<String> {
        val grantedPermissions: MutableList<String> = ArrayList()
        for (i in grantResults.indices) {
            // 把授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i])
            }
        }
        return grantedPermissions
    }

    /**
     * 处理和优化已经过时的权限
     */
    fun optimizeDeprecatedPermission(permission: MutableList<String>) {
        // 如果本次申请包含了 Android 11 存储权限
        if (permission.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
            require(
                !(permission.contains(Permission.READ_EXTERNAL_STORAGE) || permission.contains(
                    Permission.WRITE_EXTERNAL_STORAGE
                ))
            ) {
                // 检测是否有旧版的存储权限，有的话直接抛出异常，请不要自己动态申请这两个权限
                "Please do not apply for these two permissions dynamically"
            }
            if (!isAndroid11) {
                // 自动添加旧版的存储权限，因为旧版的系统不支持申请新版的存储权限
                permission.add(Permission.READ_EXTERNAL_STORAGE)
                permission.add(Permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        if (!isAndroid8 && permission.contains(Permission.READ_PHONE_NUMBERS) && !permission.contains(
                Permission.READ_PHONE_STATE
            )
        ) {
            // 自动添加旧版的读取电话号码权限，因为旧版的系统不支持申请新版的权限
            permission.add(Permission.READ_PHONE_STATE)
        }
        if (!isAndroid10 && permission.contains(Permission.ACTIVITY_RECOGNITION) && !permission.contains(
                Permission.BODY_SENSORS
            )
        ) {
            // 自动添加传感器权限，因为这个权限是从 Android 10 开始才从传感器权限中剥离成独立权限
            permission.add(Permission.BODY_SENSORS)
        }
    }

    /**
     * 将数组转换成 ArrayList
     *
     * 这里解释一下为什么不用 Arrays.asList
     * 第一是返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList
     * 第二是返回的 ArrayList 对象是只读的，也就是不能添加任何元素，否则会抛异常
     */
    fun <T> asArrayList(vararg array: T): List<T> {
        if (array.isEmpty()) {
            return arrayListOf()
        }
        val list = ArrayList<T>(array.size)
        for (t in array) {
            list.add(t)
        }
        return list
    }

    fun checkStoragePermission(context: Context, requestPermissions: List<String?>) {
        val targetSdkVersion = context.applicationInfo.targetSdkVersion
        // 在 Android 10 的手机才走这个判断，否则不进行判断，因为在 Android 9.0 及以下的手机上不会设置这个标记
        if (targetSdkVersion >= Build.VERSION_CODES.Q && isAndroid10
            && (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)
                    || requestPermissions.contains(Permission.READ_EXTERNAL_STORAGE)
                    || requestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE))) {
            try {
                // 为什么不通过反射 ApplicationInfo.hasRequestedLegacyExternalStorage 方法来判断？因为这个 API 属于反射黑名单，反射执行不了
                val field = ApplicationInfo::class.java.getDeclaredField("privateFlags")
                val privateFlags = field[context.applicationInfo] as Int
                val requestLegacyExternalStorage =
                    privateFlags and PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE != 0
                if (!requestLegacyExternalStorage) {
                    // 请在清单文件 Application 节点中注册 android:requestLegacyExternalStorage="true" 属性，否则无法在 Android 10 的设备上正常读写外部存储
                    throw IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" attribute in the manifest file")
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

        // 在已经适配 Android 11 的情况下，不能用旧版的存储权限，而是应该用新版的存储权限
        if (targetSdkVersion >= Build.VERSION_CODES.R && (requestPermissions.contains(Permission.READ_EXTERNAL_STORAGE) || requestPermissions.contains(
                Permission.WRITE_EXTERNAL_STORAGE
            ))
        ) {
            // 请直接使用 Permission.MANAGE_EXTERNAL_STORAGE 来申请权限
            throw IllegalArgumentException("Please use Permission.MANAGE_EXTERNAL_STORAGE to request storage permission")
        }
    }

    /**
     * 检查定位权限
     *
     * @param requestPermissions    请求的权限组
     */
    fun checkLocationPermission(requestPermissions: List<String>) {
        if (!requestPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION)) {
            return
        }
        for (permission in requestPermissions) {
            if (Permission.ACCESS_FINE_LOCATION == permission || Permission.ACCESS_COARSE_LOCATION == permission || Permission.ACCESS_BACKGROUND_LOCATION == permission) {
                continue
            }
            throw IllegalArgumentException("Because it includes background location permissions, do not apply for permissions unrelated to location")
        }
    }

    /**
     * 检查targetSdkVersion 是否符合要求
     *
     * @param requestPermissions        请求的权限组
     */
    fun checkTargetSdkVersion(context: Context, requestPermissions: List<String?>) {
        // targetSdk 最低版本要求
        val targetSdkMinVersion: Int =
            if (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 必须设置 targetSdkVersion >= 30 才能正常检测权限，否则请使用 Permission.Group.STORAGE 来申请存储权限
                Build.VERSION_CODES.R
            } else if (requestPermissions.contains(Permission.ACCEPT_HANDOVER)) {
                Build.VERSION_CODES.P
            } else if (requestPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION)
                    || requestPermissions.contains(Permission.ACTIVITY_RECOGNITION)
                    || requestPermissions.contains(Permission.ACCESS_MEDIA_LOCATION)) {
                Build.VERSION_CODES.Q
            } else if (requestPermissions.contains(Permission.REQUEST_INSTALL_PACKAGES)
                    || requestPermissions.contains(Permission.ANSWER_PHONE_CALLS)
                    || requestPermissions.contains(Permission.READ_PHONE_NUMBERS)) {
                Build.VERSION_CODES.O
            } else {
                Build.VERSION_CODES.M
            }
        // 必须设置正确的 targetSdkVersion 才能正常检测权限
        if (context.applicationInfo.targetSdkVersion < targetSdkMinVersion) {
            throw RuntimeException("The targetSdkVersion SDK must be $targetSdkMinVersion or more")
        }
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param requestPermissions    请求的权限组
     */
    fun checkPermissionManifest(context: Context, requestPermissions: List<String?>) {
        val manifestPermissions = getManifestPermissions(context)
        if (manifestPermissions == null || manifestPermissions.isEmpty()) {
            throw ClientException("No permissions are registered in the manifest file")
        }
        val minSdkVersion: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.applicationInfo.minSdkVersion
        } else {
            Build.VERSION_CODES.M
        }
        for (permission in requestPermissions) {
            if (minSdkVersion < Build.VERSION_CODES.R) {
                if (Permission.MANAGE_EXTERNAL_STORAGE == permission) {
                    if (!manifestPermissions.contains(Permission.READ_EXTERNAL_STORAGE)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw ClientException("${Permission.READ_EXTERNAL_STORAGE}: Permissions are not registered in the manifest file")
                    }
                    if (!manifestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw ClientException("${Permission.WRITE_EXTERNAL_STORAGE}: Permissions are not registered in the manifest file")
                    }
                }
            }
            if (minSdkVersion < Build.VERSION_CODES.Q) {
                if (Permission.ACTIVITY_RECOGNITION == permission && !manifestPermissions.contains(Permission.BODY_SENSORS)) {
                    // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                    throw ClientException("${Permission.BODY_SENSORS}: Permissions are not registered in the manifest file")

                }
            }
            if (minSdkVersion < Build.VERSION_CODES.O) {
                if (Permission.READ_PHONE_NUMBERS == permission && !manifestPermissions.contains(Permission.READ_PHONE_STATE)) {
                    // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                    throw ClientException("${Permission.READ_PHONE_STATE}: Permissions are not registered in the manifest file")
                }
            }
            if (Permission.NOTIFICATION_SERVICE == permission) {
                // 不检测通知栏权限有没有在清单文件中注册，因为这个权限是框架虚拟出来的，有没有在清单文件中注册都没关系
                continue
            }
            if (!manifestPermissions.contains(permission)) {
                throw ClientException("${permission}: Permissions are not registered in the manifest file")
            }
        }
    }

    /**
     * 获得随机的 RequestCode
     * 新版本的 Support 库限制请求码必须小于 65536
     * 旧版本的 Support 库限制请求码必须小于 256
     */
    val randomRequestCode: Int
        get() = Random().nextInt(2.0.pow(8.0).toInt())

}