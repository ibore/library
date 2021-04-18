package me.ibore.demo.utils

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.ActivityAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityListBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.ActivityItem

class UtilsActivity : BaseActivity<ActivityListBinding>() {

    private var adapter = ActivityAdapter()

    override fun ActivityListBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        mBinding.recyclerView.adapter = adapter
    }

    override fun onBindData() {
        adapter.addData(ActivityItem("日志打印", LogActivity::class.java))
        adapter.addData(ActivityItem("吐司", ToastActivity::class.java))
    }
}