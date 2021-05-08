package me.ibore.ktx

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import me.ibore.base.XActivity

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

fun Context.getActivity(): Activity? {
        var tempContext = this
        while (tempContext is ContextWrapper) {
            if (tempContext is Activity) {
                return tempContext
            }
            tempContext = tempContext.baseContext
        }
        return null
    }

fun Context.getXActivity(): XActivity<*>? {
        var tempContext = this
        while (tempContext is ContextWrapper) {
            if (tempContext is Activity) {
                return tempContext as XActivity<*>
            }
            tempContext = tempContext.baseContext
        }
        return null
    }

fun Context.hideSoftInputKeyBoard(focusView: View) {
    val binder = focusView.windowToken
    if (binder != null) {
        val imd = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imd.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}
