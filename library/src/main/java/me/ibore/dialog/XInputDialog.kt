package me.ibore.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.base.XDialog
import me.ibore.databinding.DialogInputBinding
import me.ibore.ktx.dp2px

class XInputDialog : XDialog<DialogInputBinding>() {

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

    override fun DialogInputBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        mBinding.tvTitle.text = builder.title
        if (builder.title.isNullOrEmpty()) {
            mBinding.tvTitle.visibility = View.GONE
        } else {
            mBinding.tvTitle.visibility = View.VISIBLE
        }
        val padding = resources.getDimensionPixelOffset(R.dimen.dialog_margin)
        if (builder.title.isNullOrEmpty()) {
            mBinding.tvTitle.visibility = View.GONE
            mBinding.llContent.setPadding(padding, padding, padding, padding)
        } else {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.llContent.setPadding(padding, 0, padding, padding)
        }
        mBinding.tvHint.text = builder.hint
        mBinding.etInput.setText(builder.defaultInput)
        mBinding.etInput.setSelection(mBinding.etInput.text.length);
        mBinding.tvNegative.text = builder.negative ?: getText(R.string.dialog_negative)
        mBinding.tvNegative.setOnClickListener {
            dismiss()
            builder.negativeListener?.invoke(this@XInputDialog)
        }
        if (builder.negative.isNullOrEmpty() && null == builder.negativeListener) {
            mBinding.tvNegative.visibility = View.GONE
            mBinding.viewLine.visibility = View.GONE
        } else {
            mBinding.tvNegative.visibility = View.VISIBLE
            mBinding.viewLine.visibility = View.VISIBLE
        }
        mBinding.tvPositive.text = builder.positive ?: getText(R.string.dialog_positive)
        mBinding.tvPositive.setOnClickListener {
            dismiss()
            builder.positiveListener?.invoke(mBinding.etInput.text.toString(), this@XInputDialog)
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