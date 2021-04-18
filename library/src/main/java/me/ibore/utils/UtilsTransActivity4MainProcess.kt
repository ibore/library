package me.ibore.utils

import android.app.Activity
import android.content.Intent

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2020/03/19
 * desc  :
</pre> *
 */
object UtilsTransActivity4MainProcess : UtilsTransActivity() {
    fun start(delegate: TransActivityDelegate?) {
        start(null, null, delegate, UtilsTransActivity4MainProcess::class.java)
    }

    fun start(
        consumer: Utils.Consumer<Intent>?,
        delegate: TransActivityDelegate?
    ) {
        start(null, consumer, delegate, UtilsTransActivity4MainProcess::class.java)
    }

    fun start(
        activity: Activity?,
        delegate: TransActivityDelegate?
    ) {
        start(activity, null, delegate, UtilsTransActivity4MainProcess::class.java)
    }

    fun start(
        activity: Activity?,
        consumer: Utils.Consumer<Intent>?,
        delegate: TransActivityDelegate?
    ) {
        start(activity, consumer, delegate, UtilsTransActivity4MainProcess::class.java)
    }
}