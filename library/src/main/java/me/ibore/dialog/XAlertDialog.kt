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
import me.ibore.databinding.XDialogAlertBinding
import me.ibore.ktx.dp2px
import me.ibore.utils.ScreenUtils

class XAlertDialog : XDialog<XDialogAlertBinding>() {

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

    override fun XDialogAlertBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val context = requireContext()
        tvTitle.text = builder.title
        val margin = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
        if (builder.title.isNullOrEmpty()) {
            tvTitle.visibility = View.GONE
            scrollView.setPadding(margin, margin, margin, margin)
        } else {
            tvTitle.visibility = View.VISIBLE
            scrollView.setPadding(margin, 0, margin, margin)
        }
        tvTitle.visibility = if (!builder.title.isNullOrEmpty()) View.VISIBLE else View.GONE
        tvContent.text = builder.content
        tvContent.movementMethod = LinkMovementMethod.getInstance()
        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val layoutParams = scrollView.layoutParams
                val maxHeight = if (tvTitle.visibility == View.GONE) {
                    ScreenUtils.getAppScreenHeight() / 2 + dp2px(48F)
                } else {
                    ScreenUtils.getAppScreenHeight() / 2
                }
                if (scrollView.height > maxHeight) {
                    layoutParams.height = maxHeight
                }
                scrollView.layoutParams = layoutParams
            }
        })
        tvNegative.text = builder.negative ?: getText(R.string.dialog_negative)
        tvNegative.setOnClickListener {
            dismiss()
            builder.negativeListener?.invoke(this@XAlertDialog)
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