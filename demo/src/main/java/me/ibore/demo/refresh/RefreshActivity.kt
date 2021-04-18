package me.ibore.demo.refresh

import android.os.Bundle
import android.view.ViewGroup
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityRefreshBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.ktx.dp2px
import me.ibore.widget.refresh.material.MaterialHeader

class RefreshActivity : BaseActivity<ActivityRefreshBinding>() {

    override fun ActivityRefreshBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))

        refreshLayout.setHeader(MaterialHeader(getXActivity()), ViewGroup.LayoutParams.MATCH_PARENT, dp2px(80F))
        refreshLayout.pinContent = true
    }

    override fun onBindData() {

    }

}
