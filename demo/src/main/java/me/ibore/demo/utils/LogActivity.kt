package me.ibore.demo.utils

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityListBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.TitleItem
import me.ibore.utils.LogUtils

class LogActivity : BaseActivity<ActivityListBinding>() {

    private var adapter = TitleAdapter()

    override fun ActivityListBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        mBinding.recyclerView.adapter = adapter
    }

    override fun onBindData() {
        LogUtils.d(LogUtils.currentLogFilePath)
        LogUtils.file("测试")

        adapter.addData(TitleItem("打印普通字符串") {
            LogUtils.d("打印普通字符串")
        })


    }
}