package me.ibore.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import me.ibore.ktx.logD
import me.ibore.utils.Utils.OnAppStatusChangedListener
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


@SuppressLint("StaticFieldLeak")
internal class LifecycleUtils : Application.ActivityLifecycleCallbacks {

    private val mActivityList: LinkedList<Activity> = LinkedList()
    private val mStatusListeners: MutableList<OnAppStatusChangedListener> = CopyOnWriteArrayList()
    private val mOnActivityCallbacksMap: MutableMap<Activity, MutableList<Utils.OnActivityCallbacks>?> =
        ConcurrentHashMap()
    private var mForegroundCount = 0
    private var mConfigCount = 0
    private var mIsBackground = false
    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    fun unInit(app: Application) {
        mActivityList.clear()
        app.unregisterActivityLifecycleCallbacks(this)
    }

    val topActivity: Activity?
        get() {
            val activityList = activityList
            for (activity in activityList) {
                if (!ActivityUtils.isActivityAlive(activity)) {
                    continue
                }
                return activity
            }
            return null
        }
    val activityList: List<Activity>
        get() {
            if (!mActivityList.isEmpty()) {
                return LinkedList(mActivityList)
            }
            val reflectActivities = activitiesByReflect
            mActivityList.addAll(reflectActivities)
            return LinkedList(mActivityList)
        }

