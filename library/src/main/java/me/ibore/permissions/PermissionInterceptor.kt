package me.ibore.permissions

import androidx.fragment.app.FragmentActivity
import java.util.*

/**
 * 权限请求拦截器
 */
interface PermissionInterceptor {
    /**
     * 权限申请拦截，可在此处先弹 Dialog 再申请权限
     */
    fun requestPermissions(activity: FragmentActivity, listener: OnPermissionListener, permissions: List<String>) {
        PermissionFragment.beginRequest(activity, ArrayList(permissions), listener)
    }

    /**
     * 权限授予回调拦截，参见 [OnPermissionListener.onGranted]
     */
    fun grantedPermissions(activity: FragmentActivity, listener: OnPermissionListener, permissions: List<String>, all: Boolean) {
        listener.onGranted(permissions, all)
    }

    /**
     * 权限拒绝回调拦截，参见 [OnPermissionListener.onDenied]
     */
    fun deniedPermissions(activity: FragmentActivity, listener: OnPermissionListener, permissions: List<String>, never: Boolean) {
        listener.onDenied(permissions, never)
    }
}