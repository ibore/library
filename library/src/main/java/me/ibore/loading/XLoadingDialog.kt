package me.ibore.loading

import android.os.Bundle
import me.ibore.base.XDialog
import me.ibore.databinding.XDialogLoadingBinding
import me.ibore.ktx.dp2px

class XLoadingDialog : XDialog<XDialogLoadingBinding>() {

    override fun XDialogLoadingBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun onBindDialogConfig(): DialogConfig {
        return DialogConfig(
            dp2px(102F), dp2px(102F),
            isFullScreen = false, isTransBg = true, touchOutside = false, touchBack = true
        )
    }
}