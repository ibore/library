package me.ibore.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import me.ibore.base.XActivity

object ContextUtils {

    fun getActivity(context: Context): Activity? {
        var tempContext = context
        while (tempContext is ContextWrapper) {
            if (tempContext is Activity) {
                return tempContext
            }
            tempContext = tempContext.baseContext
        }
        return null
    }

    fun getXActivity(context: Context): XActivity<*>? {
        return getActivity(context) as XActivity<*>?
    }

}