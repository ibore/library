package me.ibore.float

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes

interface IFloatingView {
    fun remove(): FloatingManager?
    fun add(activity: Activity): FloatingManager?
    fun attach(activity: Activity): FloatingManager?
    fun attach(container: FrameLayout): FloatingManager?
    fun detach(activity: Activity): FloatingManager?
    fun detach(container: FrameLayout): FloatingManager?
    val floatingView: FloatingView?
    fun icon(@DrawableRes resId: Int): FloatingManager?
    fun customView(viewGroup: FloatingView): FloatingManager?
    fun customView(@LayoutRes resource: Int): FloatingManager?
    fun layoutParams(params: ViewGroup.LayoutParams): FloatingManager?
    fun listener(floatingListener: FloatingListener): FloatingManager?
}
