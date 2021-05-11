package me.ibore.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.base.XDialog
import me.ibore.databinding.DialogXInputBinding
import me.ibore.ktx.dp2px

class XInputDialog : XDialog<DialogXInputBinding>() {

    companion object {

        fun show(activity: XActivity<*>, builder: Builder) {
            val dialog = XInputDialog()
            val bundle = Bundle()
            bundle.putParcelable("builder", builder)
            dialog.show(activity, bundle)
        }
    }

    private val builder: Builder by lazy {
        arguments?.getParcelable("builder") ?: Builder()
    }

    override fun DialogXInputBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        tvTitle.text = builder.title
        if (builder.title.isNullOrEmpty()) {
            tvTitle.visibility = View.GONE
        } else {
            tvTitle.visibility = View.VISIBLE
        }
        val padding = resources.getDimensionPixelOffset(R.dimen.dialog_margin)
        if (builder.title.isNullOrEmpty()) {
            tvTitle.visibility = View.GONE
            llContent.setPadding(padding, padding, padding, padding)
        } else {
            tvTitle.visibility = View.VISIBLE
            llContent.setPadding(padding, 0, padding, padding)
        }
        tvHint.text = builder.hint
        etInput.setText(builder.defaultInput)
        etInput.setSelection(etInput.text.length);
        tvNegative.text = builder.negative ?: getText(R.string.dialog_negative)
        tvNegative.setOnClickListener {
            dismiss()
            builder.negativeListener?.invoke(this@XInputDialog)
        }
        if (builder.negative.isNullOrEmpty() && null == builder.negativeListener) {
            tvNegative.visibility = View.GONE
            viewLine.visibility = View.GONE
        } else {
            tvNegative.visibility = View.VISIBLE
            viewLine.visibility = View.VISIBLE
        }
        tvPositive.text = builder.positive ?: getText(R.string.dialog_positive)
        tvPositive.setOnClickListener {
            dismiss()
            builder.positiveListener?.invoke(etInput.text.toString(), this@XInputDialog)
        }
    }

    override fun onBindDialogConfig(): DialogConfig {
        return DialogConfig(
            dp2px(280F), DialogConfig.WRAP_CONTENT,
            touchBack = builder.touchBack, touchOutside = builder.touchOutside
        )
    }

    @Parcelize
    data class Builder(
        val title: CharSequence? = null,
        val hint: CharSequence? = null,
        val defaultInput: CharSequence? = null,
        val negative: CharSequence? = null,
        val negativeListener: @RawValue ((dialog: XInputDialog) -> Unit)? = null,
        val positive: CharSequence? = null,
        val positiveListener: @RawValue ((input: String, dialog: XInputDialog) -> Unit)? = null,
        val touchBack: Boolean = false,
        val touchOutside: Boolean = false
    ) : Parcelable
}