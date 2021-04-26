package me.ibore.demo.http

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.base.XObserver
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityHttpBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.http.model.Weather
import me.ibore.demo.model.TitleItem
import me.ibore.http.XHttp
import me.ibore.http.progress.Progress
import me.ibore.http.progress.ProgressListener
import me.ibore.loading.XLoading
import me.ibore.utils.DialogUtils
import me.ibore.utils.GsonUtils
import me.ibore.utils.LogUtils
import me.ibore.utils.ToastUtils
import java.io.File

class HttpActivity : BaseActivity<ActivityHttpBinding>() {

    private lateinit var xHttp: XHttp
    private var adapter = TitleAdapter()

    override fun ActivityHttpBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        recyclerView.adapter = adapter

        xHttp = XHttp.Builder(applicationContext)
                .header("appVersion", "100")
                .header("appSystem", "Android")
                .header("appType", "1")
                .header("appDevice", "Xiaomi 10 Pro")
                .header("appUuid", "BuEYM9hMcvgW6GXP89gh5AMi18d2pKzu")
                .header("appToken", "1EE7EEB4B3BB4F158EA86135782F56C3")
                .builder()
    }

    override fun onBindData() {

        adapter.addData(TitleItem("接收String") {
            val map = LinkedHashMap<String, String>()
            map["areaCode"] = "86"
            map["mobile"] = "18519332274"
            map["type"] = "1"
            xHttp.post(this, "http://english.ibore.top/app/sms/sendSmsCode")
                    .body(GsonUtils.toJson(map))
                    .observable(object : XObserver<String>(XLoading.DIALOG_DIALOG) {
                        override fun onSuccess(data: String) {
                            DialogUtils.showAlert(getXActivity(), content = data)
                        }
                    })
        })
        adapter.addData(TitleItem("接收Model") {
            xHttp.get(this, "http://t.weather.sojson.com/api/weather/city/")
                    .appendUrl("101030100")
                    .observable(object : XObserver<Weather>(XLoading.DIALOG_TOAST) {
                        override fun onSuccess(data: Weather) {
                            DialogUtils.showAlert(getXActivity(), content = GsonUtils.toJson(data))
                        }
                    })
        })
        adapter.addData(TitleItem("下载图片") {
            xHttp.get(this, "https://t9.baidu.com/it/u=2268908537,2815455140&fm=79&app=86&f=JPEG?w=1280&h=719")
                    .observable(object : XObserver<Bitmap>(XLoading.DIALOG_TOAST) {
                        override fun onSuccess(data: Bitmap) {
                            //mBinding.ivImage.setImageBitmap(data)
                        }
                    })
        })
        adapter.addData(TitleItem("下载文件") {
            xHttp.get(this, "https://mmgrapp-75037.gzc.vod.tencent-cloud.com/secure/GodDresser/1/2/3/102027/tencentmobilemanager_20210118114917_8.10.0_android_build6688_102027.apk")
                    .download(object : ProgressListener {
                        override fun onProgress(progress: Progress) {
                            ToastUtils.showShort(progress.toString())
                        }
                    })
                    .observable(object : XObserver<File>(XLoading.DIALOG_TOAST) {
                        override fun onSuccess(data: File) {
                            DialogUtils.showAlert(getXActivity(), content = data.absolutePath)
                        }
                    })
        })
        adapter.addData(TitleItem("上传文件") {
            xHttp.post(this, "https://english.ibore.me/api/user/avatar")
                    .param("header", File(filesDir, "1486631099150286149.jpg"))
                    .upload(object : ProgressListener {
                        override fun onProgress(progress: Progress) {
                            LogUtils.d(progress)
                        }
                    })
                    .observable(object : XObserver<String>(XLoading.DIALOG_TOAST) {
                        override fun onSuccess(data: String) {
                            DialogUtils.showAlert(getXActivity(), content = data)
                        }
                    })
        })
    }
}
