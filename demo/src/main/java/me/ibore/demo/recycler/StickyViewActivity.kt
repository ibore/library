package me.ibore.demo.recycler

import android.os.Bundle
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityStickyViewBinding
import me.ibore.demo.databinding.TitleBarBinding

class StickyViewActivity : BaseActivity<ActivityStickyViewBinding>() {

    /*val adapter = object : StickyView.Adapter<String>() {
        override fun getLayoutId(): Int = R.layout.item_sticky_view
        override fun onBindView(holder: RecyclerHolder, data: String, dataPosition: Int) {
            viewHolder.text(R.id.tv_title, data)
        }
    }*/

    override fun ActivityStickyViewBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        //sticky_view.setAdapter(adapter)
    }

    override fun onBindData() {
        /*adapter.addData(StickyModel("测试标题1"))
        for (index in 0 until 10) {
            adapter.addData("测试数据1")
        }
        adapter.addData(StickyModel("测试标题2"))
        for (index in 0 until 15) {
            adapter.addData("测试数据2")
        }
        adapter.addData(StickyModel("测试标题3"))
        for (index in 0 until 17) {
            adapter.addData("测试数据3")
        }
        adapter.addData(StickyModel("测试标题4"))
        for (index in 0 until 20) {
            adapter.addData("测试数据4")
        }
        adapter.addData(StickyModel("测试标题5"))
        for (index in 0 until 20) {
            adapter.addData("测试数据5")
        }*/
    }

}