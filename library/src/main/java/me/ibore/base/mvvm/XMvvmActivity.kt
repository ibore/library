package me.ibore.base.mvvm

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import me.ibore.base.XActivity
import me.ibore.utils.ReflexUtils

abstract class XMvvmActivity<VB : ViewBinding, VM : ViewModel> : XActivity<VB>() {

    protected val mViewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        ReflexUtils.viewModel(javaClass, this)
    }



}