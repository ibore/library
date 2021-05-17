package me.ibore.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

object LifecycleUtils {

    private val mActivityList = CopyOnWriteArrayList<WeakReference<Activity>>()
    private val mStatusListeners: MutableList<Utils.OnAppStatusChangedListener> =
        CopyOnWriteArrayList()
    private val mActivityLifecycleCallbacks = ActivityLifecycleCallbacksImpl()
    private var mForegroundCount = 0
    private var mConfigCount = 0
    private var mIsBackground = false

    @JvmStatic
    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    @JvmStatic
    fun unInit(app: Application) {
        mActivityList.clear()
        app.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    val activityList: MutableList<Activity>
        get() {
            val list = LinkedList<Activity>()
            if (!mActivityList.isEmpty()) {
                for (weakActivity in mActivityList) {
                    val activity = weakActivity.get() ?: continue
                    list.add(activity)
                }
            }
            //val reflectActivities = activitiesByReflect
            //mActivityList.addAll(reflectActivities)
            //return LinkedList(mActivityList)
            return list
        }

    fun addOnAppStatusChangedListener(listener: Utils.OnAppStatusChangedListener) {
        mStatusListeners.add(listener)
    }

    fun removeOnAppStatusChangedListener(listener: Utils.OnAppStatusChangedListener) {
        mStatusListeners.remove(listener)
    }


    class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            LanguageUtils.applyLanguage(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            if (!mIsBackground) {
                setTopActivity(activity)
            }
            if (mConfigCount < 0) {
                ++mConfigCount
            } else {
                ++mForegroundCount
            }
        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }

    }

}