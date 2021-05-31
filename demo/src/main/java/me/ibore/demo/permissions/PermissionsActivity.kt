package me.ibore.demo.permissions

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.base.XActivity
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityPermissionsBinding
import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.TitleItem
import me.ibore.permissions.OnPermissionListener
import me.ibore.permissions.Permission
import me.ibore.permissions.PermissionInterceptor
import me.ibore.permissions.XPermissions
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.utils.DialogUtils
import me.ibore.utils.GsonUtils
import me.ibore.utils.LogUtils
import me.ibore.utils.ToastUtils


class PermissionsActivity : BaseActivity<ActivityPermissionsBinding>() {

    private val adapter = TitleAdapter()

    override fun ActivityPermissionsBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        XPermissions.init(true, object : PermissionInterceptor {
            override fun requestPermissions(activity: FragmentActivity, listener: OnPermissionListener, permissions: List<String>) {
                LogUtils.d(GsonUtils.toJson(permissions))
                /*DialogUtils.showAlert(activity as XActivity<*>, "权限请求", GsonUtils.toJson(permissions), negativeListener = {
                    deniedPermissions(activity, listener, permissions, false)
                }, positiveListener = {
                    super.requestPermissions(activity, listener, permissions)
                })*/
                super.requestPermissions(activity, listener, permissions)
            }

            override fun grantedPermissions(activity: FragmentActivity, listener: OnPermissionListener, permissions: List<String>, all: Boolean) {
                LogUtils.d(GsonUtils.toJson(permissions))
                super.grantedPermissions(activity, listener, permissions, all)
            }

            override fun deniedPermissions(activity: FragmentActivity, listener: OnPermissionListener, permissions: List<String>, never: Boolean) {
                LogUtils.d(GsonUtils.toJson(permissions))
                if (XPermissions.isPermissionPermanentDenied(activity, permissions)) {
                    DialogUtils.showAlert(activity as XActivity<*>, "权限请求", "当前有永远拒绝的权限" + GsonUtils.toJson(permissions), negativeListener = {
                        super.deniedPermissions(activity, listener, permissions, never)
                    },
                        positiveListener = {
                            XPermissions.startPermissionActivity(getXActivity(), permissions)
                        })
                }
            }
        })
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 2)
        mBinding.recyclerView.adapter = adapter

    }

    override fun onBindData() {
        Manifest.permission.BIND_INPUT_METHOD
        adapter.addData(TitleItem("拍照权限") {
            XPermissions.with(getXActivity())
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        ToastUtils.showShort("获取拍照权限成功")
                    }
                })
        })
        adapter.addData(TitleItem("录音权限") {
            XPermissions.with(getXActivity())
                .permission(Permission.RECORD_AUDIO)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("获取录音权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("日历权限") {
            XPermissions.with(getXActivity())
                .permission(*Permission.GROUP_CALENDAR)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("获取日历权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("联系人权限") {
            XPermissions.with(getXActivity())
                .permission(*Permission.GROUP_CONTACTS)
                .request(object : OnPermissionListener {
                    override fun onDenied(permissions: List<String>, never: Boolean) {
                        LogUtils.d(GsonUtils.toJson(permissions))
                    }

                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("获取联系人权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("存储卡权限") {
            XPermissions.with(getXActivity())
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        LogUtils.d(GsonUtils.toJson(permissions))
                        if (all) {
                            ToastUtils.showShort("获取存储卡权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("前台定位权限") {
            XPermissions.with(getXActivity())
                .permission(*Permission.GROUP_LOCATION)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("前台获取定位权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("后台定位权限") {
            XPermissions.with(getXActivity())
                .permission(*Permission.GROUP_LOCATION_BACKGROUND)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("后台获取定位权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("安装包权限") {
            XPermissions.with(getXActivity())
                .permission(Permission.REQUEST_INSTALL_PACKAGES)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("获取安装包权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("悬浮窗权限") {
            XPermissions.with(getXActivity())
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("获取悬浮窗权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("通知栏权限") {
            XPermissions.with(getXActivity())
                .permission(Permission.NOTIFICATION_SERVICE)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("获取通知栏权限成功")
                        }
                    }
                })
        })
        adapter.addData(TitleItem("系统设置权限") {
            XPermissions.with(getXActivity())
                .permission(Permission.WRITE_SETTINGS)
                .request(object : OnPermissionListener {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all) {
                            ToastUtils.showShort("获取系统设置权限成功")
                        }
                    }
                })
        })
    }

    class Adapter : BindingAdapter<ItemActivityBinding, String>() {
        override fun ItemActivityBinding.onBindHolder(
            holder: RecyclerHolder,
            data: String,
            dataPosition: Int
        ) {
            tvTitle.text = data
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == XPermissions.REQUEST_CODE) {
            ToastUtils.showShort("检测到你刚刚从权限设置界面返回回来")
        }
    }
}