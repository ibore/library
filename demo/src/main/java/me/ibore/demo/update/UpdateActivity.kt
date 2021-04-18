package me.ibore.demo.update

import android.os.Bundle
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityUpdateBinding
import me.ibore.demo.databinding.TitleBarBinding

class UpdateActivity : BaseActivity<ActivityUpdateBinding>() {

    override fun ActivityUpdateBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))

    }

    override fun onBindData() {

    }
}
