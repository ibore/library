package me.ibore.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import me.ibore.ktx.logD
import me.ibore.utils.BindingUtils
import me.ibore.widget.RootLayout

abstract class XActivity<VB : ViewBinding> : AppCompatActivity(), XView<VB> {

    protected val TAG: String = javaClass.simpleName

    protected val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingUtils.reflexViewBinding(javaClass, layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        logD("onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(onInflaterView(null))
        mBinding.onBindView(intent.extras, savedInstanceState)
        onBindConfig()
        onBindData()
    }

    override fun onInflaterView(container: ViewGroup?): View {
        return mBinding.root
    }

    override fun onDestroy() {
        onUnBindConfig()
        super.onDestroy()
    }

    override fun getXActivity(): XActivity<VB> = this

    open fun onBindConfig() {
//        BarUtils.setStatusBarColor(this, Color.TRANSPARENT, true)
//        BarUtils.setStatusBarLightMode(this, true)
    }

    open fun onUnBindConfig() {
        //DisposablesUtils.clear(this)
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

}
