package me.ibore.demo

import android.os.Bundle
import me.ibore.base.XActivity
import me.ibore.demo.databinding.ActivityMainBinding

class MainActivity : XActivity<ActivityMainBinding>() {

    override fun ActivityMainBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        tvHelloWorld.text = "你好，世界！"
    }

    override fun onBindData() {

    }

}