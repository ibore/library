package me.ibore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.core.content.FileProvider
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * detail: 开发工具类
 */
object XUtils {
    // 日志 TAG
    val TAG: String = XUtils::class.java.simpleName

    // 全局 Application 对象
    @JvmStatic
    private var sApplication: Application? = null

    /**
     * 判断是否 Debug 模式
     *
     * @return `true` yes, `false` no
     */
    // 是否内部 Debug 模式
    const val isDebug = false

    /**
     * 初始化方法 ( 必须调用 )
     *
     * @param context [Context]
     */
    @JvmStatic
    fun init(context: Context) {
        // 初始化全局 Application
        initApplication(context)
        // 注册 Activity 生命周期监听
        registerActivityLifecycleCallbacks(sApplication)
        // 初始化 Record
        //AnalysisRecordUtils.init()
        // 初始化 Toast
        //DevToast.init(context)
    }

    /**
     * 初始化全局 Application
     *
     * @param context [Context]
     */
    private fun initApplication(context: Context?) {
        if (sApplication == null && context != null) {
            try {
                sApplication = context.applicationContext as Application
            } catch (e: Exception) {
            }
        }
    }

    /**
     * 获取全局 Context
     *
     * @return [Context]
     */
    @JvmStatic
    val context: Context
        get() = application.applicationContext

    /**
     * 获取全局 Application
     *
     * @return [Application]
     */
    @JvmStatic
    val application: Application
        get() {
            if (sApplication != null) return sApplication!!
            val application = applicationByReflect
            init(application)
            return application
        }
    // =
    /**
     * 反射获取 Application
     *
     * @return [Application]
     * @throws NullPointerException
     */
    @JvmStatic
    private val applicationByReflect: Application
        get() {
            try {
                @SuppressLint("PrivateApi") val activityThread =
                    Class.forName("android.app.ActivityThread")
                val thread = activityThread.getMethod("currentActivityThread").invoke(null)
                val app = activityThread.getMethod("getApplication").invoke(thread)
                    ?: throw NullPointerException("u should init first")
                return app as Application
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getApplicationByReflect")
            }
            throw NullPointerException("u should init first")
        }
    // =
    /**
     * 获取 Handler
     *
     * @return [Handler]
     */
    @JvmStatic
    val handler: Handler
        get() = HandlerUtils.mainHandler

    /**
     * 执行 UI 线程任务 ( 延时执行 )
     *
     * @param runnable    线程任务
     * @param delayMillis 延时执行时间 ( 毫秒 )
     */
    @JvmStatic
    @JvmOverloads
    fun runOnUiThread(runnable: Runnable?, delayMillis: Long = 0) {
        HandlerUtils.postRunnable(runnable, delayMillis)
    }

    /**
     * 开启日志开关
     */
    fun openLog() {
        LogUtils.setPrintLog(true)
    }

    /**
     * 标记 Debug 模式
     */
    fun openDebug() {
        sDebug = true
    }

    // =================
    // = Activity 监听 =
    // =================
    // ActivityLifecycleCallbacks 实现类, 监听 Activity
    @JvmStatic
    private val ACTIVITY_LIFECYCLE = ActivityLifecycleImpl()

    // Activity 过滤判断接口
    @JvmStatic
    private var sActivityLifecycleFilter: ActivityLifecycleFilter? = null

    // 权限 Activity.class name
    const val PERMISSION_ACTIVITY_CLASS_NAME =
        "dev.utils.app.permission.PermissionUtils\$PermissionActivity"

    /**
     * 注册绑定 Activity 生命周期事件处理
     *
     * @param application [Application]
     */
    @JvmStatic
    private fun registerActivityLifecycleCallbacks(application: Application) {
        // 先移除监听
        unregisterActivityLifecycleCallbacks(application)
        // 绑定新的监听
        application.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
    }

    /**
     * 解除注册 Activity 生命周期事件处理
     *
     * @param application [Application]
     */
    private fun unregisterActivityLifecycleCallbacks(application: Application) {
        application.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
    }
    // ===============
    // = 对外公开方法 =
    // ===============
    /**
     * 获取 Activity 生命周期 相关信息获取接口类
     *
     * @return [ActivityLifecycleGet]
     */
    val activityLifecycleGet: ActivityLifecycleGet
        get() = ACTIVITY_LIFECYCLE

