package me.ibore.demo.title_bar

import android.os.Bundle
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityTitleBarBinding
import me.ibore.demo.databinding.TitleBarBinding

class TitleBarActivity : BaseActivity<ActivityTitleBarBinding>() {

    override fun ActivityTitleBarBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))


    }

    override fun onBindData() {

    }


}