package me.ibore.demo.view

import android.os.Bundle
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityPickerBinding
import me.ibore.demo.databinding.TitleBarBinding

class PickerActivity : BaseActivity<ActivityPickerBinding>() {

    override fun ActivityPickerBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.wheelView.currentYear = 2019
        mBinding.wheelView.currentMonth = 9
    }

    override fun onBindData() {

    }

}