    /**
     * 获取 Activity 生命周期 事件监听接口类
     *
     * @return [ActivityLifecycleNotify]
     */
    val activityLifecycleNotify: ActivityLifecycleNotify
        get() = ACTIVITY_LIFECYCLE

    /**
     * 获取 Top Activity
     *
     * @return [Activity]
     */
    val topActivity: Activity?
        get() = ACTIVITY_LIFECYCLE.topActivity

    /**
     * 设置 Activity 生命周期 过滤判断接口
     *
     * @param activityLifecycleFilter Activity 过滤判断接口
     */
    fun setActivityLifecycleFilter(activityLifecycleFilter: ActivityLifecycleFilter) {
        sActivityLifecycleFilter = activityLifecycleFilter
    }

    // ===========
    // = 接口实现 =
    // ===========
    // 内部 Activity 生命周期过滤处理
    private val ACTIVITY_LIFECYCLE_FILTER: ActivityLifecycleFilter =
        object : ActivityLifecycleFilter {
            override fun filter(activity: Activity?): Boolean {
                if (activity != null) {
                    if (PERMISSION_ACTIVITY_CLASS_NAME == activity.javaClass.getName()) {
                        // 如果相同则不处理 ( 该页面为内部权限框架, 申请权限页面 )
                        return true
                    } else {
                        if (sActivityLifecycleFilter != null) {
                            return sActivityLifecycleFilter.filter(activity)
                        }
                    }
                }
                return false
            }
        }

    // =
    // ActivityLifecycleCallbacks 抽象类
    private var sAbstractActivityLifecycle: AbstractActivityLifecycle? = null

    /**
     * 设置 ActivityLifecycle 监听回调
     *
     * @param abstractActivityLifecycle Activity 生命周期监听类
     */
    fun setAbstractActivityLifecycle(abstractActivityLifecycle: AbstractActivityLifecycle) {
        sAbstractActivityLifecycle = abstractActivityLifecycle
    }

    // ================
    // = FileProvider =
    // ================
    // 获取 lib utils fileProvider
    const val LIB_FILE_PROVIDER = "devapp.provider"

    /**
     * 获取 FileProvider Authority
     *
     * @return FileProvider Authority
     */
    val authority: String?
        get() {
            try {
                return getContext().getPackageName().toString() + "." + LIB_FILE_PROVIDER
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getAuthority")
            }
            return null
        }

    /**
     * 获取 FileProvider File Uri
     *
     * @param file 文件
     * @return 指定文件 [Uri]
     */
    fun getUriForFile(file: File?): Uri {
        return UriUtils.getUriForFile(file, authority)
    }

    /**
     * 获取 FileProvider File Path Uri
     *
     * @param filePath 文件路径
     * @return 指定文件 [Uri]
     */
    fun getUriForPath(filePath: String?): Uri {
        return UriUtils.getUriForFile(FileUtils.getFileByPath(filePath), authority)
    }
    // ===========
    // = 接口相关 =
    // ===========
    /**
     * detail: 对 Activity 的生命周期事件进行集中处理, ActivityLifecycleCallbacks 实现方法
     *
     * @author Ttt
     */
    private class ActivityLifecycleImpl : Application.ActivityLifecycleCallbacks,
        ActivityLifecycleGet, ActivityLifecycleNotify {
        // 保存未销毁的 Activity
        private val mActivityLists: LinkedList<Activity> = LinkedList<Activity>()

        // APP 状态改变事件
        private val mStatusListenerMaps: MutableMap<Any, OnAppStatusChangedListener> =
            ConcurrentHashMap()

        // Activity 销毁事件
        private val mDestroyedListenerMaps: MutableMap<Activity, MutableSet<OnActivityDestroyedListener>> =
            ConcurrentHashMap<Activity, MutableSet<OnActivityDestroyedListener>>()

        // 前台 Activity 总数
        private var mForegroundCount = 0

        // Activity Configuration 改变次数
        private var mConfigCount = 0

        /**
         * 判断应用是否在后台 ( 不可见 )
         *
         * @return `true` yes, `false` no
         */
        override var isBackground = false
            private set

        // ==============================
        // = ActivityLifecycleCallbacks =
        // ==============================
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            topActivity = activity
            sAbstractActivityLifecycle?.onActivityCreated(activity, savedInstanceState)
        }

