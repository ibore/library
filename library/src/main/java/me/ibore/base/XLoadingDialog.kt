package me.ibore.base

import android.os.Bundle
import me.ibore.databinding.DialogXLoadingBinding
import me.ibore.ktx.dp2px

class XLoadingDialog : XDialog<DialogXLoadingBinding>() {

    override fun onBindDialogConfig(): DialogConfig {
        return DialogConfig(dp2px(102F), dp2px(102F), isTransBg = true, touchBack = true)
    }

    override fun DialogXLoadingBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

}