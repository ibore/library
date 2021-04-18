package me.ibore.rxbus

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.operators.flowable.FlowableInternalHelper
import org.reactivestreams.Subscription


/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/05/08
 * desc  :
</pre> *
 */
object FlowableUtils {

    fun <T> subscribe(flowable: Flowable<T>?,
                      onNext: Consumer<T>?,
                      onError: Consumer<Throwable>): Disposable {
        return subscribe(flowable,
                onNext, onError,
                Functions.EMPTY_ACTION,
                FlowableInternalHelper.RequestMax.INSTANCE
        )
    }

    private fun <T> subscribe(flowable: Flowable<T>?,
                              onNext: Consumer<T>?,
                              onError: Consumer<Throwable>?,
                              onComplete: Action?,
                              onSubscribe: Consumer<in Subscription?>?): Disposable {
        ObjectHelper.requireNonNull(flowable, "flowable is null")
        ObjectHelper.requireNonNull(onNext, "onNext is null")
        ObjectHelper.requireNonNull(onError, "onError is null")
        ObjectHelper.requireNonNull(onComplete, "onComplete is null")
        ObjectHelper.requireNonNull(onSubscribe, "onSubscribe is null")
        val ls = MyLambdaSubscriber<T>(onNext, onError, onComplete, onSubscribe)
        flowable!!.subscribe(ls)
        return ls
    }
}