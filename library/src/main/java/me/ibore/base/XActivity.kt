package me.ibore.base

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import me.ibore.utils.BarUtils
import me.ibore.utils.DisposablesUtils
import me.ibore.utils.ReflexUtils
import me.ibore.widget.RootLayout


abstract class XActivity<VB : ViewBinding> : AppCompatActivity(), XStatusView<VB> {

    protected open val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        onBindViewBinding()
    }

    protected open fun onBindViewBinding(): VB {
        return ReflexUtils.viewBinding(javaClass, layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        onBindConfig()
        mBinding.onBindView(intent.extras, savedInstanceState)
        onBindData()
    }

    override fun onDestroy() {
        onUnBindConfig()
        super.onDestroy()
    }

    override fun getXActivity(): XActivity<VB> = this

    override fun onBindConfig() {
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT, true)
    }

    override fun onUnBindConfig() {
        DisposablesUtils.clear(this)
    }

    override fun showLoading() {
        (mBinding.root as RootLayout?)?.showLoading()
    }

    override fun showContent() {
        (mBinding.root as RootLayout?)?.showContent()
    }

    override fun showEmpty() {
        (mBinding.root as RootLayout?)?.showEmpty()
    }

    override fun showError() {
        (mBinding.root as RootLayout?)?.showError()
    }

    override fun showDialog() {

    }

    override fun dismissDialog() {

    }
}
