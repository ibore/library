package me.ibore.base

import io.reactivex.subscribers.DisposableSubscriber
import me.ibore.exception.XException
import me.ibore.loading.XLoading
import me.ibore.loading.XLoadingHelper

abstract class XSubscriber<T> : DisposableSubscriber<T>, XListener<T> {

    private var XLoadingHelper: XLoadingHelper
    var tag: Any? = null
        get() {
            if (null == field) field = XLoadingHelper.tag
            return field
        }
        set(value) {
            field = value
            XLoadingHelper.tag = value
        }

    constructor() {
        XLoadingHelper = XLoadingHelper()
    }

    constructor(@XLoading xStatus: Int) {
        XLoadingHelper = XLoadingHelper(xStatus)
    }

    constructor(tag: Any, @XLoading xStatus: Int) {
        XLoadingHelper = XLoadingHelper(tag, xStatus)
    }

    override fun onStart() {
        super.onStart()
        XLoadingHelper.showLoading(this)
    }

    final override fun onNext(t: T) {
        XLoadingHelper.dismiss()
        onSuccess(t)
    }

    override fun onComplete() {}

    final override fun onError(e: Throwable) {
        if (e is XException) onFailure(e)
        else onFailure(XException(e))
    }

    override fun onFailure(e: XException) {
        XLoadingHelper.failure(e)
    }

}