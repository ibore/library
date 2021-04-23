package me.ibore.demo.permissions

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.base.XActivity
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityPermissionsBinding
import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.permissions.PermissionInterceptor
import me.ibore.permissions.OnPermissionListener
import me.ibore.permissions.Permission
import me.ibore.permissions.XPermissions
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.listener.OnItemClickListener
import me.ibore.utils.DialogUtils
import me.ibore.utils.GsonUtils
import me.ibore.utils.LogUtils
import me.ibore.utils.ToastUtils


class PermissionsActivity : BaseActivity<ActivityPermissionsBinding>() {

    private val adapter = Adapter()

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
                    }, positiveListener = {
                        XPermissions.startPermissionActivity(getXActivity(), permissions)
                    })
                }
            }
        })
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 2)
        mBinding.recyclerView.adapter = adapter
        adapter.onItemClickListener = object : OnItemClickListener<RecyclerHolder, String> {

            override fun onItemClick(
                holder: RecyclerHolder, data: String, dataPosition: Int
            ) {
                when (dataPosition) {
                    0 -> {
                        XPermissions.with(getXActivity())
                            .permission(Permission.CAMERA)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    ToastUtils.showShort("获取拍照权限成功")
                                }
                            })
                    }
                    1 -> {
                        XPermissions.with(getXActivity())
                            .permission(Permission.RECORD_AUDIO)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    if (all) {
                                        ToastUtils.showShort("获取录音权限成功")
                                    }
                                }
                            })
                    }
                    2 -> {
                        XPermissions.with(getXActivity())
                            .permission(*Permission.GROUP_CALENDAR)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    if (all) {
                                        ToastUtils.showShort("获取日历权限成功")
                                    }
                                }
                            })
                    }
                    3 -> {
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
                    }
                    4 -> {
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
                    }
                    5 -> {
                        XPermissions.with(getXActivity())
                            .permission(Permission.ACCESS_BACKGROUND_LOCATION)
                            //.permission(*Permission.Group.LOCATION)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    if (all) {
                                        ToastUtils.showShort("获取定位权限成功")
                                    }
                                }
                            })
                    }
                    6 -> {
                        XPermissions.with(getXActivity())
                            .permission(Permission.REQUEST_INSTALL_PACKAGES)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    if (all) {
                                        ToastUtils.showShort("获取安装包权限成功")
                                    }
                                }
                            })
                    }
                    7 -> {
                        XPermissions.with(getXActivity())
                            .permission(Permission.SYSTEM_ALERT_WINDOW)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    if (all) {
                                        ToastUtils.showShort("获取悬浮窗权限成功")
                                    }
                                }
                            })
                    }
                    8 -> {
                        XPermissions.with(getXActivity())
                            .permission(Permission.NOTIFICATION_SERVICE)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    if (all) {
                                        ToastUtils.showShort("获取通知栏权限成功")
                                    }
                                }
                            })
                    }
                    9 -> {
                        XPermissions.with(getXActivity())
                            .permission(Permission.WRITE_SETTINGS)
                            .request(object : OnPermissionListener {
                                override fun onGranted(permissions: List<String>, all: Boolean) {
                                    if (all) {
                                        ToastUtils.showShort("获取系统设置权限成功")
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

    override fun onBindData() {
        adapter.addData("拍照权限")
        adapter.addData("录音权限")
        adapter.addData("日历权限")
        adapter.addData("联系人权限")
        adapter.addData("存储卡权限")
        adapter.addData("定位权限")
        adapter.addData("安装包权限")
        adapter.addData("悬浮窗权限")
        adapter.addData("通知栏权限")
        adapter.addData("系统设置权限")
    }

    class Adapter : BindingAdapter<ItemActivityBinding, String>() {
        override fun ItemActivityBinding.onBindHolder(holder: RecyclerHolder, data: String, dataPosition: Int) {
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