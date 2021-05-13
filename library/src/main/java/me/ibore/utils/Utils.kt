package me.ibore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.Lifecycle
import me.ibore.ktx.logD
import me.ibore.utils.ThreadUtils.SimpleTask
import me.ibore.utils.UtilsBridge.applicationByReflect

/**
 * <pre>
 * author:
 * ___           ___           ___         ___
 * _____                       /  /\         /__/\         /__/|       /  /\
 * /  /::\                     /  /::\        \  \:\       |  |:|      /  /:/
 * /  /:/\:\    ___     ___    /  /:/\:\        \  \:\      |  |:|     /__/::\
 * /  /:/~/::\  /__/\   /  /\  /  /:/~/::\   _____\__\:\   __|  |:|     \__\/\:\
 * /__/:/ /:/\:| \  \:\ /  /:/ /__/:/ /:/\:\ /__/::::::::\ /__/\_|:|____    \  \:\
 * \  \:\/:/~/:/  \  \:\  /:/  \  \:\/:/__\/ \  \:\~~\~~\/ \  \:\/:::::/     \__\:\
 * \  \::/ /:/    \  \:\/:/    \  \::/       \  \:\  ~~~   \  \::/~~~~      /  /:/
 * \  \:\/:/      \  \::/      \  \:\        \  \:\        \  \:\         /__/:/
 * \  \::/        \__\/        \  \:\        \  \:\        \  \:\        \__\/
 * \__\/                       \__\/         \__\/         \__\/
 * blog  : http://blankj.com
 * time  : 16/12/08
 * desc  : utils about initialization
</pre> *
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

    val packageName: String
        get() = app.packageName

    val applicationInfo: ApplicationInfo
        get() = app.applicationInfo

    val packageManager: PackageManager
        get() = app.packageManager

    val contentResolver: ContentResolver
        get() = app.contentResolver

    @JvmStatic
    fun <T> doAsync(task: Task<T>): Task<T> {
        ThreadUtils.getCachedPool().execute(task)
        return task
    }

    abstract class Task<Result>(private val mConsumer: Consumer<Result>?) : SimpleTask<Result>() {
        override fun onSuccess(result: Result) {
            mConsumer?.accept(result)
        }
    }

    interface OnAppStatusChangedListener {
        fun onForeground(activity: Activity?)
        fun onBackground(activity: Activity?)
    }

    open class ActivityLifecycleCallbacks {
        open fun onActivityCreated(activity: Activity) {
        }

        open fun onActivityStarted(activity: Activity) {
        }

        open fun onActivityResumed(activity: Activity) {
        }

        open fun onActivityPaused(activity: Activity) {
        }

        open fun onActivityStopped(activity: Activity) {
        }

        open fun onActivityDestroyed(activity: Activity) {
        }

        open fun onLifecycleChanged(activity: Activity, event: Lifecycle.Event?) {
        }
    }

    @JvmStatic
    val SP: SPUtils
        get() = SPUtils.getInstance("Utils")

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