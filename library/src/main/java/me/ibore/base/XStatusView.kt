package me.ibore.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface XStatusView<VB : ViewBinding> : XView<VB> {

    fun showLoading()

    fun showContent()

    fun showError()

    fun showEmpty()

    fun showDialog()

    fun dismissDialog()

}