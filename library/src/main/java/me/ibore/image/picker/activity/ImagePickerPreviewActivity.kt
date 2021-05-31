package me.ibore.image.picker.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.databinding.XActivityImagePickerPreviewBinding
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.adapter.ImagePreviewAdapter
import me.ibore.image.picker.adapter.ImageSelectAdapter
import me.ibore.image.picker.model.MediaFile
import me.ibore.image.picker.utils.ImagePickerUtils
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.layoutmanager.CenterLayoutManager
import me.ibore.recycler.listener.OnItemChildClickListener
import me.ibore.recycler.listener.OnItemClickListener
import me.ibore.utils.BarUtils
import me.ibore.utils.ToastUtils


class ImagePickerPreviewActivity : XActivity<XActivityImagePickerPreviewBinding>() {

    private var mPosition = 0
    private lateinit var mMediaFileList: MutableList<MediaFile>
    private var mSelectAdapter = ImageSelectAdapter()
    private var mPreviewAdapter = ImagePreviewAdapter()

    override fun onBindConfig() {
        super.onBindConfig()
        BarUtils.setStatusBarLightMode(this, false)
    }

    override fun XActivityImagePickerPreviewBinding.onBindView(
        bundle: Bundle?,
        savedInstanceState: Bundle?
    ) {
        mPosition = bundle!!.getInt("position", 0)
        mMediaFileList = bundle.getParcelableArrayList<MediaFile>("datas") as MutableList<MediaFile>
        ivImagePickerBack.setOnClickListener { finish() }
        viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvTitleBarTitle.text =
                    String.format("%d/%d", position + 1, mMediaFileList.size)
                mBinding.updateImageSelect()
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
                        ToastUtils.showShort(R.string.image_picker_single_type_choose)
                        return@OnClickListener
                    }
                }
            }

            val addSuccess = ImagePickerUtils.addSelectList(getCurrentMediaFile())
            if (addSuccess) {
                updateImageSelect()
            } else {
                ToastUtils.showShort(R.string.image_picker_select_max, ImagePickerUtils.maxCount)
            }
        })
        tvImageCommit.setOnClickListener {
            setResult(RESULT_OK, Intent())
            finish()
        }

        viewPager2.adapter = mPreviewAdapter
        mPreviewAdapter.onItemChildClickListener =
            object : OnItemChildClickListener<RecyclerHolder, MediaFile> {
                override fun onItemClick(
                    holder: RecyclerHolder, id: Int, data: MediaFile, dataPosition: Int
                ) {
                    if (id == R.id.iv_picker_image) {
                        val height = titleBar.height.toFloat()
                        if (titleBar.isVisible) {
                            val tbAnimator = ObjectAnimator
                                .ofFloat(titleBar, "translationY", 0f, -height)
                                .setDuration(300)
                            tbAnimator.doOnEnd {
                                titleBar.isGone = true
                                BarUtils.setStatusBarVisibility(getXActivity(), false)
                            }
                            tbAnimator.start()
                            val bbAnimator = ObjectAnimator
                                .ofFloat(bottomBar, "alpha", 1f, 0f)
                                .setDuration(300)
                            bbAnimator.doOnEnd {
                                bottomBar.isGone = true
                            }
                            bbAnimator.start()
                        } else {
                            titleBar.isVisible = true
                            BarUtils.setStatusBarVisibility(getXActivity(), true)
                            ObjectAnimator.ofFloat(titleBar, "translationY", -height, 0f)
                                .setDuration(300).start()
                            bottomBar.isVisible = true
                            ObjectAnimator.ofFloat(bottomBar, "alpha", 0f, 1f)
                                .setDuration(300).start()
                        }
                    }
                }
            }

        rvImageSelect.layoutManager =
            CenterLayoutManager(getXActivity(), LinearLayoutManager.HORIZONTAL, false)
        rvImageSelect.adapter = mSelectAdapter
        mSelectAdapter.setDatas(ImagePickerUtils.selectMedias)
        mSelectAdapter.onItemClickListener =
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
        val imageQuality = ImagePicker.getConfig().imageQuality
        llOriginalImage.isGone = imageQuality == 1 || imageQuality == 2
        llOriginalImage.setOnClickListener {
            if (ImagePicker.getConfig().imageQuality == 3) {
                ImagePicker.getConfig().imageQuality = 4
            } else {
                ImagePicker.getConfig().imageQuality = 3
            }
            mBinding.updateOriginalImageView(ImagePicker.getConfig().imageQuality)
        }
        mBinding.updateOriginalImageView(ImagePicker.getConfig().imageQuality)
    }

    private fun getCurrentMediaFile(): MediaFile {
        return mMediaFileList[mBinding.viewPager2.currentItem]
    }

    override fun onBindData() {
        mBinding.tvTitleBarTitle.text = String.format("%d/%d", mPosition + 1, mMediaFileList.size)
        mPreviewAdapter.setDatas(mMediaFileList)
        mBinding.viewPager2.setCurrentItem(mPosition, false)
    }

    private fun XActivityImagePickerPreviewBinding.updateImageSelect() {
        updateCommitView()
        rvImageSelect.visibility =
            if (ImagePickerUtils.selectMedias.isNullOrEmpty()) View.GONE else View.VISIBLE
        if (ImagePickerUtils.isSelectContains(getCurrentMediaFile())) {
            val index = ImagePickerUtils.selectMedias.indexOf(getCurrentMediaFile())
            mSelectAdapter.currentPosition = index
            rvImageSelect.smoothScrollToPosition(index)
            ivImageCheck.setImageResource(R.drawable.x_image_picker_preview_checked)
            ivImageCheck.setBackgroundResource(R.drawable.image_picker_checked)
        } else {
            mSelectAdapter.currentPosition = -1
            ivImageCheck.setImageDrawable(null)
            ivImageCheck.setBackgroundResource(R.drawable.image_picker_check)
        }
        mSelectAdapter.notifyDataSetChanged()
    }

    private fun XActivityImagePickerPreviewBinding.updateCommitView() {
        val selectCount = ImagePickerUtils.selectMedias.size
        if (selectCount == 0) {
            tvImageCommit.isEnabled = false
            tvImageCommit.text = getString(R.string.image_picker_confirm)
        } else if (selectCount <= ImagePickerUtils.maxCount) {
            tvImageCommit.isEnabled = true
            tvImageCommit.text =
                getString(R.string.image_picker_confirm_msg, selectCount, ImagePickerUtils.maxCount)
        }
    }

    private fun XActivityImagePickerPreviewBinding.updateOriginalImageView(imageQuality: Int) {
        if (imageQuality == 3) {
            ivOriginalImage.setImageDrawable(null)
            tvOriginalImage.isEnabled = false
        } else if (imageQuality == 4) {
            ivOriginalImage.setImageDrawable(null)
            tvOriginalImage.isEnabled = true
        }
    }

}

