package me.ibore.update

import android.os.Bundle
import me.ibore.base.XDialog
import me.ibore.databinding.XDialogUpdateBinding

class UpdateDialog : XDialog<XDialogUpdateBinding>() {

    private var update: Update? = null

    override fun XDialogUpdateBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        update = bundle?.getParcelable("update")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        update = outState.getParcelable("update")
    }

    override fun isCancelable(): Boolean {
        return update?.isForceUpdate!!
    }


}