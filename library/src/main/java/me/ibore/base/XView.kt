package me.ibore.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup

interface XView {

    fun onInflaterView(container: ViewGroup?): View

    fun onBindView(bundle: Bundle?, savedInstanceState: Bundle?)

    fun onBindData()

    fun getXActivity(): XActivity<*>

    fun showLoading()

    fun showContent()

    fun showError()

    fun showEmpty()
}