package me.ibore.demo.image

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityImagePickerBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.TitleItem
import me.ibore.image.picker.ImagePicker
import me.ibore.utils.LogUtils
import me.ibore.utils.ToastUtils

class ImagePickerActivity : BaseActivity<ActivityImagePickerBinding>() {

    private var adapter = TitleAdapter()

    private var mImagePaths: ArrayList<String>? = null
    private val REQUEST_SELECT_IMAGES_CODE = 0x01

    override fun ActivityImagePickerBinding.onBindView(
        bundle: Bundle?, savedInstanceState: Bundle?
    ) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        recyclerView.adapter = adapter
    }

    override fun onBindData() {
        adapter.addData(TitleItem("选择单张图片") {
            ImagePicker.start()
                .showCamera() //设置是否显示拍照按钮
                .showImage() //设置是否展示图片
//                .showVideo() //设置是否展示视频
//                    .filterGif() //设置是否过滤gif图片
                .maxCount(9) //设置最大选择图片数目(默认为1，单选)
//                    .singleType() //设置图片视频不能同时选择
                .imageQualityUser()
                .imagePaths(mImagePaths) //设置历史选择记录
                .request(getXActivity(), REQUEST_SELECT_IMAGES_CODE) //REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode

        })

        adapter.addData(TitleItem("选择多张图片") {
            ImagePicker.start()
//                    .showCamera() //设置是否显示拍照按钮
                .showImage() //设置是否展示图片
                .showVideo() //设置是否展示视频
//                    .filterGif() //设置是否过滤gif图片
                .maxCount(12) //设置最大选择图片数目(默认为1，单选)
//                    .singleType() //设置图片视频不能同时选择
                .imageQualityUser()
                .imagePaths(mImagePaths) //设置历史选择记录
                .request(getXActivity(), REQUEST_SELECT_IMAGES_CODE) //REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK && null != data) {
            mImagePaths = ImagePicker.onActivityResult(data)
            val stringBuffer = StringBuffer()
            stringBuffer.append("当前选中图片路径：\n\n")
            for (i in mImagePaths!!.indices) {
                stringBuffer.append(mImagePaths!![i].trimIndent())
            }
            LogUtils.d(stringBuffer.toString())
            ToastUtils.showShort(stringBuffer.toString())
        }
    }

}