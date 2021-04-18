package me.ibore.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.annotation.IntDef
import java.util.*

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2020/03/31
 * desc  :
</pre> *
 */
object BatteryUtils {

    /**
     * Register the status of battery changed listener.
     *
     * @param listener The status of battery changed listener.
     */
    fun registerBatteryStatusChangedListener(listener: OnBatteryStatusChangedListener?) {
        BatteryChangedReceiver.getInstance().registerListener(listener)
    }

    /**
     * Return whether the status of battery changed listener has been registered.
     *
     * @param listener The status of battery changed listener.
     * @return true to registered, false otherwise.
     */
    fun isRegistered(listener: OnBatteryStatusChangedListener?): Boolean {
        return BatteryChangedReceiver.getInstance().isRegistered(listener)
    }

    /**
     * Unregister the status of battery changed listener.
     *
     * @param listener The status of battery changed listener.
     */
    fun unregisterBatteryStatusChangedListener(listener: OnBatteryStatusChangedListener?) {
        BatteryChangedReceiver.getInstance().unregisterListener(listener)
    }

    @IntDef(value = [BatteryStatus.UNKNOWN, BatteryStatus.DISCHARGING, BatteryStatus.CHARGING, BatteryStatus.NOT_CHARGING, BatteryStatus.FULL])
    @Retention(AnnotationRetention.SOURCE)
    annotation class BatteryStatus {
        companion object {
            const val UNKNOWN: Int = BatteryManager.BATTERY_STATUS_UNKNOWN
            const val DISCHARGING: Int = BatteryManager.BATTERY_STATUS_DISCHARGING
            const val CHARGING: Int = BatteryManager.BATTERY_STATUS_CHARGING
            const val NOT_CHARGING: Int = BatteryManager.BATTERY_STATUS_NOT_CHARGING
            const val FULL: Int = BatteryManager.BATTERY_STATUS_FULL
        }
    }

    class BatteryChangedReceiver : BroadcastReceiver() {
        private val mListeners: MutableSet<OnBatteryStatusChangedListener> = HashSet()
        fun registerListener(listener: OnBatteryStatusChangedListener?) {
            if (listener == null) return
            ThreadUtils.runOnUiThread {
                val preSize = mListeners.size
                mListeners.add(listener)
                if (preSize == 0 && mListeners.size == 1) {
                    val intentFilter = IntentFilter()
                    intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                    Utils.app.registerReceiver(getInstance(), intentFilter)
                }
            }
        }

        fun isRegistered(listener: OnBatteryStatusChangedListener?): Boolean {
            return if (listener == null) false else mListeners.contains(listener)
        }

        fun unregisterListener(listener: OnBatteryStatusChangedListener?) {
            if (listener == null) return
            ThreadUtils.runOnUiThread {
                val preSize = mListeners.size
                mListeners.remove(listener)
                if (preSize == 1 && mListeners.size == 0) {
                    Utils.app.unregisterReceiver(getInstance())
                }
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_BATTERY_CHANGED == intent.getAction()) {
                ThreadUtils.runOnUiThread {
                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val status: Int =
                        intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryStatus.UNKNOWN)
                    for (listener in mListeners) {
                        listener.onBatteryStatusChanged(Status(level, status))
                    }
                }
            }
        }

        internal object LazyHolder {
            internal val INSTANCE = BatteryChangedReceiver()
        }

        companion object {
            internal fun getInstance(): BatteryChangedReceiver {
                return BatteryChangedReceiver.LazyHolder.INSTANCE
            }
        }
    }

    interface OnBatteryStatusChangedListener {
        fun onBatteryStatusChanged(status: Status?)
    }

    class Status internal constructor(
        var level: Int,
        @BatteryStatus
        var status: Int
    ) {

        override fun toString(): String {
            return batteryStatus2String(status) + ": " + level + "%"
        }

        companion object {
            fun batteryStatus2String(@BatteryStatus status: Int): String {
                if (status == BatteryStatus.DISCHARGING) {
                    return "discharging"
                }
                if (status == BatteryStatus.CHARGING) {
                    return "charging"
                }
                if (status == BatteryStatus.NOT_CHARGING) {
                    return "not_charging"
                }
                return if (status == BatteryStatus.FULL) {
                    "full"
                } else "unknown"
            }
        }
    }
}