    fun addOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        mStatusListeners.add(listener)
    }

    fun removeOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        mStatusListeners.remove(listener)
    }

    fun addActivityLifecycleCallbacks(listener: Utils.OnActivityCallbacks?) {
        addActivityLifecycleCallbacks(STUB, listener)
    }

    fun addActivityLifecycleCallbacks(activity: Activity?, listener: Utils.OnActivityCallbacks?) {
        if (activity == null || listener == null) return
        ThreadUtils.runOnUiThread {
            addActivityLifecycleCallbacksInner(activity, listener)
        }
    }

    val isAppForeground: Boolean
        get() = !mIsBackground

    private fun addActivityLifecycleCallbacksInner(
        activity: Activity, callbacks: Utils.OnActivityCallbacks
    ) {
        var callbacksList = mOnActivityCallbacksMap[activity]
        if (callbacksList == null) {
            callbacksList = CopyOnWriteArrayList()
            mOnActivityCallbacksMap[activity] = callbacksList
        } else {
            if (callbacksList.contains(callbacks)) return
        }
        callbacksList.add(callbacks)
    }

    fun removeActivityLifecycleCallbacks(callbacksOn: Utils.OnActivityCallbacks?) {
        removeActivityLifecycleCallbacks(STUB, callbacksOn)
    }

    fun removeActivityLifecycleCallbacks(activity: Activity?) {
        if (activity == null) return
        ThreadUtils.runOnUiThread { mOnActivityCallbacksMap.remove(activity) }
    }

    fun removeActivityLifecycleCallbacks(
        activity: Activity?,
        callbacksOn: Utils.OnActivityCallbacks?
    ) {
        if (activity == null || callbacksOn == null) return
        ThreadUtils.runOnUiThread {
            removeActivityLifecycleCallbacksInner(activity, callbacksOn)
        }
    }

    private fun removeActivityLifecycleCallbacksInner(
        activity: Activity,
        callbacksOn: Utils.OnActivityCallbacks
    ) {
        val callbacksList = mOnActivityCallbacksMap[activity]
        if (callbacksList != null && callbacksList.isNotEmpty()) {
            callbacksList.remove(callbacksOn)
        }
    }

    private fun consumeActivityLifecycleCallbacks(activity: Activity, event: Lifecycle.Event) {
        consumeLifecycle(activity, event, mOnActivityCallbacksMap[activity])
        consumeLifecycle(activity, event, mOnActivityCallbacksMap[STUB])
    }

    private fun consumeLifecycle(
        activity: Activity, event: Lifecycle.Event, listeners: List<Utils.OnActivityCallbacks>?
    ) {
        if (listeners == null) return
        for (listener in listeners) {
            listener.onLifecycleChanged(activity, event)
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    listener.onActivityCreated(activity)
                }
                Lifecycle.Event.ON_START -> {
                    listener.onActivityStarted(activity)
                }
                Lifecycle.Event.ON_RESUME -> {
                    listener.onActivityResumed(activity)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    listener.onActivityPaused(activity)
                }
                Lifecycle.Event.ON_STOP -> {
                    listener.onActivityStopped(activity)
                }
                Lifecycle.Event.ON_DESTROY -> {
                    listener.onActivityDestroyed(activity)
                }
            }
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            mOnActivityCallbacksMap.remove(activity)
        }
    }

    val applicationByReflect: Application?
        get() {
            try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val thread = activityThread
                val app =
                    activityThreadClass.getMethod("getApplication").invoke(thread) ?: return null
                return app as Application
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

    override fun onActivityCreated(@NonNull activity: Activity, savedInstanceState: Bundle?) {
        if (mActivityList.isEmpty()) {
            postStatus(activity, true)
        }
        LanguageUtils.applyLanguage(activity)
        setAnimatorsEnabled()
        setTopActivity(activity)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_CREATE)
    }

    override fun onActivityStarted(@NonNull activity: Activity) {
        if (!mIsBackground) {
            setTopActivity(activity)
        }
        if (mConfigCount < 0) {
            ++mConfigCount
        } else {
            ++mForegroundCount
        }
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_START)
    }

    override fun onActivityResumed(@NonNull activity: Activity) {
        setTopActivity(activity)
        if (mIsBackground) {
            mIsBackground = false
            postStatus(activity, true)
        }
        processHideSoftInputOnActivityDestroy(activity, false)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_RESUME)
    }

    override fun onActivityPaused(@NonNull activity: Activity) {
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_PAUSE)
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity.isChangingConfigurations) {
            --mConfigCount
        } else {
            --mForegroundCount
            if (mForegroundCount <= 0) {
                mIsBackground = true
                postStatus(activity, false)
            }
        }
        processHideSoftInputOnActivityDestroy(activity, true)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_STOP)
    }

    override fun onActivitySaveInstanceState(
        @NonNull activity: Activity,
        @NonNull outState: Bundle
    ) {
    }

    override fun onActivityDestroyed(@NonNull activity: Activity) {
        mActivityList.remove(activity)
        KeyboardUtils.fixSoftInputLeaks(activity)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_DESTROY)
    }

    override fun onActivityPostDestroyed(@NonNull activity: Activity) {
    }

    /**
     * To solve close keyboard when activity onDestroy.
     * The preActivity set windowSoftInputMode will prevent
     * the keyboard from closing when curActivity onDestroy.
     */
    private fun processHideSoftInputOnActivityDestroy(activity: Activity, isSave: Boolean) {
        try {
            if (isSave) {
                val window: Window = activity.window
                val attrs: WindowManager.LayoutParams = window.attributes
                val softInputMode = attrs.softInputMode
                window.decorView.setTag(-123, softInputMode)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            } else {
                val tag = activity.window.decorView.getTag(-123)
                if (tag !is Int) return
                ThreadUtils.runOnUiThreadDelayed({
                    try {
                        val window: Window? = activity.window
                        window?.setSoftInputMode(tag)
                    } catch (ignore: Exception) {
                    }
                }, 100)
            }
        } catch (ignore: Exception) {
        }
    }

    private fun postStatus(activity: Activity, isForeground: Boolean) {
        if (mStatusListeners.isEmpty()) return
        for (statusListener in mStatusListeners) {
            if (isForeground) {
                statusListener.onForeground(activity)
            } else {
                statusListener.onBackground(activity)
            }
        }
    }

    private fun setTopActivity(activity: Activity) {
        if (mActivityList.contains(activity)) {
            if (!mActivityList.first.equals(activity)) {
                mActivityList.remove(activity)
                mActivityList.addFirst(activity)
            }
        } else {
            mActivityList.addFirst(activity)
        }
    }

    /**
     * @return the activities which topActivity is first position
     */
    private val activitiesByReflect: List<Activity>
        get() {
            val list: LinkedList<Activity> = LinkedList()
            var topActivity: Activity? = null
            try {
                val activityThread = activityThread
                val mActivitiesField: Field =
                    activityThread!!.javaClass.getDeclaredField("mActivities")
                mActivitiesField.isAccessible = true
                val mActivities: Any = mActivitiesField.get(activityThread)
                if (mActivities !is Map<*, *>) {
                    return list
                }
                val binder_activityClientRecord_map = mActivities as Map<Any, Any>
                for (activityRecord in binder_activityClientRecord_map.values) {
                    val activityClientRecordClass: Class<*> = activityRecord.javaClass
                    val activityField: Field =
                        activityClientRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    val activity = activityField.get(activityRecord) as Activity
                    if (topActivity == null) {
                        val pausedField: Field =
                            activityClientRecordClass.getDeclaredField("paused")
                        pausedField.isAccessible = true
                        if (!pausedField.getBoolean(activityRecord)) {
                            topActivity = activity
                        } else {
                            list.add(activity)
                        }
                    } else {
                        list.add(activity)
                    }
                }
            } catch (e: Exception) {
                Log.e("UtilsActivityLifecycle", "getActivitiesByReflect: " + e.message)
            }
            if (topActivity != null) {
                list.addFirst(topActivity)
            }
            return list
        }
    private val activityThread: Any?
        get() {
            return activityThreadInActivityThreadStaticField
                ?: activityThreadInActivityThreadStaticMethod
        }

    private val activityThreadInActivityThreadStaticField: Any?
        get() = try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val sCurrentActivityThreadField: Field =
                activityThreadClass.getDeclaredField("sCurrentActivityThread")
            sCurrentActivityThreadField.isAccessible = true
            sCurrentActivityThreadField.get(null)
        } catch (e: Exception) {
            logD("getActivityThreadInActivityThreadStaticField: " + e.message)
            null
        }
    private val activityThreadInActivityThreadStaticMethod: Any?
        get() {
            return try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                activityThreadClass.getMethod("currentActivityThread").invoke(null)
            } catch (e: Exception) {
                logD("getActivityThreadInActivityThreadStaticMethod: " + e.message)
                null
            }
        }

    companion object {

        private var sInstance: LifecycleUtils? = null

        @Synchronized
        fun getInstance(): LifecycleUtils {
            if (sInstance == null) {
                sInstance = LifecycleUtils()
            }
            return sInstance!!
        }

        private val STUB = Activity()

        /**
         * Set animators enabled.
         */
        private fun setAnimatorsEnabled() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
                return
            }
            try {
                val sDurationScaleField: Field =
                    ValueAnimator::class.java.getDeclaredField("sDurationScale")
                sDurationScaleField.isAccessible = true
                val sDurationScale = sDurationScaleField.get(null) as Float
                if (sDurationScale == 0f) {
                    sDurationScaleField.set(null, 1f)
                    logD("setAnimatorsEnabled: Animators are enabled now!")
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }
}