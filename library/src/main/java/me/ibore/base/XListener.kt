package me.ibore.base

import me.ibore.exception.XException

interface XListener<T> {

    fun onStart()

    fun onSuccess(data: T)

    fun onComplete()

    fun onFailure(e: XException)

}