package me.ibore.demo.video

import android.os.Bundle
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityVideoBinding
import me.ibore.demo.databinding.TitleBarBinding

class VideoActivity : BaseActivity<ActivityVideoBinding>() {

    override fun ActivityVideoBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))

    }

    override fun onBindData() {

    }
}
