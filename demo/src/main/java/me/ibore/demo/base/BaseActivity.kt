package me.ibore.demo.base

import androidx.viewbinding.ViewBinding
import me.ibore.base.XActivity
import me.ibore.demo.R
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.utils.BarUtils

abstract class BaseActivity<VB : ViewBinding> : XActivity<VB>() {

    protected lateinit var mTitleBar : TitleBarBinding

    override fun onBindConfig() {
        super.onBindConfig()
        BarUtils.setStatusBarLightMode(this, true)
        //binding.root.setBackgroundResource(R.color.bg_f4f5f7)
    }

    fun setTitleBar(titleBarBinding: TitleBarBinding, title: CharSequence?) {
        mTitleBar = titleBarBinding
        mTitleBar.ivBack.setOnClickListener { onBackPressed() }
        mTitleBar.tvTitle.text = title
    }
}