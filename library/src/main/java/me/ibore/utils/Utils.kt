package me.ibore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import me.ibore.ktx.logD

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
            LifecycleUtils.INSTANCE.init(sApp!!)
            return
        }
        if (sApp == app) return
        LifecycleUtils.INSTANCE.unInit(sApp!!)
        sApp = app
        LifecycleUtils.INSTANCE.init(sApp!!)
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
            init(LifecycleUtils.INSTANCE.applicationByReflect!!)
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

    open class ActivityLifecycleCallbacks {
        open fun onActivityCreated(@NonNull activity: Activity) {
        }

        open fun onActivityStarted(@NonNull activity: Activity) {
        }

        open fun onActivityResumed(@NonNull activity: Activity) {
        }

        open fun onActivityPaused(@NonNull activity: Activity) {
        }

        open fun onActivityStopped(@NonNull activity: Activity) {
        }

        open fun onActivityDestroyed(@NonNull activity: Activity) {
        }

        open fun onLifecycleChanged(@NonNull activity: Activity, event: Lifecycle.Event) {
        }
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