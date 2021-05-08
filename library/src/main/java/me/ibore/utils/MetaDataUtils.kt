package me.ibore.utils

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.pm.PackageManager

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/05/15
 * desc  : utils about meta-data
</pre> *
 */
object MetaDataUtils {
    /**
     * Return the value of meta-data in application.
     *
     * @param key The key of meta-data.
     * @return the value of meta-data in application
     */
    fun getMetaDataInApp(key: String): String {
        var value = ""
        val pm = Utils.packageManager
        val packageName = Utils.packageName
        try {
            val ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            value = ai.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * Return the value of meta-data in activity.
     *
     * @param activity The activity.
     * @param key      The key of meta-data.
     * @return the value of meta-data in activity
     */
    fun getMetaDataInActivity(activity: Activity, key: String): String {
        return getMetaDataInActivity(activity.javaClass, key)
    }

    /**
     * Return the value of meta-data in activity.
     *
     * @param clz The activity class.
     * @param key The key of meta-data.
     * @return the value of meta-data in activity
     */
    fun getMetaDataInActivity(clz: Class<out Activity?>, key: String): String {
        var value = ""
        val pm = Utils.packageManager
        val componentName = ComponentName(Utils.app, clz)
        try {
            val ai = pm.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            value = ai.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * Return the value of meta-data in service.
     *
     * @param service The service.
     * @param key     The key of meta-data.
     * @return the value of meta-data in service
     */
    fun getMetaDataInService(service: Service,key: String): String {
        return getMetaDataInService(service.javaClass, key)
    }

    /**
     * Return the value of meta-data in service.
     *
     * @param clz The service class.
     * @param key The key of meta-data.
     * @return the value of meta-data in service
     */
    fun getMetaDataInService(clz: Class<out Service?>, key: String): String {
        var value = ""
        val pm = Utils.packageManager
        val componentName = ComponentName(Utils.app, clz)
        try {
            val info = pm.getServiceInfo(componentName, PackageManager.GET_META_DATA)
            value = info.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * Return the value of meta-data in receiver.
     *
     * @param receiver The receiver.
     * @param key      The key of meta-data.
     * @return the value of meta-data in receiver
     */
    fun getMetaDataInReceiver(receiver: BroadcastReceiver, key: String): String {
        return getMetaDataInReceiver(receiver.javaClass, key)
    }

    /**
     * Return the value of meta-data in receiver.
     *
     * @param clz The receiver class.
     * @param key The key of meta-data.
     * @return the value of meta-data in receiver
     */
    fun getMetaDataInReceiver(clz: Class<out BroadcastReceiver?>, key: String): String {
        var value = ""
        val pm = Utils.packageManager
        val componentName = ComponentName(Utils.app, clz)
        try {
            val info = pm.getReceiverInfo(componentName, PackageManager.GET_META_DATA)
            value = info.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }
}