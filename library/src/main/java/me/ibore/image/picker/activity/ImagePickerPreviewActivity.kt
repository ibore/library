package me.ibore.image.picker.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.databinding.ActivityImagePickerPreviewBinding
import me.ibore.databinding.ItemImagePickerPreviewSelectBinding
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.adapter.ImageSelectAdapter
import me.ibore.image.picker.model.MediaFile
import me.ibore.image.picker.utils.ImagePickerUtils
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.layoutmanager.CenterLayoutManager
import me.ibore.recycler.listener.OnItemClickListener
import me.ibore.utils.ColorUtils


class ImagePickerPreviewActivity : XActivity<ActivityImagePickerPreviewBinding>() {

    private var mPosition = 0
    private lateinit var mMediaFileList: MutableList<MediaFile>
    private var mImageSelectAdapter = ImageSelectAdapter()

    override fun ActivityImagePickerPreviewBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        mPosition = bundle!!.getInt("position", 0)
        mMediaFileList = bundle.getParcelableArrayList<MediaFile>("datas") as MutableList<MediaFile>
        ivImagePickerBack.setOnClickListener { finish() }

        bottomBar.setBackgroundColor(
            ColorUtils.setAlpha(
                ContextCompat.getColor(
                    getXActivity(),
                    R.color.image_picker_bar_color
                ), 0.3F
            )
        )
        contentView.registerOnPageChangeCallback(object :
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
                Toast.makeText(
                    getXActivity(),
                    String.format(
                        getString(R.string.image_picker_select_max),
                        ImagePickerUtils.maxCount
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        tvImagePickerCommit.setOnClickListener {
            setResult(RESULT_OK, Intent())
            finish()
        }
        rvImageSelect.layoutManager =
            CenterLayoutManager(getXActivity(), LinearLayoutManager.HORIZONTAL, false)
        rvImageSelect.adapter = mImageSelectAdapter
        mImageSelectAdapter.setDatas(ImagePickerUtils.selectMedias)
        mImageSelectAdapter.onItemClickListener = object : OnItemClickListener<BindingHolder<ItemImagePickerPreviewSelectBinding>, MediaFile> {
            override fun onItemClick(
                holder: BindingHolder<ItemImagePickerPreviewSelectBinding>,
                data: MediaFile,
                position: Int
            ) {
                val indexOf = mMediaFileList.indexOf(data)
                if (indexOf >= 0) {
                    contentView.currentItem = indexOf
                }
            }
        }

    }

    private fun getCurrentMediaFile(): MediaFile {
        return mMediaFileList[mBinding.contentView.currentItem]
    }

    override fun onBindData() {

        mBinding.tvTitleBarTitle.text = String.format("%d/%d", mPosition + 1, mMediaFileList.size)

        mBinding.contentView.adapter = object : RecyclerView.Adapter<RecyclerHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
                return RecyclerHolder.create(parent, R.layout.item_image_picker_preview)
            }

            override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
//                val pinchImageView = holder.viewHolder.view<PinchImageView>(R.id.iv_picker_image)
//                pinchImageView.reset()
//                holder.viewHolder.image(R.id.iv_picker_image, mMediaFileList[position].path)
//                pinchImageView.setOnClickListener {
//                    if (binding.titleBar.visibility == View.VISIBLE) {
//                        binding.titleBar.visibility = View.GONE
//                        binding.bottomBar.visibility = View.GONE
//                    } else {
//                        binding.titleBar.visibility = View.VISIBLE
//                        binding.bottomBar.visibility = View.VISIBLE
//                    }
//                }
//                if (mMediaFileList[position].duration > 0) {
//                    holder.viewHolder.visibility(R.id.iv_image_picker_play, View.VISIBLE)
//                } else {
//                    holder.viewHolder.visibility(R.id.iv_image_picker_play, View.GONE)
//                }
//                holder.viewHolder.onClickListener(R.id.iv_image_picker_play) {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    val uri = UriUtils.file2Uri(File(getCurrentMediaFile().path))
//                    intent.setDataAndType(uri, "video/*")
//                    //给所有符合跳转条件的应用授权
//                    val resInfoList = packageManager
//                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//                    for (resolveInfo in resInfoList) {
//                        val packageName = resolveInfo.activityInfo.packageName
//                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                    }
//                    startActivity(intent)
//                }
            }

            override fun getItemCount(): Int = mMediaFileList.size
        }
        mBinding.contentView.setCurrentItem(mPosition, false)
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