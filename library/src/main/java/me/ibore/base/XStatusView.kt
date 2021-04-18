package me.ibore.base

import androidx.viewbinding.ViewBinding

interface XStatusView<VB : ViewBinding> : XView<VB> {

    fun showLoading()

    fun showContent()

    fun showError()

    fun showEmpty()

    fun showDialog()

    fun dismissDialog()

}