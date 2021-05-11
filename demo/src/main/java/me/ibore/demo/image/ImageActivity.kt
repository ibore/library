package me.ibore.demo.image

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.ActivityAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityImageBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.ActivityItem
import me.ibore.demo.title_bar.TitleBarActivity
import me.ibore.image.picker.ImagePicker
import me.ibore.utils.ToastUtils

class ImageActivity : BaseActivity<ActivityImageBinding>() {

    private var adapter = ActivityAdapter()

    override fun ActivityImageBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        recyclerView.adapter = adapter
    }


    override fun onBindData() {

        adapter.addData(ActivityItem("选择图片", ImagePickerActivity::class.java))
        adapter.addData(ActivityItem("图片压缩", ImageCompressActivity::class.java))

    }


}