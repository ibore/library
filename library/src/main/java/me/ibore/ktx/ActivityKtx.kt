package me.ibore.ktx

import me.ibore.utils.LogUtils


fun Any.logD(message: String) {
    LogUtils.d(this.javaClass.simpleName, message)
}