        override fun onActivityStarted(activity: Activity) {
            if (!isBackground) {
                topActivity = activity
            }
            if (mConfigCount < 0) {
                ++mConfigCount
            } else {
                ++mForegroundCount
            }
            sAbstractActivityLifecycle?.onActivityStarted(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            topActivity = activity
            // Activity 准备可见, 设置为非后台 Activity
            if (isBackground) {
                isBackground = false
                postStatus(true)
            }
            sAbstractActivityLifecycle?.onActivityResumed(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            sAbstractActivityLifecycle?.onActivityPaused(activity)
        }

        override fun onActivityStopped(activity: Activity) {
            // 检测当前的 Activity 是否因为 Configuration 的改变被销毁了
            if (activity.isChangingConfigurations) {
                --mConfigCount
            } else {
                --mForegroundCount
                if (mForegroundCount <= 0) {
                    isBackground = true
                    postStatus(false)
                }
            }
            sAbstractActivityLifecycle?.onActivityStopped(activity)
        }

        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle
        ) {
            sAbstractActivityLifecycle?.onActivitySaveInstanceState(activity, outState)
        }

        override fun onActivityDestroyed(activity: Activity) {
            mActivityLists.remove(activity)
            // 通知 Activity 销毁
            consumeOnActivityDestroyedListener(activity)
            // 修复软键盘内存泄漏 在 Activity.onDestroy() 中使用
            KeyBoardUtils.fixSoftInputLeaks(activity)
            sAbstractActivityLifecycle?.onActivityDestroyed(activity)
        }
        // ===================
        // = 内部处理判断方法 =
        // ===================
        /**
         * 反射获取栈顶 Activity
         *
         * @return [Activity]
         */
        private val topActivityByReflect: Activity?
            get() {
                try {
                    @SuppressLint("PrivateApi") val activityThreadClass =
                        Class.forName("android.app.ActivityThread")
                    val activityThread =
                        activityThreadClass.getMethod("currentActivityThread").invoke(null)
                    val activitiesField = activityThreadClass.getDeclaredField("mActivityLists")
                    activitiesField.isAccessible = true
                    val activities = activitiesField[activityThread] as Map<*, *>
                        ?: return null
                    for (activityRecord in activities.values) {
                        val activityRecordClass: Class<*> = activityRecord.javaClass
                        val pausedField = activityRecordClass.getDeclaredField("paused")
                        pausedField.isAccessible = true
                        if (!pausedField.getBoolean(activityRecord)) {
                            val activityField = activityRecordClass.getDeclaredField("activity")
                            activityField.isAccessible = true
                            return activityField[activityRecord] as Activity
                        }
                    }
                } catch (e: Exception) {
                    LogUtils.eTag(TAG, e, "getTopActivityByReflect")
                }
                return null
            }
        // =============================
        // = ActivityLifecycleGet 方法 =
        // =============================
        /**
         * 获取最顶部 ( 当前或最后一个显示 ) Activity
         *
         * @return [Activity]
         */// 判断是否过滤 Activity
        // 判断是否已经包含该 Activity
        /**
         * 保存 Activity 栈顶
         *
         * @param activity [Activity]
         */
        override var topActivity: Activity?
            get() {
                if (!mActivityLists.isEmpty()) {
                    val topActivity: Activity? = mActivityLists.getLast()
                    if (topActivity != null) {
                        return topActivity
                    }
                }
                val topActivityByReflect: Activity? = topActivityByReflect
                if (topActivityByReflect != null) {
                    topActivity = topActivityByReflect
                }
                return topActivityByReflect
            }
            private set(activity) {
                // 判断是否过滤 Activity
                if (ACTIVITY_LIFECYCLE_FILTER.filter(activity)) return
                // 判断是否已经包含该 Activity
                if (mActivityLists.contains(activity)) {
                    if (mActivityLists.getLast() != activity) {
                        mActivityLists.remove(activity)
                        mActivityLists.addLast(activity)
                    }
                } else {
                    mActivityLists.addLast(activity)
                }
            }

        /**
         * 判断某个 Activity 是否 Top Activity
         *
         * @param activityClassName Activity.class.getCanonicalName()
         * @return `true` yes, `false` no
         */
        override fun isTopActivity(activityClassName: String): Boolean {
            if (!TextUtils.isEmpty(activityClassName)) {
                val activity: Activity? = topActivity
                // 判断是否类是否一致
                return activity != null && activity.javaClass.canonicalName == activityClassName
            }
            return false
        }

        /**
         * 判断某个 Class(Activity) 是否 Top Activity
         *
         * @param clazz Activity.class or this.getClass()
         * @return `true` yes, `false` no
         */
        override fun isTopActivity(clazz: Class<*>?): Boolean {
            if (clazz != null) {
                val activity: Activity? = topActivity
                // 判断是否类是否一致
                return activity != null && activity.javaClass.canonicalName == clazz.canonicalName
            }
            return false
        }

        /**
         * 获取 Activity 总数
         *
         * @return 已打开 Activity 总数
         */
        override val activityCount: Int
            get() = mActivityLists.size
        // ===========================
        // = ActivityLifecycleNotify =
        // ===========================
        /**
         * 添加 APP 状态改变事件监听
         *
         * @param any   key
         * @param listener APP 状态改变监听事件
         */
        override fun addOnAppStatusChangedListener(
            any: Any,
            listener: OnAppStatusChangedListener
        ) {
            mStatusListenerMaps[any] = listener
        }

        /**
         * 移除 APP 状态改变事件监听
         *
         * @param any key
         */
        override fun removeOnAppStatusChangedListener(any: Any) {
            mStatusListenerMaps.remove(any)
        }

        /**
         * 移除全部 APP 状态改变事件监听
         */
        override fun removeAllOnAppStatusChangedListener() {
            mStatusListenerMaps.clear()
        }
        // =
        /**
         * 添加 Activity 销毁通知事件
         *
         * @param activity [Activity]
         * @param listener Activity 销毁通知事件
         */
        override fun addOnActivityDestroyedListener(
            activity: Activity?,
            listener: OnActivityDestroyedListener?
        ) {
            if (activity == null || listener == null) return
            val listeners: MutableSet<OnActivityDestroyedListener>
            if (!mDestroyedListenerMaps.containsKey(activity)) {
                listeners = HashSet()
                mDestroyedListenerMaps[activity] = listeners
            } else {
                listeners = mDestroyedListenerMaps[activity]!!
                if (listeners.contains(listener)) return
            }
            listeners.add(listener)
        }

        /**
         * 移除 Activity 销毁通知事件
         *
         * @param activity [Activity]
         */
        override fun removeOnActivityDestroyedListener(activity: Activity?) {
            if (activity == null) return
            mDestroyedListenerMaps.remove(activity)
        }

        /**
         * 移除全部 Activity 销毁通知事件
         */
        override fun removeAllOnActivityDestroyedListener() {
            mDestroyedListenerMaps.clear()
        }
        // ===============
        // = 事件通知相关 =
        // ===============
        /**
         * 发送状态改变通知
         *
         * @param isForeground 是否在前台
         */
        private fun postStatus(isForeground: Boolean) {
            if (mStatusListenerMaps.isEmpty()) return
            // 保存到新的集合, 防止 ConcurrentModificationException
            val lists: List<OnAppStatusChangedListener> = ArrayList(mStatusListenerMaps.values)
            // 遍历通知
            for (listener in lists) {
                if (listener != null) {
                    if (isForeground) {
                        listener.onForeground()
                    } else {
                        listener.onBackground()
                    }
                }
            }
        }

        /**
         * 通知 Activity 销毁, 并且消费 ( 移除 ) 监听事件
         *
         * @param activity [Activity]
         */
        private fun consumeOnActivityDestroyedListener(activity: Activity) {
            try {
                // 保存到新的集合, 防止 ConcurrentModificationException
                val sets: Set<OnActivityDestroyedListener> = HashSet(
                    mDestroyedListenerMaps[activity]
                )
                // 遍历通知
                for (listener in sets) {
                    listener?.onActivityDestroyed(activity)
                }
            } catch (e: Exception) {
            }
            // 移除已消费的事件
            removeOnActivityDestroyedListener(activity)
        }
    }

