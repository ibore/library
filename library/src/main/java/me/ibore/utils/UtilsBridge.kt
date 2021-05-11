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

    @JvmStatic
    fun addOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        UtilsActivityLifecycleImpl.INSTANCE.addOnAppStatusChangedListener(listener)
    }

    @JvmStatic
    fun removeOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        UtilsActivityLifecycleImpl.INSTANCE.removeOnAppStatusChangedListener(listener)
    }

    @JvmStatic
    fun addActivityLifecycleCallbacks(callbacks: Utils.ActivityLifecycleCallbacks) {
        UtilsActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(callbacks)
    }

    @JvmStatic
    fun removeActivityLifecycleCallbacks(callbacks: Utils.ActivityLifecycleCallbacks) {
        UtilsActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(callbacks)
    }

    @JvmStatic
    fun addActivityLifecycleCallbacks(
        activity: Activity,
        callbacks: Utils.ActivityLifecycleCallbacks
    ) {
        UtilsActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(activity, callbacks)
    }

    @JvmStatic
    fun removeActivityLifecycleCallbacks(activity: Activity) {
        UtilsActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity)
    }

    @JvmStatic
    fun removeActivityLifecycleCallbacks(
        activity: Activity,
        callbacks: Utils.ActivityLifecycleCallbacks
    ) {
        UtilsActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity, callbacks)
    }

    @JvmStatic
    val activityList: List<Activity>
        get() = UtilsActivityLifecycleImpl.INSTANCE.activityList

    @JvmStatic
    val applicationByReflect: Application?
        get() = UtilsActivityLifecycleImpl.INSTANCE.applicationByReflect

    @JvmStatic
    val isAppForeground: Boolean
        get() = UtilsActivityLifecycleImpl.INSTANCE.isAppForeground


    ///////////////////////////////////////////////////////////////////////////
    // ConvertUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun bytes2HexString(bytes: ByteArray): String {
        return ConvertUtils.bytes2HexString(bytes)
    }

    @JvmStatic
    fun hexString2Bytes(hexString: String?): ByteArray {
        return ConvertUtils.hexString2Bytes(hexString)
    }

    @JvmStatic
    fun string2Bytes(string: String?): ByteArray? {
        return ConvertUtils.string2Bytes(string)
    }

    @JvmStatic
    fun bytes2String(bytes: ByteArray?): String? {
        return ConvertUtils.bytes2String(bytes)
    }

    @JvmStatic
    fun jsonObject2Bytes(jsonObject: JSONObject?): ByteArray? {
        return ConvertUtils.jsonObject2Bytes(jsonObject)
    }

    @JvmStatic
    fun bytes2JSONObject(bytes: ByteArray?): JSONObject? {
        return ConvertUtils.bytes2JSONObject(bytes)
    }

    @JvmStatic
    fun jsonArray2Bytes(jsonArray: JSONArray?): ByteArray? {
        return ConvertUtils.jsonArray2Bytes(jsonArray)
    }

    @JvmStatic
    fun bytes2JSONArray(bytes: ByteArray?): JSONArray? {
        return ConvertUtils.bytes2JSONArray(bytes)
    }

    @JvmStatic
    fun parcelable2Bytes(parcelable: Parcelable?): ByteArray? {
        return ConvertUtils.parcelable2Bytes(parcelable)
    }

    @JvmStatic
    fun <T> bytes2Parcelable(
        bytes: ByteArray,
        creator: Parcelable.Creator<T>
    ): T? {
        return ConvertUtils.bytes2Parcelable(bytes, creator)
    }

    @JvmStatic
    fun serializable2Bytes(serializable: Serializable?): ByteArray? {
        return ConvertUtils.serializable2Bytes(serializable)
    }

    @JvmStatic
    fun bytes2Object(bytes: ByteArray?): Any? {
        return ConvertUtils.bytes2Object(bytes)
    }

    @JvmStatic
    fun byte2FitMemorySize(byteSize: Long): String {
        return ConvertUtils.byte2FitMemorySize(byteSize)
    }

    @JvmStatic
    fun inputStream2Bytes(inputStream: InputStream?): ByteArray? {
        return ConvertUtils.inputStream2Bytes(inputStream)
    }

    @JvmStatic
    fun input2OutputStream(inputStream: InputStream?): ByteArrayOutputStream? {
        return ConvertUtils.input2OutputStream(inputStream)
    }

    @JvmStatic
    @JvmOverloads
    fun inputStream2Lines(inputStream: InputStream?, charsetName: String? = null): List<String>? {
        return ConvertUtils.inputStream2Lines(inputStream, charsetName)
    }

    ///////////////////////////////////////////////////////////////////////////
    // DebouncingUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun isValid(view: View, duration: Long): Boolean {
        return DebouncingUtils.isValid(view, duration)
    }


    ///////////////////////////////////////////////////////////////////////////
    // ImageUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap): ByteArray {
        return ImageUtils.bitmap2Bytes(bitmap)
    }

    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap, format: CompressFormat, quality: Int): ByteArray {
        return ImageUtils.bitmap2Bytes(bitmap, format, quality)
    }

    @JvmStatic
    fun bytes2Bitmap(bytes: ByteArray): Bitmap {
        return ImageUtils.bytes2Bitmap(bytes)
    }

    @JvmStatic
    fun drawable2Bytes(drawable: Drawable): ByteArray {
        return ImageUtils.drawable2Bytes(drawable)
    }

    @JvmStatic
    fun drawable2Bytes(drawable: Drawable, format: CompressFormat, quality: Int): ByteArray {
        return ImageUtils.drawable2Bytes(drawable, format, quality)
    }

    @JvmStatic
    fun bytes2Drawable(bytes: ByteArray): Drawable {
        return ImageUtils.bytes2Drawable(bytes)
    }

    @JvmStatic
    fun view2Bitmap(view: View): Bitmap {
        return ImageUtils.view2Bitmap(view)
    }

    @JvmStatic
    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        return ImageUtils.drawable2Bitmap(drawable)
    }

    @JvmStatic
    fun bitmap2Drawable(bitmap: Bitmap): Drawable {
        return ImageUtils.bitmap2Drawable(bitmap)
    }


    ///////////////////////////////////////////////////////////////////////////
    // KeyboardUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun fixSoftInputLeaks(activity: Activity?) {
        KeyboardUtils.fixSoftInputLeaks(activity!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // NotificationUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun getNotification(
        channelConfig: ChannelConfig,
        consumer: Utils.Consumer<NotificationCompat.Builder?>?
    ): Notification {
        return NotificationUtils.getNotification(channelConfig, consumer)
    }

    ///////////////////////////////////////////////////////////////////////////
    // ProcessUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    val isMainProcess: Boolean
        get() = ProcessUtils.isMainProcess

    @JvmStatic
    val foregroundProcessName: String?
        get() = ProcessUtils.foregroundProcessName

    ///////////////////////////////////////////////////////////////////////////
    // ServiceUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun isServiceRunning(className: String?): Boolean {
        return ServiceUtils.isServiceRunning(className!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // ShellUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun execCmd(command: String, isRooted: Boolean): CommandResult {
        return ShellUtils.execCmd(command, isRooted)
    }

    ///////////////////////////////////////////////////////////////////////////
    // StringUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun isSpace(s: String?): Boolean {
        return StringUtils.isSpace(s)
    }

    @JvmStatic
    fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
        return StringUtils.equals(s1, s2)
    }

    ///////////////////////////////////////////////////////////////////////////
    // ThreadUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun <T> doAsync(task: Utils.Task<T>): Utils.Task<T> {
        ThreadUtils.getCachedPool().execute(task)
        return task
    }

    @JvmStatic
    private fun preLoad(vararg runs: Runnable) {
        for (r in runs) {
            ThreadUtils.getCachedPool().execute(r)
        }
    }

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