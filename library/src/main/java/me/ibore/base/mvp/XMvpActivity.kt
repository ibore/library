package me.ibore.base.mvp

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import me.ibore.base.XActivity

abstract class XMvpActivity<VB : ViewBinding, P : XMvpPresenter<out XMvpView<P>>> : XActivity<VB>(),
    XMvpView<P> {

    override fun VB.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun P.onBindData() {

    }
}