package me.ibore.utils

import android.Manifest.permission
import android.app.Activity
import android.app.Application
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.view.View
import androidx.core.app.NotificationCompat
import me.ibore.utils.NotificationUtils.ChannelConfig
import me.ibore.utils.ShellUtils.CommandResult
import me.ibore.utils.Utils.OnAppStatusChangedListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.util.*

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2020/03/19
 * desc  :
</pre> *
 */
internal object UtilsBridge {

    ///////////////////////////////////////////////////////////////////////////
    // Common
    ///////////////////////////////////////////////////////////////////////////
    internal class FileHead(private val mName: String) {
        private val mFirst = LinkedHashMap<String, String>()
        private val mLast = LinkedHashMap<String, String>()
        fun addFirst(key: String, value: String) {
            append2Host(mFirst, key, value)
        }

        fun append(extra: Map<String, String>?) {
            append2Host(mLast, extra)
        }

        fun append(key: String, value: String) {
            append2Host(mLast, key, value)
        }

        private fun append2Host(host: MutableMap<String, String>, extra: Map<String, String>?) {
            if (extra == null || extra.isEmpty()) {
                return
            }
            for ((key, value) in extra) {
                append2Host(host, key, value)
            }
        }

        private fun append2Host(host: MutableMap<String, String>, key: String, value: String) {
            var keyTemp = key
            if (keyTemp.isEmpty() || value.isEmpty()) {
                return
            }
            val delta = 19 - keyTemp.length // 19 is length of "Device Manufacturer"
            if (delta > 0) {
                keyTemp += "                   ".substring(0, delta)
            }
            host[keyTemp] = value
        }

        val appended: String
            get() {
                val sb = StringBuilder()
                for ((key, value) in mLast) {
                    sb.append(key).append(": ").append(value).append("\n")
                }
                return sb.toString()
            }

        override fun toString(): String {
            val sb = StringBuilder()
            val border = "************* $mName Head ****************\n"
            sb.append(border)
            for ((key, value) in mFirst) {
                sb.append(key).append(": ").append(value).append("\n")
            }
            sb.append("Rom Info           : ").append(RomUtils.romInfo).append("\n")
            sb.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n")
            sb.append("Device Model       : ").append(Build.MODEL).append("\n")
            sb.append("Android Version    : ").append(Build.VERSION.RELEASE).append("\n")
            sb.append("Android SDK        : ").append(Build.VERSION.SDK_INT).append("\n")
            sb.append("App VersionName    : ").append(AppUtils.getAppVersionName()).append("\n")
            sb.append("App VersionCode    : ").append(AppUtils.getAppVersionCode()).append("\n")
            sb.append(appended)
            return sb.append(border).append("\n").toString()
        }
    }
}