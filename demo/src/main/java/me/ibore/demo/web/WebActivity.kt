package me.ibore.demo.web

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityWebBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.TitleItem
import me.ibore.xweb.XWebActivity


class WebActivity : BaseActivity<ActivityWebBinding>() {

    private var adapter = TitleAdapter()

    override fun ActivityWebBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(titleBar), bundle?.getString("title"))
        recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        recyclerView.adapter = adapter
    }

    override fun onBindData() {

        adapter.addData(TitleItem("普通网页") {
            XWebActivity.startActivity(
                getXActivity(),
                XWebActivity.Builder("百度一下", "https://www.baidu.com/")
            )
        })

        adapter.addData(TitleItem("本地网页") {
            XWebActivity.startActivity(
                getXActivity(),
                XWebActivity.Builder("本地网页", "file:///android_asset/android.html")
            )
        })

        adapter.addData(TitleItem("搜狗地图") {
            XWebActivity.startActivity(
                getXActivity(),
                XWebActivity.Builder("搜狗地图", "https://map.sogou.com/m/webapp/m.html")
            )
        })

        adapter.addData(TitleItem("视频网站") {
            XWebActivity.startActivity(
                getXActivity(),
                XWebActivity.Builder("视频网站", "https://m.bilibili.com/")
            )
        })


    }
}