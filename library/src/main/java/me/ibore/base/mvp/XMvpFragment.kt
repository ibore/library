package me.ibore.base.mvp

import androidx.viewbinding.ViewBinding
import me.ibore.base.XFragment
import me.ibore.utils.ReflexUtils

@Suppress("UNCHECKED_CAST")
abstract class XMvpFragment<VB : ViewBinding, P : XMvpPresenter<*>> : XFragment<VB>(), XMvpView<VB> {

    protected open val mPresenter: P by lazy(mode = LazyThreadSafetyMode.NONE) {
        ReflexUtils.presenter(javaClass)
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