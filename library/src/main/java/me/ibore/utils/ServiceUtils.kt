package me.ibore.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : utils about service
</pre> *
 */
object ServiceUtils {
    /**
     * Return all of the services are running.
     *
     * @return all of the services are running
     */
    val allRunningServices: Set<String>?
        get() {
            val am =
                Utils.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.getRunningServices(0x7FFFFFFF)
            val names: MutableSet<String> = HashSet()
            if (info == null || info.size == 0) return null
            for (aInfo in info) {
                names.add(aInfo.service.className)
            }
            return names
        }

    /**
     * Start the service.
     *
     * @param className The name of class.
     */
    fun startService(className: String) {
        try {
            startService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Start the service.
     *
     * @param cls The service class.
     */
    fun startService(cls: Class<*>) {
        startService(Intent(Utils.app, cls))
    }

    /**
     * Start the service.
     *
     * @param intent The intent.
     */
    fun startService(intent: Intent) {
        try {
            intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.app.startForegroundService(intent)
            } else {
                Utils.app.startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stop the service.
     *
     * @param className The name of class.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(className: String): Boolean {
        return try {
            stopService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Stop the service.
     *
     * @param cls The name of class.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(cls: Class<*>): Boolean {
        return stopService(Intent(Utils.app, cls))
    }

    /**
     * Stop the service.
     *
     * @param intent The intent.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(intent: Intent): Boolean {
        return try {
            Utils.app.stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Bind the service.
     *
     * @param className The name of class.
     * @param conn      The ServiceConnection object.
     * @param flags     Operation options for the binding.
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        className: String,
        conn: ServiceConnection,
        flags: Int
    ) {
        try {
            bindService(Class.forName(className), conn, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Bind the service.
     *
     * @param cls   The service class.
     * @param conn  The ServiceConnection object.
     * @param flags Operation options for the binding.
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        cls: Class<*>,
        conn: ServiceConnection,
        flags: Int
    ) {
        bindService(Intent(Utils.app, cls), conn, flags)
    }

    /**
     * Bind the service.
     *
     * @param intent The intent.
     * @param conn   The ServiceConnection object.
     * @param flags  Operation options for the binding.
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        intent: Intent,
        conn: ServiceConnection,
        flags: Int
    ) {
        try {
            Utils.app.bindService(intent, conn, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Unbind the service.
     *
     * @param conn The ServiceConnection object.
     */
    fun unbindService(conn: ServiceConnection) {
        Utils.app.unbindService(conn)
    }

    /**
     * Return whether service is running.
     *
     * @param cls The service class.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isServiceRunning(cls: Class<*>): Boolean {
        return isServiceRunning(cls.name)
    }

    /**
     * Return whether service is running.
     *
     * @param className The name of class.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isServiceRunning(className: String): Boolean {
        return try {
            val am =
                Utils.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.getRunningServices(0x7FFFFFFF)
            if (info == null || info.size == 0) return false
            for (aInfo in info) {
                if (className == aInfo.service.className) return true
            }
            false
        } catch (ignore: Exception) {
            false
        }
    }
}