    /**
     * detail: Activity 生命周期 相关信息获取接口
     *
     * @author Ttt
     */
    interface ActivityLifecycleGet {
        /**
         * 获取最顶部 ( 当前或最后一个显示 ) Activity
         *
         * @return [Activity]
         */
        val topActivity: Activity?

        /**
         * 判断某个 Activity 是否 Top Activity
         *
         * @param activityClassName Activity.class.getCanonicalName()
         * @return `true` yes, `false` no
         */
        fun isTopActivity(activityClassName: String): Boolean

        /**
         * 判断某个 Class(Activity) 是否 Top Activity
         *
         * @param clazz Activity.class or this.getClass()
         * @return `true` yes, `false` no
         */
        fun isTopActivity(clazz: Class<*>?): Boolean

        /**
         * 判断应用是否在后台 ( 不可见 )
         *
         * @return `true` yes, `false` no
         */
        val isBackground: Boolean

        /**
         * 获取 Activity 总数
         *
         * @return 已打开 Activity 总数
         */
        val activityCount: Int
    }

    /**
     * detail: Activity 生命周期 过滤判断接口
     *
     * @author Ttt
     */
    interface ActivityLifecycleFilter {
        /**
         * 判断是否过滤该类 ( 不进行添加等操作 )
         *
         * @param activity [Activity]
         * @return `true` yes, `false` no
         */
        fun filter(activity: Activity?): Boolean
    }

