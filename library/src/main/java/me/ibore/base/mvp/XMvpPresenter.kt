package me.ibore.base.mvp

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import me.ibore.base.XObserver
import me.ibore.base.XSubscriber
import me.ibore.utils.DisposablesUtils

abstract class XMvpPresenter<V : XMvpView<*>> {

    protected lateinit var mView: V

    fun getView(): V = mView

    @Suppress("UNCHECKED_CAST")
    fun onAttach(view: XMvpView<*>) {
        this.mView = view as V
    }

    fun onDetach() {
        DisposablesUtils.clear(this)
    }

    protected fun addDisposable(disposable: Disposable): Disposable {
        return DisposablesUtils.add(this, disposable)
    }

    protected fun <T> addDisposable(observable: Observable<T>, observer: XObserver<T>): Disposable {
        return DisposablesUtils.add(this, observable, observer)
    }

    protected fun <T> addDisposable(flowable: Flowable<T>, subscriber: XSubscriber<T>): Disposable {
        return DisposablesUtils.add(this, flowable, subscriber)
    }

    protected fun removeDisposable(disposable: Disposable): Boolean {
        return DisposablesUtils.remove(this, disposable)
    }

}

