package me.ibore.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.SparseBooleanArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList

/**
 * 权限请求处理类
 */
class PermissionFragment : Fragment(), Runnable {
    /** 是否申请了特殊权限  */
    private var mSpecialRequest = false

    /** 是否申请了危险权限  */
    private var mDangerousRequest = false

    /** 权限回调对象  */
    private var mListener: OnPermissionListener? = null

    /** Activity 屏幕方向  */
    private var mScreenOrientation = 0

    /**
     * 绑定 Activity
     */
    fun attachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss()
    }

    /**
     * 解绑 Activity
     */
    fun detachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    /**
     * 设置权限监听回调监听
     */
    fun setCallBack(listener: OnPermissionListener) {
        mListener = listener
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity ?: return
        // 如果当前没有锁定屏幕方向就获取当前屏幕方向并进行锁定
        mScreenOrientation = activity.requestedOrientation
        if (mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return
        }
        val currentOrientation = activity.resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onDetach() {
        super.onDetach()
        val activity = activity
        if (activity == null || mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return
        }
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消引用监听器，避免内存泄漏
        mListener = null
    }

    override fun onResume() {
        super.onResume()
        // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
        // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
        if (mSpecialRequest) {
            return
        }
        mSpecialRequest = true
        requestSpecialPermission()
    }

    /**
     * 申请特殊权限
     */
    fun requestSpecialPermission() {
        val arguments = arguments
        val activity = activity
        if (arguments == null || activity == null) {
            return
        }
        val permissions: List<String> = arguments.getStringArrayList(REQUEST_PERMISSIONS)?: arrayListOf()

        // 是否需要申请特殊权限
        var requestSpecialPermission = false

        // 判断当前是否包含特殊权限
        if (PermissionUtils.containsSpecialPermission(permissions)) {
            if (permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) && !PermissionUtils.isGrantedStoragePermission(activity)) {
                // 当前必须是 Android 11 及以上版本，因为 hasStoragePermission 在旧版本上是拿旧权限做的判断，所以这里需要多判断一次版本
                if (PermissionUtils.isAndroid11) {
                    // 跳转到存储权限设置界面
                    startActivityForResult(PermissionSettingPage.getStoragePermissionIntent(activity), arguments.getInt(REQUEST_CODE))
                    requestSpecialPermission = true
                }
            }
            if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.isGrantedInstallPermission(activity)) {
                // 跳转到安装权限设置界面
                startActivityForResult(PermissionSettingPage.getInstallPermissionIntent(activity), arguments.getInt(REQUEST_CODE))
                requestSpecialPermission = true
            }
            if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.isGrantedWindowPermission(activity)) {
                // 跳转到悬浮窗设置页面
                startActivityForResult(PermissionSettingPage.getWindowPermissionIntent(activity), arguments.getInt(REQUEST_CODE))
                requestSpecialPermission = true
            }
            if (permissions.contains(Permission.NOTIFICATION_SERVICE) && !PermissionUtils.isGrantedNotifyPermission(activity)) {
                // 跳转到通知栏权限设置页面
                startActivityForResult(PermissionSettingPage.getNotifyPermissionIntent(activity), arguments.getInt(REQUEST_CODE))
                requestSpecialPermission = true
            }
            if (permissions.contains(Permission.WRITE_SETTINGS) && !PermissionUtils.isGrantedSettingPermission(activity)) {
                // 跳转到系统设置权限设置页面
                startActivityForResult(PermissionSettingPage.getSettingPermissionIntent(activity), arguments.getInt(REQUEST_CODE))
                requestSpecialPermission = true
            }
        }

        // 当前必须没有跳转到悬浮窗或者安装权限界面
        if (!requestSpecialPermission) {
            requestDangerousPermission()
        }
    }

    /**
     * 申请危险权限
     */
    fun requestDangerousPermission() {
        val activity = activity
        val arguments = arguments
        if (activity == null || arguments == null) {
            return
        }
        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions == null || allPermissions.size == 0) {
            return
        }
        val locationPermission: ArrayList<String> = ArrayList()
        // Android 10 定位策略发生改变，申请后台定位权限的前提是要有前台定位权限（授予了精确或者模糊任一权限）
        if (PermissionUtils.isAndroid10 && allPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION)) {

            if (allPermissions.contains(Permission.ACCESS_COARSE_LOCATION) && !PermissionUtils.isGrantedPermission(activity, Permission.ACCESS_COARSE_LOCATION)) {
                locationPermission.add(Permission.ACCESS_COARSE_LOCATION)
            }
            if (allPermissions.contains(Permission.ACCESS_FINE_LOCATION) && !PermissionUtils.isGrantedPermission(activity, Permission.ACCESS_FINE_LOCATION)) {
                locationPermission.add(Permission.ACCESS_FINE_LOCATION)
            }
        }
        // 如果不需要申请前台定位权限就直接申请危险权限
        if (locationPermission.isEmpty()) {
            requestPermissions(allPermissions.toTypedArray(), arguments.getInt(REQUEST_CODE))
            return
        }

        // 在 Android 10 的机型上，需要先申请前台定位权限，再申请后台定位权限
        beginRequest(activity, locationPermission, object : OnPermissionListener {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (!all || !isAdded) return
                requestPermissions(allPermissions.toTypedArray(), arguments.getInt(REQUEST_CODE))
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                if (!isAdded) return
                // 如果申请的权限里面只包含定位相关的权限，那么就直接回调失败
                if (permissions.size == allPermissions.size - 1) {
                    val grantResults = IntArray(allPermissions.size)
                    Arrays.fill(grantResults, PackageManager.PERMISSION_DENIED)
                    onRequestPermissionsResult(arguments.getInt(REQUEST_CODE), allPermissions.toTypedArray(), grantResults)
                    return
                }
                // 如果还有其他类型的权限组就继续申请
                requestPermissions(allPermissions.toTypedArray(), arguments.getInt(REQUEST_CODE))
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val arguments = arguments
        val activity = activity
        if (activity == null || arguments == null || mListener == null || requestCode != arguments.getInt(REQUEST_CODE)) {
            return
        }
        val callBack: OnPermissionListener = mListener!!
        mListener = null
        for (i in permissions.indices) {
            val permission = permissions[i]
            if (PermissionUtils.isSpecialPermission(permission)) {
                // 如果这个权限是特殊权限，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission)
                continue
            }
            // 重新检查 Android 11 后台定位权限
            if (PermissionUtils.isAndroid11 && Permission.ACCESS_BACKGROUND_LOCATION == permission) {
                // 这个权限是后台定位权限并且当前手机版本是 Android 11 及以上，那么就需要重新进行检测
                // 因为只要申请这个后台定位权限，grantResults 数组总对这个权限申请的结果返回 -1（拒绝）
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission)
                continue
            }
            // 重新检查 Android 10.0 的三个新权限
            if (!PermissionUtils.isAndroid10 && (Permission.ACCESS_BACKGROUND_LOCATION == permission || Permission.ACTIVITY_RECOGNITION == permission || Permission.ACCESS_MEDIA_LOCATION == permission)) {
                // 如果当前版本不符合最低要求，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission)
                continue
            }
            // 重新检查 Android 9.0 的一个新权限
            if (!PermissionUtils.isAndroid9 && Permission.ACCEPT_HANDOVER == permission) {
                // 如果当前版本不符合最低要求，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission)
                continue
            }
            // 重新检查 Android 8.0 的两个新权限
            if (!PermissionUtils.isAndroid8 && (Permission.ANSWER_PHONE_CALLS == permission || Permission.READ_PHONE_NUMBERS == permission)) {
                // 如果当前版本不符合最低要求，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission)
            }
        }

        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.delete(requestCode)
        // 将 Fragment 从 Activity 移除
        detachActivity(activity)

        // 获取已授予的权限
        val grantedPermission = PermissionUtils.getGrantedPermissions(permissions, grantResults)

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermission.size == permissions.size) {
            // 代表申请的所有的权限都授予了
            XPermissions.permissionInterceptor.grantedPermissions(activity, callBack, grantedPermission, true)
            return
        }

        // 获取被拒绝的权限
        val deniedPermission = PermissionUtils.getDeniedPermissions(permissions, grantResults)

        // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
        XPermissions.permissionInterceptor.deniedPermissions(activity, callBack, deniedPermission, PermissionUtils.isPermissionPermanentDenied(activity, deniedPermission))

        // 证明还有一部分权限被成功授予，回调成功接口
        if (grantedPermission.isNotEmpty()) {
            XPermissions.permissionInterceptor.grantedPermissions(activity, callBack, grantedPermission, false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val activity = activity
        val arguments = arguments
        if (activity == null || arguments == null || requestCode != arguments.getInt(REQUEST_CODE) || mDangerousRequest) {
            return
        }
        mDangerousRequest = true
        // 需要延迟执行，不然有些华为机型授权了但是获取不到权限
        activity.window.decorView.postDelayed(this, 300)
    }

    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded) {
            return
        }
        // 请求其他危险权限
        requestDangerousPermission()
    }

    companion object {
        /** 请求的权限组  */
        private const val REQUEST_PERMISSIONS = "request_permissions"

        /** 请求码（自动生成） */
        private const val REQUEST_CODE = "request_code"

        /** 权限请求码存放集合  */
        private val REQUEST_CODE_ARRAY = SparseBooleanArray()

        /**
         * 开启权限申请
         */
        @JvmStatic
        fun beginRequest(activity: FragmentActivity, permissions: ArrayList<String>, listener: OnPermissionListener) {
            val fragment = PermissionFragment()
            val bundle = Bundle()
            var requestCode: Int
            // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
            do {
                requestCode = PermissionUtils.randomRequestCode
            } while (REQUEST_CODE_ARRAY[requestCode])
            // 标记这个请求码已经被占用
            REQUEST_CODE_ARRAY.put(requestCode, true)
            bundle.putInt(REQUEST_CODE, requestCode)
            bundle.putStringArrayList(REQUEST_PERMISSIONS, permissions)
            fragment.arguments = bundle
            // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
            fragment.retainInstance = true
            // 设置权限回调监听
            fragment.setCallBack(listener)
            // 绑定到 Activity 上面
            fragment.attachActivity(activity)
        }
    }
}