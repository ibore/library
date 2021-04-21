package me.ibore.recycler.holder

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import me.ibore.utils.ContextUtils

@Suppress("UNCHECKED_CAST")
open class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {

        fun create(parent: ViewGroup, @LayoutRes layoutId: Int): RecyclerHolder {
            return create(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
        }

        fun create(itemView: View): RecyclerHolder = RecyclerHolder(itemView)

    }

    var extra: Any? = null

    val context: Context
        get() = itemView.context

    fun getActivity(): Activity? {
        return ContextUtils.getActivity(itemView.context)
    }

    fun <T : View> view(@IdRes id: Int): T {
        return itemView.findViewById(id)
    }

    fun string(@StringRes id: Int): String {
        return itemView.context.getString(id)
    }

    fun string(@StringRes id: Int, vararg formatArgs: Any): String {
        return itemView.context.getString(id, formatArgs)
    }

    fun drawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(itemView.context, id)
    }

    fun colorStateList(@ColorRes id: Int): ColorStateList? {
        return ContextCompat.getColorStateList(itemView.context, id)
    }

    @ColorInt
    fun color(@ColorRes id: Int): Int {
        return ContextCompat.getColor(itemView.context, id)
    }

}