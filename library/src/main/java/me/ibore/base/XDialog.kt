package me.ibore.base

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import kotlinx.parcelize.Parcelize
import me.ibore.R
import me.ibore.base.XDialog.DialogConfig.Companion.WRAP_CONTENT
import me.ibore.utils.BindingUtils

abstract class XDialog<VB : ViewBinding> : AppCompatDialogFragment(), XView<VB> {

    protected open val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingUtils.reflexViewBinding(javaClass, layoutInflater)
    }

    private val mDialogConfig: DialogConfig by lazy(mode = LazyThreadSafetyMode.NONE) {
        onBindDialogConfig()
    }

    protected open fun onBindDialogConfig(): DialogConfig {
        return DialogConfig(WRAP_CONTENT, WRAP_CONTENT)
    }

    private var onDismissListener: DialogInterface.OnDismissListener? = null

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (mDialogConfig.isTransBg) setStyle(STYLE_NO_TITLE, R.style.XDialog_Trans)
        else setStyle(STYLE_NO_TITLE, R.style.XDialog_Not_Trans)
        val dialog = object : AppCompatDialog(requireContext(), theme) {
            override fun show() {
                window?.apply {
                    decorView.systemUiVisibility =
                        (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                }
                super.show()
            }
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(mDialogConfig.touchOutside)
        dialog.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return@OnKeyListener !mDialogConfig.touchBack
            }
            false
        })
        return dialog
    }

    override fun onResume() {
        super.onResume()
        if (mDialogConfig.isFullScreen) {
            //dialog!!.window!!.setLayout(ScreenUtils.appScreenWidth, MATCH_PARENT)
        } else {
            dialog!!.window!!.setLayout(mDialogConfig.width, mDialogConfig.height)
        }
        dialog!!.window!!.setGravity(mDialogConfig.gravity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onBindConfig()
        mBinding.onBindView(arguments, savedInstanceState)
        onBindData()
    }

    override fun onBindData() {}

    override fun getXActivity(): XActivity<*> {
        return (activity as XActivity<*>)
    }

    override fun onBindConfig() {}

    override fun onUnBindConfig() {}

    override fun onDestroyView() {
        //DisposablesUtils.clear(this)
        onUnBindConfig()
        super.onDestroyView()
    }

    fun show(xActivity: XActivity<*>, arguments: Bundle? = null) {
        this.arguments = arguments
        show(xActivity.supportFragmentManager, this::class.java.simpleName)
    }

    @Parcelize
    data class DialogConfig(
        val width: Int, val height: Int, val gravity: Int = Gravity.CENTER,
        val isFullScreen: Boolean = false, val isTransBg: Boolean = false,
        val touchBack: Boolean = false, val touchOutside: Boolean = false
    ) : Parcelable {
        companion object {
            const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
            const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}