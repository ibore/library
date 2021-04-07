package me.ibore.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface XView<VB : ViewBinding> {

    fun VB.onBindView(bundle: Bundle?, savedInstanceState: Bundle?)

    fun onBindData()

    fun getXActivity(): XActivity<*>

    fun showLoading()

    fun showContent()

    fun showError()

    fun showEmpty()
}