package me.ibore.utils

import android.annotation.SuppressLint
import me.ibore.utils.Utils.app
import me.ibore.utils.UtilsBridge.FileHead
import me.ibore.utils.UtilsBridge.isSpace
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
     * @param crashDir The directory of saving crash information.
     */
    fun init(crashDir: File) {
        init(crashDir.absolutePath, null)
    }

    /**
     * Initialization
     *
     * @param onCrashListener The crash listener.
     */
    fun init(onCrashListener: OnCrashListener?) {
        init("", onCrashListener)
    }

    /**
     * Initialization
     *
     * @param crashDir        The directory of saving crash information.
     * @param onCrashListener The crash listener.
     */
    fun init(crashDir: File, onCrashListener: OnCrashListener?) {
        init(crashDir.absolutePath, onCrashListener)
    }

    /**
     * Initialization
     *
     * @param crashDirPath    The directory's path of saving crash information.
     * @param onCrashListener The crash listener.
     */
    @JvmOverloads
    fun init(crashDirPath: String = "", onCrashListener: OnCrashListener? = null) {
        val dirPath: String = if (isSpace(crashDirPath)) {
            if (SDCardUtils.isSDCardEnableByEnvironment
                && app.getExternalFilesDir(null) != null
            ) app.getExternalFilesDir(null)
                .toString() + FILE_SEP + "crash" + FILE_SEP else {
                app.filesDir.toString() + FILE_SEP + "crash" + FILE_SEP
            }
        } else {
            if (crashDirPath.endsWith(FILE_SEP)) crashDirPath else crashDirPath + FILE_SEP
        }
        Thread.setDefaultUncaughtExceptionHandler(
            getUncaughtExceptionHandler(
                dirPath,
                onCrashListener
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getUncaughtExceptionHandler(
        dirPath: String,
        onCrashListener: OnCrashListener?
    ): Thread.UncaughtExceptionHandler {
        return Thread.UncaughtExceptionHandler { t, e ->
            val time = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())
            val info = CrashInfo(time, e)
            onCrashListener?.onCrash(info)
            val crashFile = "$dirPath$time.txt"
            FileIOUtils.writeFileFromString(crashFile, info.toString(), true)
            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(t, e)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnCrashListener {
        fun onCrash(crashInfo: CrashInfo?)
    }

    class CrashInfo internal constructor(time: String, val throwable: Throwable) {

        private val mFileHeadProvider: FileHead = FileHead("Crash")

        fun addExtraHead(extraHead: Map<String, String>?) {
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