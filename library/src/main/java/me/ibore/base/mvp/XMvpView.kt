package me.ibore.base.mvp

import me.ibore.base.XActivity

interface XMvpView<P : XMvpPresenter<*>> {

    fun getXActivity(): XActivity<*>

    fun P.onBindData()

}