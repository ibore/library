package me.ibore.utils

import android.annotation.SuppressLint
import me.ibore.utils.UtilsBridge.FileHead
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/27
 * desc  : utils about crash
</pre> *
 */
object CrashUtils {

    private val FILE_SEP = System.getProperty("file.separator")

    private val DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler()

    /**
     * Initialization
     *
     * @param crashDir        The directory of saving crash information.
     * @param onCrashListener The crash listener.
     */
    @JvmStatic
    @JvmOverloads
    fun init(crashDir: File, onCrashListener: OnCrashListener? = null) {
        init(crashDir.absolutePath, onCrashListener)
    }

    /**
     * Initialization
     *
     * @param crashDirPath    The directory's path of saving crash information.
     * @param onCrashListener The crash listener.
     */
    @JvmStatic
    @JvmOverloads
    @SuppressLint("SimpleDateFormat")
    fun init(crashDirPath: String = "", onCrashListener: OnCrashListener? = null) {
        val dirPath: String = if (crashDirPath.isBlank()) {
            if (SDCardUtils.isSDCardEnableByEnvironment && Utils.app.getExternalFilesDir(null) != null)
                Utils.app.getExternalFilesDir(null).toString() + FILE_SEP + "crash" + FILE_SEP
            else Utils.app.filesDir.toString() + FILE_SEP + "crash" + FILE_SEP
        } else {
            if (crashDirPath.endsWith(FILE_SEP)) crashDirPath else crashDirPath + FILE_SEP
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            val time = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())
            val info = CrashInfo(time, e)
            onCrashListener?.onCrash(info)
            val crashFile = "$dirPath$time.txt"
            FileIOUtils.writeFileFromString(crashFile, info.toString(), true)
            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(t, e)
        }
    }

    interface OnCrashListener {
        fun onCrash(crashInfo: CrashInfo)
    }

    class CrashInfo internal constructor(time: String, val throwable: Throwable) {

        private val mFileHeadProvider: FileHead = FileHead("Crash")

        fun addExtraHead(extraHead: Map<String, String>) {
            mFileHeadProvider.append(extraHead)
        }

        fun addExtraHead(key: String, value: String) {
            mFileHeadProvider.append(key, value)
        }

        override fun toString(): String {
            return mFileHeadProvider.toString() +
                    ThrowableUtils.getFullStackTrace(throwable)
        }

        init {
            mFileHeadProvider.addFirst("Time Of Crash", time)
        }
    }
}