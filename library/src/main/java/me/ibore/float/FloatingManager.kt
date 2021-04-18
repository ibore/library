package me.ibore.float

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import me.ibore.R
import java.lang.ref.WeakReference

class FloatingManager private constructor() : IFloatingView {

    companion object {
        @Volatile
        private var mInstance: FloatingManager? = null
        fun get(): FloatingManager {
            if (mInstance == null) {
                synchronized(FloatingManager::class.java) {
                    if (mInstance == null) {
                        mInstance = FloatingManager()
                    }
                }
            }
            return mInstance!!
        }
    }

    override var floatingView: FloatingView? = null
        private set
    private var mContainer: WeakReference<FrameLayout>? = null

    @LayoutRes
    private var mLayoutId: Int = 0

    @DrawableRes
    private var mIconRes: Int = R.drawable.ic_title_bar_back_normal
    private var mLayoutParams: ViewGroup.LayoutParams = params
    override fun remove(): FloatingManager {
        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                if (floatingView == null) {
                    return
                }
                if (ViewCompat.isAttachedToWindow(floatingView!!) && getContainer() != null) {
                    getContainer()!!.removeView(floatingView)
                }
                floatingView = null
            }
        })
        return this
    }

    private fun ensureFloatingView(activity: Activity) {
        synchronized(this) {
            if (floatingView != null) {
                return
            }
            val floatingIconView = FloatingIconView(activity)
            floatingView = floatingIconView
            floatingIconView.layoutParams = mLayoutParams
            floatingIconView.setIconImage(mIconRes)
            addViewToWindow(floatingIconView)
        }
    }

    override fun add(activity: Activity): FloatingManager {
        ensureFloatingView(activity)
        return this
    }

    override fun attach(activity: Activity): FloatingManager {
        attach(getActivityRoot(activity))
        return this
    }

    override fun attach(container: FrameLayout): FloatingManager {
        if (floatingView == null) {
            mContainer = WeakReference(container)
            return this
        }
        if (floatingView!!.parent == container) {
            return this
        }
        if (getContainer() != null && floatingView!!.parent === container) {
            getContainer()!!.removeView(floatingView)
        }
        mContainer = WeakReference(container)
        container.addView(floatingView)
        return this
    }

    override fun detach(activity: Activity): FloatingManager {
        detach(getActivityRoot(activity))
        return this
    }

    override fun detach(container: FrameLayout): FloatingManager {
        if (floatingView != null && ViewCompat.isAttachedToWindow(floatingView!!)) {
            container.removeView(floatingView)
        }
        if (getContainer() == container) {
            mContainer = null
        }
        return this
    }

    override fun icon(@DrawableRes resId: Int): FloatingManager {
        mIconRes = resId
        return this
    }

    override fun customView(viewGroup: FloatingView): FloatingManager {
        floatingView = viewGroup
        return this
    }

    override fun customView(@LayoutRes resource: Int): FloatingManager {
        mLayoutId = resource
        return this
    }

    override fun layoutParams(params: ViewGroup.LayoutParams): FloatingManager? {
        mLayoutParams = params!!
        if (floatingView != null) {
            floatingView!!.layoutParams = params
        }
        return this
    }

    override fun listener(floatingListener: FloatingListener): FloatingManager {
        if (floatingView != null) {
            floatingView!!.setFloatingListener(floatingListener)
        }
        return this
    }

    private fun addViewToWindow(view: View) {
        if (getContainer() == null) {
            return
        }
        getContainer()!!.addView(view)
    }

    private fun getContainer(): FrameLayout? {
        return mContainer?.get()
    }

    private val params: FrameLayout.LayoutParams
        get() {
            val params = FrameLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.BOTTOM or Gravity.START
            params.setMargins(13, params.topMargin, params.rightMargin, 500)
            return params
        }

    private fun getActivityRoot(activity: Activity): FrameLayout {
        return activity.window.decorView.findViewById<View>(android.R.id.content) as FrameLayout
    }


}