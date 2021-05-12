package me.ibore.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

object ActivityLifecycleUtils {


    fun init(app: Application) {

    }

    class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            LanguageUtils.applyLanguage(activity)
        }

        override fun onActivityStarted(activity: Activity) {

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

    interface Callbacks {

        fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        fun onActivityStarted(activity: Activity) {}

        fun onActivityResumed(activity: Activity) {}

        fun onActivityPaused(activity: Activity) {}

        fun onActivityStopped(activity: Activity) {}

        fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        fun onActivityDestroyed(activity: Activity) {}
    }
}