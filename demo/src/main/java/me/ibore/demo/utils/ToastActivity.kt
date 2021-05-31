package me.ibore.demo.utils

import android.os.Bundle
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.R
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityListBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.TitleItem
import me.ibore.utils.ToastUtils

class ToastActivity : BaseActivity<ActivityListBinding>() {

    private var adapter = TitleAdapter()

    override fun ActivityListBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        mBinding.recyclerView.adapter = adapter
    }

    override fun onBindData() {


        adapter.addData(TitleItem("打印普通字符串") {
            ToastUtils.setDefault(
                ToastUtils.make().setMode(ToastUtils.MODE.DARK).setGravity(Gravity.CENTER, 0, 0)
            )
            ToastUtils.showShort("你好")
        })

        adapter.addData(TitleItem("打印普通字符串") {
            ToastUtils.setDefault(
                ToastUtils.make().setMode(ToastUtils.MODE.LIGHT).setGravity(Gravity.CENTER, 0, 0)
            )
            ToastUtils.showLong("你好")
        })


    }
}