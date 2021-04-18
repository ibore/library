package me.ibore.loading

import androidx.annotation.IntDef
import me.ibore.loading.XLoading.Companion.DIALOG_DIALOG
import me.ibore.loading.XLoading.Companion.DIALOG_NONE
import me.ibore.loading.XLoading.Companion.DIALOG_TOAST
import me.ibore.loading.XLoading.Companion.NONE
import me.ibore.loading.XLoading.Companion.NONE_DIALOG
import me.ibore.loading.XLoading.Companion.NONE_TOAST

@IntDef(NONE, DIALOG_NONE, DIALOG_TOAST, DIALOG_DIALOG, NONE_TOAST, NONE_DIALOG)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class XLoading {

    companion object {
        const val NONE = 0
        const val DIALOG_NONE = 1
        const val DIALOG_TOAST = 2
        const val DIALOG_DIALOG = 3
        const val NONE_TOAST = 4
        const val NONE_DIALOG = 5
    }

}