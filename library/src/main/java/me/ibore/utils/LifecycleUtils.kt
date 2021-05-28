package me.ibore.utils

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.Lifecycle
import me.ibore.utils.LanguageUtils.applyLanguage
import me.ibore.utils.Utils.OnAppStatusChangedListener
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


internal class LifecycleUtils : Application.ActivityLifecycleCallbacks {

    private val mActivityList: LinkedList<Activity> = LinkedList()
    private val mStatusListeners: MutableList<OnAppStatusChangedListener> = CopyOnWriteArrayList()
    private val mActivityLifecycleCallbacksMap: MutableMap<Activity, MutableList<Utils.ActivityLifecycleCallbacks>?> =
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

    fun addActivityLifecycleCallbacks(listener: Utils.ActivityLifecycleCallbacks?) {
        addActivityLifecycleCallbacks(STUB, listener)
    }

    fun addActivityLifecycleCallbacks(
        activity: Activity?,
        listener: Utils.ActivityLifecycleCallbacks?
    ) {
        if (activity == null || listener == null) return
        ThreadUtils.runOnUiThread {
            addActivityLifecycleCallbacksInner(activity, listener)
        }
    }

    val isAppForeground: Boolean
        get() = !mIsBackground

    private fun addActivityLifecycleCallbacksInner(
        activity: Activity,
        callbacks: Utils.ActivityLifecycleCallbacks
    ) {
        var callbacksList = mActivityLifecycleCallbacksMap[activity]
        if (callbacksList == null) {
            callbacksList = CopyOnWriteArrayList()
            mActivityLifecycleCallbacksMap[activity] = callbacksList
        } else {
            if (callbacksList.contains(callbacks)) return
        }
        callbacksList.add(callbacks)
    }

    fun removeActivityLifecycleCallbacks(callbacks: Utils.ActivityLifecycleCallbacks?) {
        removeActivityLifecycleCallbacks(STUB, callbacks)
    }

    fun removeActivityLifecycleCallbacks(activity: Activity?) {
        if (activity == null) return
        ThreadUtils.runOnUiThread { mActivityLifecycleCallbacksMap.remove(activity) }
    }

    fun removeActivityLifecycleCallbacks(
        activity: Activity?,
        callbacks: Utils.ActivityLifecycleCallbacks?
    ) {
        if (activity == null || callbacks == null) return
        ThreadUtils.runOnUiThread {
            removeActivityLifecycleCallbacksInner(activity, callbacks)
        }
    }

    private fun removeActivityLifecycleCallbacksInner(
        activity: Activity,
        callbacks: Utils.ActivityLifecycleCallbacks
    ) {
        val callbacksList = mActivityLifecycleCallbacksMap[activity]
        if (callbacksList != null && callbacksList.isNotEmpty()) {
            callbacksList.remove(callbacks)
        }
    }

    private fun consumeActivityLifecycleCallbacks(activity: Activity, event: Lifecycle.Event) {
        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap[activity])
        consumeLifecycle(
            activity, event,
            mActivityLifecycleCallbacksMap[STUB]
        )
    }

    private fun consumeLifecycle(
        activity: Activity,
        event: Lifecycle.Event,
        listeners: List<Utils.ActivityLifecycleCallbacks>?
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
            mActivityLifecycleCallbacksMap.remove(activity)
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

    ///////////////////////////////////////////////////////////////////////////
    // lifecycle start
    ///////////////////////////////////////////////////////////////////////////
    override fun onActivityPreCreated(
        @NonNull activity: Activity,
        @Nullable savedInstanceState: Bundle?
    ) { 
    }

    override fun onActivityCreated(@NonNull activity: Activity, savedInstanceState: Bundle?) {
        if (mActivityList.isEmpty()) {
            postStatus(activity, true)
        }
        applyLanguage(activity)
        setAnimatorsEnabled()
        setTopActivity(activity)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_CREATE)
    }

    override fun onActivityPostCreated(@NonNull activity: Activity, @Nullable savedInstanceState: Bundle?) {
    }

    override fun onActivityPreStarted(@NonNull activity: Activity) { 
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

    override fun onActivityPostStarted(@NonNull activity: Activity) { 
    }

    override fun onActivityPreResumed(@NonNull activity: Activity) { 
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

    override fun onActivityPostResumed(@NonNull activity: Activity) { 
    }

    override fun onActivityPrePaused(@NonNull activity: Activity) { 
    }

    override fun onActivityPaused(@NonNull activity: Activity) {
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_PAUSE)
    }

    override fun onActivityPostPaused(@NonNull activity: Activity) { 
    }

    override fun onActivityPreStopped(@NonNull activity: Activity) { 
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

    override fun onActivityPostStopped(@NonNull activity: Activity) { 
    }

    override fun onActivityPreSaveInstanceState(
        @NonNull activity: Activity,
        @NonNull outState: Bundle
    ) { 
    }

    override fun onActivitySaveInstanceState(
        @NonNull activity: Activity,
        @NonNull outState: Bundle
    ) { 
    }

    override fun onActivityPostSaveInstanceState(
        @NonNull activity: Activity,
        @NonNull outState: Bundle
    ) { 
    }

    override fun onActivityPreDestroyed(@NonNull activity: Activity) { 
    }

    override fun onActivityDestroyed(@NonNull activity: Activity) {
        mActivityList.remove(activity)
        KeyboardUtils.fixSoftInputLeaks(activity)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_DESTROY)
    }

    override fun onActivityPostDestroyed(@NonNull activity: Activity) { 
    }
    ///////////////////////////////////////////////////////////////////////////
    // lifecycle end
    ///////////////////////////////////////////////////////////////////////////
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
        private get() {
            val activityThread = activityThreadInActivityThreadStaticField
            return if (activityThread != null) activityThread else activityThreadInActivityThreadStaticMethod
        }
    private val activityThreadInActivityThreadStaticField: Any?
        private get() = try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val sCurrentActivityThreadField: Field =
                activityThreadClass.getDeclaredField("sCurrentActivityThread")
            sCurrentActivityThreadField.setAccessible(true)
            sCurrentActivityThreadField.get(null)
        } catch (e: Exception) {
            Log.e(
                "UtilsActivityLifecycle",
                "getActivityThreadInActivityThreadStaticField: " + e.message
            )
            null
        }
    private val activityThreadInActivityThreadStaticMethod: Any?
        private get() {
            return try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                activityThreadClass.getMethod("currentActivityThread").invoke(null)
            } catch (e: Exception) {
                Log.e(
                    "UtilsActivityLifecycle",
                    "getActivityThreadInActivityThreadStaticMethod: " + e.message
                )
                null
            }
        }

    companion object {

        val INSTANCE = LifecycleUtils()
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
                sDurationScaleField.setAccessible(true)
                val sDurationScale = sDurationScaleField.get(null) as Float
                if (sDurationScale == 0f) {
                    sDurationScaleField.set(null, 1f)
                    Log.i(
                        "UtilsActivityLifecycle",
                        "setAnimatorsEnabled: Animators are enabled now!"
                    )
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }
}