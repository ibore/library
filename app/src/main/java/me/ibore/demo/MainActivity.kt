package me.ibore.demo

import android.os.Bundle
import android.view.View
import me.ibore.base.XActivity
import me.ibore.demo.databinding.ActivityMainBinding

class MainActivity : XActivity<ActivityMainBinding>() {

    override fun ActivityMainBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        titleBar.ivBack.visibility = View.GONE
        titleBar.tvTitle.text = "首页"
    }

    override fun onBindData() {

    }

}