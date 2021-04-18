package me.ibore.utils

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/06/18
 * desc  : utils about view
</pre> *
 */
object ViewUtils {
    /**
     * Set the enabled state of this view.
     *
     * @param view    The view.
     * @param enabled True to enabled, false otherwise.
     */
    fun setViewEnabled(view: View?, enabled: Boolean) {
        setViewEnabled(view, enabled, (null as View?)!!)
    }

    /**
     * Set the enabled state of this view.
     *
     * @param view     The view.
     * @param enabled  True to enabled, false otherwise.
     * @param excludes The excludes.
     */
    fun setViewEnabled(view: View?, enabled: Boolean, vararg excludes: View) {
        if (view == null) return
        for (exclude in excludes) {
            if (view == exclude) return
        }
        if (view is ViewGroup) {
            val childCount = view.childCount
            for (i in 0 until childCount) {
                setViewEnabled(view.getChildAt(i), enabled, *excludes)
            }
        }
        view.isEnabled = enabled
    }

    /**
     * @param runnable The runnable
     */
    fun runOnUiThread(runnable: Runnable) {
        ThreadUtils.runOnUiThread(runnable)
    }

    /**
     * @param runnable    The runnable.
     * @param delayMillis The delay (in milliseconds) until the Runnable will be executed.
     */
    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis)
    }

    /**
     * Return whether horizontal layout direction of views are from Right to Left.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isLayoutRtl: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val primaryLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Utils.app.resources.configuration.locales[0]
                } else {
                    Utils.app.resources.configuration.locale
                }
                return TextUtils.getLayoutDirectionFromLocale(primaryLocale) == View.LAYOUT_DIRECTION_RTL
            }
            return false
        }

    /**
     * Fix the problem of topping the ScrollView nested ListView/GridView/WebView/RecyclerView.
     *
     * @param view The root view inner of ScrollView.
     */
    fun fixScrollViewTopping(view: View) {
        view.isFocusable = false
        var viewGroup: ViewGroup? = null
        if (view is ViewGroup) {
            viewGroup = view
        }
        if (viewGroup == null) {
            return
        }
        var i = 0
        val n = viewGroup.childCount
        while (i < n) {
            val childAt = viewGroup.getChildAt(i)
            childAt.isFocusable = false
            if (childAt is ViewGroup) {
                fixScrollViewTopping(childAt)
            }
            i++
        }
    }

    fun layoutId2View(@LayoutRes layoutId: Int): View {
        val inflate = Utils.app.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflate.inflate(layoutId, null)
    }
}