    /**
     * detail: Activity 生命周期 通知接口
     *
     * @author Ttt
     */
    interface ActivityLifecycleNotify {
        /**
         * 添加 APP 状态改变事件监听
         *
         * @param any   key
         * @param listener APP 状态改变监听事件
         */
        fun addOnAppStatusChangedListener(any: Any, listener: OnAppStatusChangedListener)

        /**
         * 移除 APP 状态改变事件监听
         *
         * @param any key
         */
        fun removeOnAppStatusChangedListener(any: Any)

        /**
         * 移除全部 APP 状态改变事件监听
         */
        fun removeAllOnAppStatusChangedListener()
        // =
        /**
         * 添加 Activity 销毁通知事件
         *
         * @param activity [Activity]
         * @param listener Activity 销毁通知事件
         */
        fun addOnActivityDestroyedListener(
            activity: Activity?,
            listener: OnActivityDestroyedListener?
        )

        /**
         * 移除 Activity 销毁通知事件
         *
         * @param activity [Activity]
         */
        fun removeOnActivityDestroyedListener(activity: Activity?)

        /**
         * 移除全部 Activity 销毁通知事件
         */
        fun removeAllOnActivityDestroyedListener()
    }

    /**
     * detail: APP 状态改变事件
     *
     * @author Ttt
     */
    interface OnAppStatusChangedListener {
        /**
         * 切换到前台
         */
        fun onForeground()

        /**
         * 切换到后台
         */
        fun onBackground()
    }

    /**
     * detail: Activity 销毁事件
     *
     * @author Ttt
     */
    interface OnActivityDestroyedListener {
        /**
         * Activity 销毁通知
         *
         * @param activity [Activity]
         */
        fun onActivityDestroyed(activity: Activity?)
    }

    /**
     * detail: ActivityLifecycleCallbacks 抽象类
     *
     * @author Ttt
     */
    abstract class AbstractActivityLifecycle : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    /**
     * detail: FileProvider
     *
     * @author Ttt
     */
    class FileProviderDevApp : FileProvider() {
        override fun onCreate(): Boolean {
            init(context!!)
            return true
        }
    }
}