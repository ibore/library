package me.ibore.demo.status

import android.graphics.Color
import android.os.Bundle
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityStatusBinding
import me.ibore.demo.databinding.TitleBarBinding

class StatusActivity : BaseActivity<ActivityStatusBinding>() {

    override fun ActivityStatusBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        titleBar.setBackgroundColor(Color.TRANSPARENT)
        root.clipChildren = false
        root.clipToPadding = false
        nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

        }
    }

    override fun onBindData() {

    }


}