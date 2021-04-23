package me.ibore.base

import io.reactivex.observers.DisposableObserver
import me.ibore.exception.XException
import me.ibore.loading.XLoading
import me.ibore.loading.XLoadingHelper

abstract class XObserver<T> : DisposableObserver<T>, XListener<T> {

    private var helper: XLoadingHelper

    internal var tag: Any? = null
        set(value) {
            field = value
            helper.tag = value
        }

    constructor() {
        helper = XLoadingHelper()
    }

    constructor(@XLoading xLoading: Int) {
        helper = XLoadingHelper(xLoading)
    }

    constructor(tag: Any, @XLoading xLoading: Int) {
        helper = XLoadingHelper(tag, xLoading)
    }

    override fun onStart() {
        helper.showLoading(this)
    }

    final override fun onNext(t: T) {
        helper.dismiss()
        onSuccess(t)
    }

    override fun onComplete() {
        helper.complete()
    }

    final override fun onError(e: Throwable) {
        if (e is XException) onFailure(e)
        else {
            onFailure(XException(e.message))
        }
    }

    override fun onFailure(e: XException) {
        helper.failure(e)
    }

}