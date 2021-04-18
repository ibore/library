package me.ibore.loading

import android.os.Bundle
import me.ibore.base.XDialog
import me.ibore.databinding.DialogLoadingBinding
import me.ibore.ktx.dp2px

class XLoadingDialog : XDialog<DialogLoadingBinding>() {

    override fun DialogLoadingBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun onBindDialogConfig(): DialogConfig {
        return DialogConfig(
            dp2px(102F), dp2px(102F),
            isFullScreen = false, isTransBg = true, touchOutside = false, touchBack = true
        )
    }
}