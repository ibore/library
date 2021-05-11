package me.ibore.update

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import me.ibore.R
import me.ibore.base.XDialog
import me.ibore.databinding.DialogXUpdateBinding

class UpdateDialog : XDialog<DialogXUpdateBinding>() {

    private var update: Update? = null

    override fun DialogXUpdateBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
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