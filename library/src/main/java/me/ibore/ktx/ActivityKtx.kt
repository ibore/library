package me.ibore.ktx

import me.ibore.utils.LogUtils


fun Any.logD(message: String) {
    LogUtils.dTag(this.javaClass.simpleName, message)
}