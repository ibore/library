package me.ibore.update

import android.app.IntentService
import android.content.Intent
import me.ibore.http.XHttp
import me.ibore.http.progress.Progress
import me.ibore.http.progress.ProgressListener
import me.ibore.utils.AppUtils
import java.io.File

class UpdateService : IntentService("UpdateService") {

    override fun onHandleIntent(intent: Intent?) {
        val update: Update = intent!!.getParcelableExtra("update")!!
        val file: File = XHttp.getDefault().get(update.apkUrl)
                .uiThread(false)
                .download(object : ProgressListener {
                    override fun onProgress(progress: Progress) {

                    }
                })
                .execute(File::class.java)
        AppUtils.installApp(file)
    }

}