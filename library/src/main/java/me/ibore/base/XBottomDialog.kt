package me.ibore.base

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.ibore.R
import me.ibore.utils.BindingUtils
import me.ibore.utils.DisposablesUtils

abstract class XBottomDialog<VB : ViewBinding> : BottomSheetDialogFragment(), XView<VB> {

    protected open val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingUtils.reflexViewBinding(javaClass, layoutInflater)
    }

    private var onDismissListener: DialogInterface.OnDismissListener? = null

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.XDialog_Not_Trans)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.onBindView(arguments, savedInstanceState)
        onBindData()
    }

    override fun onBindData() {}

    override fun getXActivity(): XActivity<*> {
        return (activity as XActivity<*>)
    }

    override fun onDestroyView() {
        DisposablesUtils.clear(this)
        super.onDestroyView()
    }

    fun show(xActivity: XActivity<*>, arguments: Bundle? = null) {
        this.arguments = arguments
        super.show(xActivity.supportFragmentManager, this::class.java.simpleName)
    }

}