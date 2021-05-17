package me.ibore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import me.ibore.ktx.logD
import me.ibore.utils.ThreadUtils.SimpleTask
import me.ibore.utils.UtilsBridge.applicationByReflect

/**
 * utils about initialization
 */
object Utils {

    @SuppressLint("StaticFieldLeak")
    private var sApp: Application? = null

    /**
     * Init utils.
     *
     * Init it in the class of UtilsFileProvider.
     *
     * @param app application
     */
    fun init(app: Application) {
        if (sApp == null) {
            sApp = app
            UtilsActivityLifecycleImpl.INSTANCE.init(sApp!!)
            return
        }
        if (sApp == app) return
        UtilsActivityLifecycleImpl.INSTANCE.unInit(sApp!!)
        sApp = app
        UtilsActivityLifecycleImpl.INSTANCE.init(sApp!!)
        CrashUtils.init("", object : CrashUtils.OnCrashListener {
            override fun onCrash(crashInfo: CrashUtils.CrashInfo) {
                logD(crashInfo.toString())
            }
        })
    }

    /**
     * Return the Application object.
     *
     * Main process get app by UtilsFileProvider,
     * and other process get app by reflect.
     *
     * @return the Application object
     */
    @JvmStatic
    val app: Application
        get() {
            if (sApp != null) return sApp!!
            init(applicationByReflect!!)
            if (sApp == null) throw NullPointerException("reflect failed.")
            logD("${ProcessUtils.currentProcessName} reflect app success.")
            return sApp!!
        }

    @JvmStatic
    val packageName: String
        get() = app.packageName

    @JvmStatic
    val applicationInfo: ApplicationInfo
        get() = app.applicationInfo

    @JvmStatic
    val packageManager: PackageManager
        get() = app.packageManager

    @JvmStatic
    val contentResolver: ContentResolver
        get() = app.contentResolver

    @JvmStatic
    val sp: SPUtils
        get() = SPUtils.getInstance("Utils")

    interface OnAppStatusChangedListener {
        fun onForeground(activity: Activity)
        fun onBackground(activity: Activity)
    }

    interface ActivityLifecycleCallbacks {
        fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        fun onActivityStarted(activity: Activity) {}
        fun onActivityResumed(activity: Activity) {}
        fun onActivityPaused(activity: Activity) {}
        fun onActivityStopped(activity: Activity) {}
        fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        fun onActivityDestroyed(activity: Activity) {}
    }

    interface Consumer<T> {
        fun accept(t: T)
    }

    interface Supplier<T> {
        fun get(): T
    }

    interface Func1<Ret, Par> {
        fun call(param: Par): Ret
    }
}