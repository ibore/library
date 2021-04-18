package me.ibore.demo.view

import android.os.Bundle
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityWheelBinding
import me.ibore.demo.databinding.TitleBarBinding

class WheelActivity : BaseActivity<ActivityWheelBinding>() {

    override fun ActivityWheelBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.wheelView.datas = arrayListOf("测试1", "测试2", "测试3", "测试4", "测试5")
    }

    override fun onBindData() {

    }

}