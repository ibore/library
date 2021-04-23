package me.ibore.image.picker.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.databinding.ActivityImagePickerPreviewBinding
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.adapter.ImagePreviewAdapter
import me.ibore.image.picker.adapter.ImageSelectAdapter
import me.ibore.image.picker.model.MediaFile
import me.ibore.image.picker.utils.ImagePickerUtils
import me.ibore.ktx.color
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.layoutmanager.CenterLayoutManager
import me.ibore.recycler.listener.OnItemChildClickListener
import me.ibore.recycler.listener.OnItemClickListener
import me.ibore.utils.BarUtils
import me.ibore.utils.ColorUtils
import me.ibore.utils.ToastUtils


class ImagePickerPreviewActivity : XActivity<ActivityImagePickerPreviewBinding>() {

    private var mPosition = 0
    private lateinit var mMediaFileList: MutableList<MediaFile>
    private var mImageSelectAdapter = ImageSelectAdapter()
    private var mImagePreviewAdapter = ImagePreviewAdapter()

    override fun onBindConfig() {
        super.onBindConfig()
        BarUtils.setStatusBarLightMode(this, false)
    }

    override fun ActivityImagePickerPreviewBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        mPosition = bundle!!.getInt("position", 0)
        mMediaFileList = bundle.getParcelableArrayList<MediaFile>("datas") as MutableList<MediaFile>
        ivImagePickerBack.setOnClickListener { finish() }
        bottomBar.setBackgroundColor(
            ColorUtils.setAlpha(color(R.color.image_picker_bar_color), 0.3F)
        )
        viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvTitleBarTitle.text =
                    String.format("%d/%d", position + 1, mMediaFileList.size)
                updateImageSelect()
            }
        })
        llPreSelect.setOnClickListener(View.OnClickListener { //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
            if (ImagePicker.getConfig().singleType) {
                if (ImagePickerUtils.selectMedias.isNotEmpty()) {
                    //判断选中集合中第一项是否为视频
                    if (!ImagePickerUtils.isCanAddSelectionPaths(
                            getCurrentMediaFile(),
                            ImagePickerUtils.selectMedias.first()
                        )
                    ) {
                        //类型不同
                        Toast.makeText(getXActivity(), getString(R.string.image_picker_single_type_choose), Toast.LENGTH_SHORT).show()
                        return@OnClickListener
                    }
                }
            }

            val addSuccess = ImagePickerUtils.addImageToSelectList(getCurrentMediaFile())
            if (addSuccess) {
                updateImageSelect()
            } else {
                ToastUtils.showShort(R.string.image_picker_select_max, ImagePickerUtils.maxCount)
            }
        })
        tvImagePickerCommit.setOnClickListener {
            setResult(RESULT_OK, Intent())
            finish()
        }

        //mBinding.viewPager2.adapter = mImageSelectAdapter
        mImagePreviewAdapter.onItemChildClickListener =
            object : OnItemChildClickListener<RecyclerHolder, MediaFile> {
                override fun onItemClick(
                    holder: RecyclerHolder, id: Int, data: MediaFile, dataPosition: Int
                ) {
                    if (id == R.id.iv_picker_image) {
                        if (titleBar.isVisible) {
                            titleBar.isGone = true
                            bottomBar.isGone = true
                        } else {
                            titleBar.isVisible = true
                            bottomBar.isVisible = true
                        }
                    }
                }
            }

        rvImageSelect.layoutManager =
            CenterLayoutManager(getXActivity(), LinearLayoutManager.HORIZONTAL, false)
        rvImageSelect.adapter = mImageSelectAdapter
        mImageSelectAdapter.setDatas(ImagePickerUtils.selectMedias)
        mImageSelectAdapter.onItemClickListener =
            object : OnItemClickListener<RecyclerHolder, MediaFile> {
                override fun onItemClick(
                    holder: RecyclerHolder, data: MediaFile, dataPosition: Int
                ) {
                    val indexOf = mMediaFileList.indexOf(data)
                    if (indexOf >= 0) {
                        viewPager2.currentItem = indexOf
                    }
                }
            }

    }

    private fun getCurrentMediaFile(): MediaFile {
        return mMediaFileList[mBinding.viewPager2.currentItem]
    }

    override fun onBindData() {
        mBinding.tvTitleBarTitle.text = String.format("%d/%d", mPosition + 1, mMediaFileList.size)
        mImagePreviewAdapter.setDatas(mMediaFileList)
        mBinding.viewPager2.setCurrentItem(mPosition, false)
    }

    private fun updateImageSelect() {
        ImagePickerUtils.updateCommitView(mBinding.tvImagePickerCommit)
        mBinding.rvImageSelect.visibility =
            if (ImagePickerUtils.selectMedias.isNullOrEmpty()) View.GONE else View.VISIBLE
        if (ImagePickerUtils.isSelectContains(getCurrentMediaFile())) {
            val index = ImagePickerUtils.selectMedias.indexOf(getCurrentMediaFile())
            mImageSelectAdapter.currentPosition = index
            mBinding.rvImageSelect.smoothScrollToPosition(index)
            mBinding.ivImagePickerCheck.setImageResource(R.drawable.image_picker_preview_checked)
            mBinding.ivImagePickerCheck.setBackgroundResource(R.drawable.image_picker_checked)
        } else {
            mImageSelectAdapter.currentPosition = -1
            mBinding.ivImagePickerCheck.setImageDrawable(null)
            mBinding.ivImagePickerCheck.setBackgroundResource(R.drawable.image_picker_check)
        }
        mImageSelectAdapter.notifyDataSetChanged()
    }

}