package me.ibore.loading

import io.reactivex.disposables.Disposable
import me.ibore.BuildConfig
import me.ibore.base.XActivity
import me.ibore.base.XDialog
import me.ibore.base.XFragment
import me.ibore.base.mvp.XMvpPresenter
import me.ibore.loading.XLoading.Companion.NONE
import me.ibore.utils.DialogUtils
import me.ibore.utils.ToastUtils

class XLoadingHelper {

    @XLoading
    private val xLoading: Int
    var tag: Any? = null
        set(value) {
            field = value
            when (value) {
                is XActivity<*> -> xActivity = value
                is XFragment<*> -> xActivity = value.getXActivity()
                is XDialog<*> -> xActivity = value.getXActivity()
                is XMvpPresenter<*> -> xActivity = value.getView().getXActivity()
            }
        }

    private var xActivity: XActivity<*>? = null
    private var XLoadingDialog: XLoadingDialog? = null

    constructor() {
        this.xLoading = NONE
    }

    constructor(@XLoading xLoading: Int) {
        this.xLoading = xLoading
    }

    constructor(tag: Any, @XLoading xLoading: Int) {
        this.tag = tag
        this.xLoading = xLoading
    }

    fun showLoading(disposable: Disposable) {
        if (xLoading == XLoading.DIALOG_NONE || xLoading == XLoading.DIALOG_TOAST || xLoading == XLoading.DIALOG_DIALOG) {
            XLoadingDialog = XLoadingDialog()
            XLoadingDialog?.show(xActivity!!)
            XLoadingDialog?.setOnDismissListener {
                disposable.dispose()
            }
        }
    }

    fun dismiss() {
        XLoadingDialog?.dismissAllowingStateLoss()
    }

    fun failure(e: Exception) {
        dismiss()
        if (BuildConfig.DEBUG) e.printStackTrace()
        if (xLoading == XLoading.NONE_TOAST || xLoading == XLoading.DIALOG_TOAST) {
            ToastUtils.showShort(e.message)
        } else if (xLoading == XLoading.NONE_DIALOG || xLoading == XLoading.DIALOG_DIALOG) {
            DialogUtils.showAlert(xActivity!!, content = e.message!!)
        }
    }

    fun complete() {


    }

}