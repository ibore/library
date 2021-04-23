package me.ibore.image.picker.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.base.XObserver
import me.ibore.databinding.ActivityImagePickerBinding
import me.ibore.image.picker.ImagePicker
import me.ibore.image.picker.adapter.ImageFoldersAdapter
import me.ibore.image.picker.adapter.ImagePickerAdapter
import me.ibore.image.picker.model.MediaFile
import me.ibore.image.picker.model.MediaFolder
import me.ibore.image.picker.observable.MediaObservable
import me.ibore.image.picker.utils.ImagePickerUtils
import me.ibore.image.picker.utils.MediaFileUtil
import me.ibore.ktx.color
import me.ibore.loading.XLoading
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.listener.OnItemClickListener
import me.ibore.utils.*
import java.io.File

class ImagePickerActivity : XActivity<ActivityImagePickerBinding>(), ImagePickerAdapter.OnMediaListener {

    companion object {
        /**
         * 大图预览页相关
         */
        private const val REQUEST_SELECT_IMAGES_CODE = 0x01 //用于在大图预览页中点击提交按钮标识
        private const val REQUEST_CODE_CAPTURE = 0x02 //点击拍照标识

        /**
         * 权限相关
         */
        private const val REQUEST_PERMISSION_CAMERA_CODE = 0x03
    }

    private lateinit var gridLayoutManager: GridLayoutManager
    private var mImagePickerAdapter = ImagePickerAdapter()

    //是否显示时间
    private var showTime = false
    private val mMyHandler = Handler()
    private val mHideRunnable = Runnable {
        hideImageTime()
    }

    private var mFilePath: String? = null
    private var mImageFoldersAdapter = ImageFoldersAdapter()
    private var mFolderHeight =
        ScreenUtils.appScreenHeight - Utils.app.resources.getDimensionPixelSize(R.dimen.image_picker_action_bar_height) * 3

    override fun onBindConfig() {
        super.onBindConfig()
        BarUtils.setStatusBarLightMode(this, false)
    }

