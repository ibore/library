package me.ibore.utils

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import me.ibore.base.XObserver
import me.ibore.base.XSubscriber
import java.util.*

object DisposablesUtils {

    private val mDisposables = WeakHashMap<Any, CompositeDisposable>()

    fun add(tag: Any, disposable: Disposable): Disposable {
        var compositeDisposable: CompositeDisposable? = mDisposables[tag]
        if (null == compositeDisposable) {
            compositeDisposable = CompositeDisposable()
            mDisposables[tag] = compositeDisposable
        }
        compositeDisposable.add(disposable)
        return disposable
    }

    fun <T> add(tag: Any, observable: Observable<T>, observer: DisposableObserver<T>): Disposable {
        if (observer is XObserver) {
            observer.tag = tag
        }
        val disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer) as Disposable
        return add(tag, disposable)
    }

    fun <T> add(tag: Any, flowable: Flowable<T>, subscriber: DisposableSubscriber<T>): Disposable {
        if (subscriber is XSubscriber) {
            subscriber.tag = tag
        }
        val disposable = flowable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber) as Disposable
        return add(tag, disposable)
    }

    fun remove(tag: Any, disposable: Disposable?): Boolean {
        if (null == disposable) return false
        val compositeDisposable = mDisposables[tag]
        return compositeDisposable?.remove(disposable) ?: false
    }

    fun clear(tag: Any) {
        mDisposables[tag]?.clear()
    }

}


