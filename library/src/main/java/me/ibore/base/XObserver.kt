package me.ibore.base

import io.reactivex.observers.DisposableObserver
import me.ibore.exception.XException
import me.ibore.loading.XLoading
import me.ibore.loading.XLoadingHelper

abstract class XObserver<T> : DisposableObserver<T>, XListener<T> {

    private var XLoadingHelper: XLoadingHelper
    internal var tag: Any? = null
        set(value) {
            field = value
            XLoadingHelper.tag = value
        }

    constructor() {
        XLoadingHelper = XLoadingHelper()
    }

    constructor(@XLoading xLoading: Int) {
        XLoadingHelper = XLoadingHelper(xLoading)
    }

    constructor(tag: Any, @XLoading xLoading: Int) {
        XLoadingHelper = XLoadingHelper(tag, xLoading)
    }

    override fun onStart() {
        XLoadingHelper.showLoading(this)
    }

    final override fun onNext(t: T) {
        XLoadingHelper.dismiss()
        onSuccess(t)
    }

    override fun onComplete() {
        XLoadingHelper.complete()
    }

    final override fun onError(e: Throwable) {
        if (e is XException) onFailure(e)
        else {
            onFailure(XException(e.message))
        }
    }

    override fun onFailure(e: XException) {
        XLoadingHelper.failure(e)
    }

}