    override fun ActivityImagePickerBinding.onBindView(
        bundle: Bundle?,
        savedInstanceState: Bundle?
    ) {
        val timeBgColor = ColorUtils.setAlpha(color(R.color.image_picker_bar_color), 0.8F)
        tvImagePickerTime.setBackgroundColor(timeBgColor)
        //列表相关
        gridLayoutManager = GridLayoutManager(getXActivity(), 4)
        recyclerView.layoutManager = gridLayoutManager
        mImagePickerAdapter.onMediaListener = this@ImagePickerActivity

        rvFolderPicker.layoutManager = LinearLayoutManager(getXActivity())
        rvFolderPicker.adapter = mImageFoldersAdapter

        val layoutParams = rvFolderPicker.layoutParams
        layoutParams.height = mFolderHeight
        rvFolderPicker.layoutParams = layoutParams

        mImageFoldersAdapter.onItemClickListener =
            object : OnItemClickListener<RecyclerHolder, MediaFolder> {
                override fun onItemClick(
                    holder: RecyclerHolder, data: MediaFolder, dataPosition: Int
                ) {
                    showOrHideFolderView(data)
                }
            }
        recyclerView.adapter = mImagePickerAdapter
        ivImagePickerBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        tvImagePickerCommit.setOnClickListener { commitSelection(ImagePickerUtils.getSelectPaths()) }
        llFolder.setOnClickListener { showOrHideFolderView(null) }
        llImagePickerFolder.setOnClickListener { showOrHideFolderView(null) }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                updateImageTime()
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateImageTime()
            }
        })
        tvPickerPreview.setOnClickListener {
            val intent = Intent(getXActivity(), ImagePickerPreviewActivity::class.java)
            intent.putExtra("position", 0)
            intent.putParcelableArrayListExtra("datas", ImagePickerUtils.selectMedias)
            startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE)
        }
    }

    override fun onBindData() {
        ImagePickerUtils.restSelect()
        //进行权限的判断
        val hasPermission = (ContextCompat.checkSelfPermission(getXActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getXActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CAMERA_CODE)
        } else {
            startScannerTask()
        }
    }

    private fun startScannerTask() {
        DisposablesUtils.add(this, Observable.create(MediaObservable(applicationContext)),
                object : XObserver<MutableList<MediaFolder>>(XLoading.DIALOG_TOAST) {
                    override fun onSuccess(data: MutableList<MediaFolder>) {
                        if (data.isNotEmpty()) {
                            //默认加载全部照片
                            mImageFoldersAdapter.setDatas(data)
                            if (data.size > 0) {
                                showImagePicker(data[0])
                            }
                            ImagePickerUtils.updateCommitView(mBinding.tvImagePickerCommit)
                        }
                    }
                })
    }

    private fun showImagePicker(data: MediaFolder) {
        mBinding.tvImagePickerFolder.text = data.folderName
        mImagePickerAdapter.setDatas(data.mediaFileList)
    }

    // 权限申请回调
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults.isNotEmpty()) {
                val cameraResult = grantResults[0] //相机权限
                val sdResult = grantResults[1] //sd卡权限
                val cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED //拍照权限
                val sdGranted = sdResult == PackageManager.PERMISSION_GRANTED //拍照权限
                if (cameraGranted && sdGranted) {
                    //具有拍照权限，sd卡权限，开始扫描任务
                    startScannerTask()
                } else {
                    //没有权限
                    ToastUtils.showShort(R.string.image_picker_permission_tip)
                    finish()
                }
            }
        }
    }

    private fun ActivityImagePickerBinding.showOrHideFolderView(data: MediaFolder?) {
        val isShow = !llFolder.isVisible
        val animator = ValueAnimator.ofInt(0, mFolderHeight).setDuration(300)
//        if (isShow) {
//            animator.interpolator = DecelerateInterpolator()
//        } else {
//            animator.interpolator = AccelerateInterpolator()
//        }
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            if (isShow) {
                if (value == 0) {
                    llFolder.isVisible = true
                    llFolder.setBackgroundColor(Color.TRANSPARENT)
                }
                rvFolderPicker.translationY = (value - mFolderHeight).toFloat()
                val alpha = value * 0.8f / mFolderHeight
                llFolder.setBackgroundColor(ColorUtils.setAlpha(Color.BLACK, alpha))
                ivImageFolderIndicator.rotation = 180f * value / mFolderHeight
            } else {
                rvFolderPicker.translationY = -value.toFloat()
                val alpha = (mFolderHeight - value) * 0.8f / mFolderHeight
                llFolder.setBackgroundColor(ColorUtils.setAlpha(Color.BLACK, alpha))
                ivImageFolderIndicator.rotation = 180 + (180f * value / mFolderHeight)
                if (value == mFolderHeight) {
                    llFolder.isGone = true
                    data?.let { showImagePicker(data) }
                }
            }
        }
        animator.start()
    }

    private fun showImageTime() {
        if (showTime) {
            showTime = false
            ObjectAnimator.ofFloat(mBinding.tvImagePickerTime, "alpha", 1f, 0f)
                .setDuration(300).start()
        }
    }

    // 隐藏时间
    private fun hideImageTime() {
        if (!showTime) {
            showTime = true
            ObjectAnimator.ofFloat(mBinding.tvImagePickerTime, "alpha", 0f, 1f)
                .setDuration(300).start()
        }
    }

    // 更新时间
    private fun updateImageTime() {
        val position = gridLayoutManager.findFirstVisibleItemPosition()
        if (position != RecyclerView.NO_POSITION && !(ImagePicker.getConfig().showCamera && position == 0)) {
            if (mBinding.tvImagePickerTime.visibility != View.VISIBLE) {
                mBinding.tvImagePickerTime.visibility = View.VISIBLE
            }
            val time = ImagePickerUtils.getImageTime(mImagePickerAdapter.getData(position).dateToken)
            mBinding.tvImagePickerTime.text = time
            showImageTime()
            mMyHandler.removeCallbacks(mHideRunnable)
            mMyHandler.postDelayed(mHideRunnable, 1500)
        }
    }

    // 打开相机
    override fun onCameraClick() {
        if (ImagePickerUtils.isSelectOutRange()) {
            ToastUtils.showShort(R.string.image_picker_select_max, ImagePicker.getConfig().maxCount)
            return
        }
        if (ImagePicker.getConfig().singleType) {
            //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
            if (ImagePickerUtils.selectMedias.isNotEmpty()) {
                if (MediaFileUtil.isVideoFileType(ImagePickerUtils.selectMedias.first().path)) {
                    //如果存在视频，就不能拍照了
                    ToastUtils.showShort(R.string.image_picker_single_type_choose)
                    return
                }
            }
        }

        //拍照存放路径
        val fileDir = File(Environment.getExternalStorageDirectory(), "Pictures")
        if (!fileDir.exists()) {
            fileDir.mkdir()
        }
        mFilePath = fileDir.absolutePath + "/IMG_" + System.currentTimeMillis() + ".jpg"
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri: Uri? = UriUtils.file2Uri(File(mFilePath!!))
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_CODE_CAPTURE)
    }

    // 点击图片
    override fun onMediaClick(view: View, data: MediaFile, dataPosition: Int) {
        val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeClipRevealAnimation(
                view, view.width / 2, view.height / 2, 0, 0)
        val intent = Intent(this, ImagePickerPreviewActivity::class.java)
        intent.putExtra("position", dataPosition)
        intent.putParcelableArrayListExtra("datas", mImagePickerAdapter.getDatas() as ArrayList<MediaFile>)
        startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE, optionsCompat.toBundle())
    }

    // 选中/取消选中图片
    override fun onMediaCheck(view: View, data: MediaFile, dataPosition: Int) {
        if (ImagePicker.getConfig().singleType) {
            //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
            if (ImagePickerUtils.selectMedias.isNotEmpty()) {
                //判断选中集合中第一项是否为视频
                if (!ImagePickerUtils.isCanAddSelectionPaths(data, ImagePickerUtils.selectMedias.first())) {
                    //类型不同
                    ToastUtils.showShort(getString(R.string.image_picker_single_type_choose))
                    return
                }
            }
        }
        val indexOf = ImagePickerUtils.indexOfSelect(data)
        if (indexOf >= 0) {
            val notifyList = ArrayList(ImagePickerUtils.selectMedias.subList(indexOf, ImagePickerUtils.selectMedias.size))
            ImagePickerUtils.removeSelect(data)
            if (!notifyList.isNullOrEmpty()) {
                for (mediaFile in notifyList) {
                    mImagePickerAdapter.notifyItemChanged(mImagePickerAdapter.getDatas().indexOf(mediaFile) + mImagePickerAdapter.getDifference())
                }
            }
        } else if (!ImagePickerUtils.isSelectOutRange()) {
            ImagePickerUtils.addSelect(data)
            ImagePickerUtils.updateCommitView(mBinding.tvImagePickerCommit)
            ImagePickerUtils.updatePreviewView(mBinding.tvPickerPreview)
            mImagePickerAdapter.notifyItemChanged(dataPosition + mImagePickerAdapter.getDifference())
        } else {
            ToastUtils.showShort(R.string.image_picker_select_max, ImagePicker.getConfig().maxCount)
        }
    }

    /**
     * 拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE) {
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$mFilePath")))
                commitSelection(arrayListOf(mFilePath!!))
            }
            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                commitSelection(ImagePickerUtils.getSelectPaths())
            }
        }
    }


    /**
     * 选择图片完毕，返回
     */
    private fun commitSelection(selectPaths: ArrayList<String>) {
        val intent = Intent()
        intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, selectPaths)
        setResult(RESULT_OK, intent)
        ImagePickerUtils.clearSelect()
        finish()
    }

    override fun onResume() {
        super.onResume()
        mImagePickerAdapter.notifyDataSetChanged()
        ImagePickerUtils.updateCommitView(mBinding.tvImagePickerCommit)
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        ImagePickerUtils.clearSelect()
        super.onBackPressed()
    }

}