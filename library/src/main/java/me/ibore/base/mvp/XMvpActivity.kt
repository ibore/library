package me.ibore.base.mvp

import androidx.viewbinding.ViewBinding
import me.ibore.base.XActivity
import me.ibore.utils.BindingUtils

abstract class XMvpActivity<VB : ViewBinding, P : XMvpPresenter<*>> : XActivity<VB>(), XMvpView<VB> {

    protected open val mPresenter: P by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingUtils.reflexPresenter(javaClass)
    }

    override fun onBindConfig() {
        super.onBindConfig()
        mPresenter.onAttach(this)
    }

    override fun onUnBindConfig() {
        super.onUnBindConfig()
        mPresenter.onDetach()
    }
    
}