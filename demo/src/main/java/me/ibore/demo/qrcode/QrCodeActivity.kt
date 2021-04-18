package me.ibore.demo.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import io.reactivex.Observable
import me.ibore.base.XObserver
import me.ibore.demo.R
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityQrcodeBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.ktx.dp2px
import me.ibore.loading.XLoading
import me.ibore.qrcode.QRCodeEncoder
import me.ibore.utils.DisposablesUtils
import me.ibore.utils.ImageUtils
import me.ibore.utils.LogUtils

class QrCodeActivity : BaseActivity<ActivityQrcodeBinding>() {

    override fun ActivityQrcodeBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))

    }

    override fun onBindData() {
        mBinding.btnGenerateQrCode.setOnClickListener {
            DisposablesUtils.add(this, Observable.create {
                val bitmap = QRCodeEncoder.syncEncodeQRCode("https://www.baidu.com",
                        dp2px(160F), foregroundColor = Color.RED, logo = ImageUtils.getBitmap(R.drawable.refresh_wechat))!!
                it.onNext(bitmap)
                it.onComplete()
            }, object : XObserver<Bitmap>(XLoading.DIALOG_DIALOG) {
                override fun onSuccess(data: Bitmap) {
                    LogUtils.d("ddddddddd")
                    mBinding.ivImage.setImageBitmap(data)
                }
            })
        }


    }
}
