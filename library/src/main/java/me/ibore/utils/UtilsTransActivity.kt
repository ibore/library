package me.ibore.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable
import java.util.*

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2020/03/19
 * desc  :
</pre> *
 */
open class UtilsTransActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        val extra = intent.getSerializableExtra(EXTRA_DELEGATE)
        if (extra !is TransActivityDelegate) {
            super.onCreate(savedInstanceState)
            finish()
            return
        }
        CALLBACK_MAP[this] = extra
        extra.onCreateBefore(this, savedInstanceState)
        super.onCreate(savedInstanceState)
        extra.onCreated(this, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val callback = CALLBACK_MAP[this] ?: return
        callback.onStarted(this)
    }

    override fun onResume() {
        super.onResume()
        val callback = CALLBACK_MAP[this] ?: return
        callback.onResumed(this)
    }

    override fun onPause() {
        overridePendingTransition(0, 0)
        super.onPause()
        val callback = CALLBACK_MAP[this] ?: return
        callback.onPaused(this)
    }

    override fun onStop() {
        super.onStop()
        val callback = CALLBACK_MAP[this] ?: return
        callback.onStopped(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val callback = CALLBACK_MAP[this] ?: return
        callback.onSaveInstanceState(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        val callback = CALLBACK_MAP[this] ?: return
        callback.onDestroy(this)
        CALLBACK_MAP.remove(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val callback = CALLBACK_MAP[this] ?: return
        callback.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val callback = CALLBACK_MAP[this] ?: return
        callback.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val callback = CALLBACK_MAP[this]
            ?: return super.dispatchTouchEvent(ev)
        return if (callback.dispatchTouchEvent(this, ev)) {
            true
        } else super.dispatchTouchEvent(ev)
    }

    abstract class TransActivityDelegate : Serializable {
        fun onCreateBefore(activity: UtilsTransActivity, savedInstanceState: Bundle?) {
        }

        open fun onCreated(activity: UtilsTransActivity, savedInstanceState: Bundle?) {
        }

        fun onStarted(activity: UtilsTransActivity) {
        }

        open fun onDestroy(activity: UtilsTransActivity) {
        }

        fun onResumed(activity: UtilsTransActivity) {
        }

        fun onPaused(activity: UtilsTransActivity) {
        }

        fun onStopped(activity: UtilsTransActivity) {
        }

        fun onSaveInstanceState(activity: UtilsTransActivity, outState: Bundle?) {
        }

        open fun onRequestPermissionsResult(
            activity: UtilsTransActivity, requestCode: Int,
            permissions: Array<String>, grantResults: IntArray
        ) {
        }

        open fun onActivityResult(
            activity: UtilsTransActivity, requestCode: Int, resultCode: Int, data: Intent?
        ) {
        }

        open fun dispatchTouchEvent(activity: UtilsTransActivity, ev: MotionEvent?): Boolean {
            return false
        }
    }

    companion object {
        private val CALLBACK_MAP: MutableMap<UtilsTransActivity, TransActivityDelegate> = HashMap()
        protected const val EXTRA_DELEGATE = "extra_delegate"
        fun start(delegate: TransActivityDelegate?) {
            start(null, null, delegate, UtilsTransActivity::class.java)
        }

        fun start(consumer: Utils.Consumer<Intent>?, delegate: TransActivityDelegate?) {
            start(null, consumer, delegate, UtilsTransActivity::class.java)
        }

        fun start(activity: Activity?, delegate: TransActivityDelegate?) {
            start(activity, null, delegate, UtilsTransActivity::class.java)
        }

        fun start(
            activity: Activity?, consumer: Utils.Consumer<Intent>?, delegate: TransActivityDelegate?
        ) {
            start(activity, consumer, delegate, UtilsTransActivity::class.java)
        }

        @JvmStatic
        protected fun start(
            activity: Activity?, consumer: Utils.Consumer<Intent>?,
            delegate: TransActivityDelegate?, cls: Class<*>?
        ) {
            if (delegate == null) return
            val starter = Intent(Utils.app, cls)
            starter.putExtra(EXTRA_DELEGATE, delegate)
            consumer?.accept(starter)
            if (activity == null) {
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                Utils.app.startActivity(starter)
            } else {
                activity.startActivity(starter)
            }
        }
    }
}