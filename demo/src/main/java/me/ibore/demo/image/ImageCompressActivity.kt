package me.ibore.demo.image

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityImageCompressBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.TitleItem

class ImageCompressActivity : BaseActivity<ActivityImageCompressBinding>() {

    private var adapter = TitleAdapter()

    override fun ActivityImageCompressBinding.onBindView(
        bundle: Bundle?, savedInstanceState: Bundle?
    ) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        recyclerView.adapter = adapter
    }

    override fun onBindData() {
        adapter.addData(TitleItem("单张图片压缩") {

        })

        adapter.addData(TitleItem("多张图片压缩") {

        })

    }
}