package me.ibore.demo.recycler

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.ActivityAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityListBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.ActivityItem

class RecyclerActivity : BaseActivity<ActivityListBinding>() {

    private var adapter = ActivityAdapter()

    override fun ActivityListBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(titleBar), bundle?.getString("title"))
        recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        recyclerView.adapter = adapter
    }

    override fun onBindData() {

        adapter.addData(ActivityItem("悬浮吸顶", StickyViewActivity::class.java))
        adapter.addData(ActivityItem("滑动动画", RecyclerAnimActivity::class.java))

    }

}