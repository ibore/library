package me.ibore.animation

import android.animation.Animator
import android.view.View

interface BaseAnimation {

    fun getAnimators(view: View): Array<Animator>

}