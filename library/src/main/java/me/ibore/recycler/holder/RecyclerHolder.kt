package me.ibore.recycler.holder

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import me.ibore.ktx.getActivity
import me.ibore.ktx.layoutInflater

@Suppress("UNCHECKED_CAST")
open class RecyclerHolder @JvmOverloads constructor(itemView: View, var helper: Any? = null) :
    RecyclerView.ViewHolder(itemView) {

    companion object {

        @JvmOverloads
        fun create(
            parent: ViewGroup, @LayoutRes layoutId: Int, helper: Any? = null
        ): RecyclerHolder {
            return create(parent.layoutInflater.inflate(layoutId, parent, false), helper)
        }

        @JvmOverloads
        fun create(itemView: View, helper: Any? = null) = RecyclerHolder(itemView, helper)

    }

    val context: Context
        get() = itemView.context

    fun getActivity(): Activity? {
        return context.getActivity()
    }

    fun <T : View> view(@IdRes id: Int): T {
        return itemView.findViewById(id)
    }

    fun string(@StringRes id: Int): String {
        return itemView.context.getString(id)
    }

    fun string(@StringRes id: Int, vararg formatArgs: Any?): String {
        return itemView.context.getString(id, *formatArgs)
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

    fun onClickListener(onClickListener: View.OnClickListener) {
        itemView.setOnClickListener(onClickListener)
    }

    fun onLongClickListener(onLongClickListener: View.OnLongClickListener) {
        itemView.setOnLongClickListener(onLongClickListener)
    }
}