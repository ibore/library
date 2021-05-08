package me.ibore.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import me.ibore.utils.Utils

/**
 * Android 危险权限请求类
 */
class XPermissions private constructor(private val mActivity: FragmentActivity) {

    private val mPermissions: MutableList<String> = ArrayList()

    /**
     * 添加权限
     */
    fun permission(vararg permissions: String): XPermissions {
        mPermissions.addAll(permissions)
        return this
    }

    fun permission(permissions: List<String>): XPermissions {
        mPermissions.addAll((permissions))
        return this
    }

    /**
     * 请求权限
     */
    fun request(listener: OnPermissionListener) {
        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        if ((mActivity.isFinishing || mActivity.isDestroyed)) {
            return
        }
        // 必须要传入权限或者权限组才能申请权限
        if (mPermissions.isEmpty()) {
            if (debugMode) {
                throw IllegalArgumentException("The requested permission cannot be empty")
            }
            return
        }
        if (debugMode) {
            // 检查申请的存储权限是否符合规范
            PermissionUtils.checkStoragePermission(mActivity, mPermissions)
            // 检查申请的定位权限是否符合规范
            PermissionUtils.checkLocationPermission(mPermissions)
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionUtils.checkTargetSdkVersion(mActivity, mPermissions)
        }

        // 优化所申请的权限列表
        PermissionUtils.optimizeDeprecatedPermission(mPermissions)
        if (debugMode) {
            // 检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissionManifest(mActivity, mPermissions)
        }
        if (PermissionUtils.isGrantedPermissions(mActivity, mPermissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            listener.onGranted(mPermissions, true)
            return
        }

        // 申请没有授予过的权限
        permissionInterceptor.requestPermissions(mActivity, listener, mPermissions)
    }

    companion object {
        /** 权限设置页跳转请求码  */
        const val REQUEST_CODE: Int = 1024 + 1

        /** 权限请求拦截器  */
        @JvmStatic
        var permissionInterceptor: PermissionInterceptor = object : PermissionInterceptor {}
            private set

        /** 调试模式  */
        @JvmStatic
        var debugMode: Boolean =
            (Utils.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            private set

        fun init(debugMode: Boolean, permissionInterceptor: PermissionInterceptor) {
            this.debugMode = debugMode
            this.permissionInterceptor = permissionInterceptor
        }

        /**
         * 设置请求的对象
         *
         * @param activity 当前 Activity，也可以传入栈顶的 Activity
         */
        @JvmStatic
        fun with(activity: FragmentActivity): XPermissions {
            return XPermissions(activity)
        }

        @JvmStatic
        fun with(fragment: Fragment): XPermissions {
            return with(fragment.requireActivity())
        }

        /**
         * 判断一个或多个权限是否全部授予了
         */
        @JvmStatic
        fun isGrantedPermission(context: Context, vararg permissions: String): Boolean {
            return isGrantedPermission(context, PermissionUtils.asArrayList(*permissions))
        }

        @JvmStatic
        fun isGrantedPermission(context: Context, permissions: List<String>): Boolean {
            return PermissionUtils.isGrantedPermissions(context, permissions)
        }

        /**
         * 获取没有授予的权限
         */
        @JvmStatic
        fun getDeniedPermissions(context: Context, permissions: Array<String>): List<String> {
            return getDeniedPermissions(context, PermissionUtils.asArrayList(*(permissions)))
        }

        @JvmStatic
        fun getDeniedPermissions(context: Context, permissions: List<String>): List<String> {
            return PermissionUtils.getDeniedPermissions(context, permissions)
        }

        /**
         * 判断一个或多个权限是否被永久拒绝了（注意不能在请求权限之前调用，应该在 [OnPermissionListener.onDenied] 方法中调用）
         */
        @JvmStatic
        fun isPermissionPermanentDenied(activity: Activity, vararg permissions: String): Boolean {
            return PermissionUtils.isPermissionPermanentDenied(
                activity,
                PermissionUtils.asArrayList(*permissions)
            )
        }

        @JvmStatic
        fun isPermissionPermanentDenied(activity: Activity, permissions: List<String>): Boolean {
            return PermissionUtils.isPermissionPermanentDenied(activity, permissions)
        }

        /**
         * 判断某个权限是否是特殊权限
         */
        @JvmStatic
        fun isSpecialPermission(permission: String): Boolean {
            return PermissionUtils.isSpecialPermission(permission)
        }

        @JvmStatic
        fun startApplicationDetails(activity: Activity) {
            activity.startActivityForResult(
                PermissionSettingPage.getApplicationDetailsIntent(
                    activity
                ), REQUEST_CODE
            )
        }

        fun startApplicationDetails(fragment: Fragment) {
            val activity: FragmentActivity = fragment.activity ?: return
            fragment.startActivityForResult(
                PermissionSettingPage.getApplicationDetailsIntent(
                    activity
                ), REQUEST_CODE
            )
        }

        /**
         * 跳转到应用权限设置页
         *
         * @param permissions 没有授予或者被拒绝的权限组
         */

        @JvmStatic
        fun startPermissionActivity(activity: Activity, vararg permissions: String) {
            startPermissionActivity(activity, PermissionUtils.asArrayList(*permissions))
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity, permissions: List<String>) {
            activity.startActivityForResult(
                PermissionSettingPage.getSmartPermissionIntent(
                    activity,
                    permissions
                ), REQUEST_CODE
            )
        }

        @JvmStatic
        fun startPermissionActivity(fragment: Fragment, vararg permissions: String) {
            startPermissionActivity(fragment, PermissionUtils.asArrayList(*permissions))
        }

        @JvmStatic
        fun startPermissionActivity(fragment: Fragment, permissions: List<String>) {
            val activity: FragmentActivity = fragment.activity ?: return
            fragment.startActivityForResult(
                PermissionSettingPage.getSmartPermissionIntent(
                    activity,
                    permissions
                ), REQUEST_CODE
            )
        }

    }
}