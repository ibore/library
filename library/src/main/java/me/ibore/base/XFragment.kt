package me.ibore.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import me.ibore.utils.ReflexUtils
import me.ibore.utils.DisposablesUtils
import me.ibore.widget.RootLayout

abstract class XFragment<VB : ViewBinding> : Fragment(), XStatusView<VB> {

    protected lateinit var mBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = ReflexUtils.viewBinding(javaClass, layoutInflater, container, false)
        return mBinding.root
    }

    override fun getXActivity(): XActivity<*> {
        return activity as XActivity<*>
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.onBindView(arguments, savedInstanceState)
        onBindConfig()
        onBindData()
    }

    override fun onDestroyView() {
        onUnBindConfig()
        DisposablesUtils.clear(this)
        super.onDestroyView()
    }

    override fun onBindData() {}

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

    override fun onBindConfig() {

    }

    override fun onUnBindConfig() {

    }

}