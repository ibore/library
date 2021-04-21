package me.ibore.demo.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.ActivityAdapter
import me.ibore.demo.audio.AudioActivity
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityMainBinding
import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.dialog.DialogActivity
import me.ibore.demo.http.HttpActivity
import me.ibore.demo.image.ImageActivity
import me.ibore.demo.model.ActivityItem
import me.ibore.demo.permissions.PermissionsActivity
import me.ibore.demo.qrcode.QrCodeActivity
import me.ibore.demo.recycler.RecyclerActivity
import me.ibore.demo.refresh.RefreshActivity
import me.ibore.demo.status.StatusActivity
import me.ibore.demo.theme.ThemeActivity
import me.ibore.demo.update.UpdateActivity
import me.ibore.demo.utils.UtilsActivity
import me.ibore.demo.video.VideoActivity
import me.ibore.demo.view.ViewActivity
import me.ibore.demo.web.WebActivity
import me.ibore.recycler.holder.BindingItemHolder

@SuppressLint("NonConstantResourceId")
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var adapter = ActivityAdapter()

    override fun ActivityMainBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), "IBORE LIBRARY DEMO")
        mTitleBar.ivBack.visibility = View.GONE
        
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        mBinding.recyclerView.adapter = adapter

    }

    override fun onBindData() {
        adapter.setHeaderHolder(object : BindingItemHolder<ItemActivityBinding>() {
            override fun ItemActivityBinding.onBindingHolder(holder: BindingHolder<ItemActivityBinding>) {
                tvTitle.setText("头布局")
            }
        })
        adapter.addData(ActivityItem("状  态  栏", StatusActivity::class.java))
        adapter.addData(ActivityItem("网络请求", HttpActivity::class.java))
        adapter.addData(ActivityItem("对  话  框", DialogActivity::class.java))
        adapter.addData(ActivityItem("图片相关", ImageActivity::class.java))
        adapter.addData(ActivityItem("自定义View", ViewActivity::class.java))
        adapter.addData(ActivityItem("自动更新", UpdateActivity::class.java))
        adapter.addData(ActivityItem("二  维  码", QrCodeActivity::class.java))
        adapter.addData(ActivityItem("RecyclerView", RecyclerActivity::class.java))
        adapter.addData(ActivityItem("音频处理", AudioActivity::class.java))
        adapter.addData(ActivityItem("视频播放", VideoActivity::class.java))
        adapter.addData(ActivityItem("应用主题", ThemeActivity::class.java))
        adapter.addData(ActivityItem("下拉刷新", RefreshActivity::class.java))
        adapter.addData(ActivityItem("WebView", WebActivity::class.java))
        adapter.addData(ActivityItem("应用权限", PermissionsActivity::class.java))
        adapter.addData(ActivityItem("工  具  类", UtilsActivity::class.java))

    }

}
