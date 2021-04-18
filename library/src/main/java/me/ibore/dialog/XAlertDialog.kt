package me.ibore.dialog

import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.base.XDialog
import me.ibore.databinding.DialogAlertBinding
import me.ibore.ktx.dp2px
import me.ibore.utils.ScreenUtils

class XAlertDialog : XDialog<DialogAlertBinding>() {

    companion object {

        fun show(activity: XActivity<*>, builder: Builder) {
            val dialog = XAlertDialog()
            val bundle = Bundle()
            bundle.putParcelable("builder", builder)
            dialog.show(activity, bundle)
        }
    }

    private val builder: Builder by lazy {
        arguments?.getParcelable("builder") ?: Builder()
    }

    override fun DialogAlertBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val context = requireContext()
        mBinding.tvTitle.text = builder.title
        val margin = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
        if (builder.title.isNullOrEmpty()) {
            mBinding.tvTitle.visibility = View.GONE
            mBinding.scrollView.setPadding(margin, margin, margin, margin)
        } else {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.scrollView.setPadding(margin, 0, margin, margin)
        }
        mBinding.tvTitle.visibility = if (!builder.title.isNullOrEmpty()) View.VISIBLE else View.GONE
        mBinding.tvContent.text = builder.content
        mBinding.tvContent.movementMethod = LinkMovementMethod.getInstance()
        mBinding.scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val layoutParams = mBinding.scrollView.layoutParams
                val maxHeight = if (mBinding.tvTitle.visibility == View.GONE) {
                    ScreenUtils.appScreenHeight / 2 + dp2px(48F)
                } else {
                    ScreenUtils.appScreenHeight / 2
                }
                if (mBinding.scrollView.height > maxHeight) {
                    layoutParams.height = maxHeight
                }
                mBinding.scrollView.layoutParams = layoutParams
            }
        })
        mBinding.tvNegative.text = builder.negative ?: getText(R.string.dialog_negative)
        mBinding.tvNegative.setOnClickListener {
            dismiss()
            builder.negativeListener?.invoke(this@XAlertDialog)
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
            builder.positiveListener?.invoke(this@XAlertDialog)
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
        val content: CharSequence? = null,
        val negative: CharSequence? = null,
        val negativeListener: @RawValue ((dialog: XAlertDialog) -> Unit)? = null,
        val positive: CharSequence? = null,
        val positiveListener: @RawValue ((dialog: XAlertDialog) -> Unit)? = null,
        val touchBack: Boolean = false,
        val touchOutside: Boolean = false
    ) : Parcelable
}