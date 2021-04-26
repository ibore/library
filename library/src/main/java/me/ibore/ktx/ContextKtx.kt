package me.ibore.ktx

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import me.ibore.utils.ContextUtils

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.activity: Activity?
    get() = ContextUtils.getActivity(this)

fun Context.hideSoftInputKeyBoard(focusView: View) {
    val binder = focusView.windowToken
    if (binder != null) {
        val imd = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imd.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}
