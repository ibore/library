package me.ibore.recycler.animators

import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView

open class ScaleInLeftAnimator : BaseItemAnimator {
    constructor()
    constructor(interpolator: Interpolator) {
        this.interpolator = interpolator
    }

    override fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.pivotX = 0f
    }

    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().apply {
            scaleX(0f)
            scaleY(0f)
            duration = removeDuration
            interpolator = interpolator
            setListener(DefaultRemoveAnimatorListener(holder))
            startDelay = getRemoveDelay(holder)
        }.start()
    }

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.pivotX = 0f
        holder.itemView.scaleX = 0f
        holder.itemView.scaleY = 0f
    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().apply {
            scaleX(1f)
            scaleY(1f)
            duration = addDuration
            interpolator = interpolator
            setListener(DefaultAddAnimatorListener(holder))
            startDelay = getAddDelay(holder)
        }.start()
    }
}
