package me.ibore.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import me.ibore.ktx.logD
import me.ibore.widget.RootLayout

abstract class XActivity<VB : ViewBinding> : AppCompatActivity(), XView {

    protected val TAG: String = javaClass.simpleName
    protected lateinit var mBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        logD("ddddddddddd")
        super.onCreate(savedInstanceState)
        setContentView(onInflaterView(null))
        onBindView(intent.extras, savedInstanceState)
        onBindConfig()
        onBindData()
    }

    override fun onInflaterView(container: ViewGroup?): View {
        //binding = ViewBindingUtils.inflate<VB>(this, layoutInflater, container)!!
        /*if (binding.root is RootLayout) {
            val root = binding.root as RootLayout
            if (!root.hasLayoutType(RootLayout.LOADING)) {
                root.addView(layoutInflater.inflate(R.layout.load_loading, root, false),
                        RootLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, RootLayout.LOADING))
            }
            if (!root.hasLayoutType(RootLayout.EMPTY)) {
                root.addView(layoutInflater.inflate(R.layout.load_empty, root, false),
                        RootLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, RootLayout.EMPTY))
            }
            if (!root.hasLayoutType(RootLayout.ERROR)) {
                root.addView(layoutInflater.inflate(R.layout.load_error, root, false),
                        RootLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, RootLayout.ERROR))
            }
        }*/
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
