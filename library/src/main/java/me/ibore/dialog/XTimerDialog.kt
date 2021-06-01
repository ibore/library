package me.ibore.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.base.XDialog
import me.ibore.databinding.XDialogTimerBinding
import me.ibore.ktx.dp2px
import me.ibore.utils.ScreenUtils

class XTimerDialog : XDialog<XDialogTimerBinding>() {

    companion object {
        fun show(activity: XActivity<*>, title: CharSequence? = null, selectedTimer: String? = null,
                 startTimer: String? = null, endTimer: String? = null,
                 onSelectListener: @RawValue ((selected: String?) -> Unit)? = null,
                 showBottom: Boolean = false, touchBack: Boolean = false, touchOutside: Boolean = false) {
            val dialog = XTimerDialog()
            val bundle = Bundle()
            bundle.putParcelable("builder", Builder(title, selectedTimer, startTimer, endTimer, onSelectListener, showBottom, touchBack, touchOutside))
            dialog.show(activity, bundle)
        }
    }

    private val builder: Builder by lazy {
        arguments?.getParcelable("builder") ?: Builder()
    }

    override fun XDialogTimerBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        if (builder.showBottom) {
            tvNegative.visibility = View.VISIBLE
            tvPositive.visibility = View.VISIBLE
            btnPositive.visibility = View.GONE
        } else {
            tvNegative.visibility = View.GONE
            tvPositive.visibility = View.GONE
            btnPositive.visibility = View.VISIBLE
        }
        tvTitle.text = builder.title ?: getString(R.string.dialog_timer_title)
        tvNegative.setOnClickListener {
            dismiss()
        }
        tvPositive.setOnClickListener {
            dismiss()
            builder.onSelectListener?.invoke(timerPickerView.selectedTimer)
        }
        btnPositive.setOnClickListener {
            dismiss()
            builder.onSelectListener?.invoke(timerPickerView.selectedTimer)
        }
        timerPickerView.setStartEndTimer(builder.startTimer, builder.endTimer)
        timerPickerView.selectedTimer = builder.selectedTimer ?: ""
    }

    override fun onBindDialogConfig(): DialogConfig {
        return if (builder.showBottom) {
            DialogConfig(
                ScreenUtils.getAppScreenWidth(), DialogConfig.WRAP_CONTENT, gravity = Gravity.BOTTOM,
                touchBack = builder.touchBack, touchOutside = builder.touchOutside
            )
        } else {
            DialogConfig(
                dp2px(280F), DialogConfig.WRAP_CONTENT, gravity = Gravity.CENTER,
                touchBack = builder.touchBack, touchOutside = builder.touchOutside
            )
        }
    }

    @Parcelize
    class Builder(val title: CharSequence? = null, val selectedTimer: String? = null,
                  val startTimer: String? = null, val endTimer: String? = null,
                  val onSelectListener: @RawValue ((selected: String?) -> Unit)? = null,
                  val showBottom: Boolean = false, val touchBack: Boolean = false, val touchOutside: Boolean = false) : Parcelable
}