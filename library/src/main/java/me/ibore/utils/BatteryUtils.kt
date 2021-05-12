package me.ibore.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.annotation.IntDef
import java.util.*

/**
 * BatteryUtils
 */
object BatteryUtils {

    const val UNKNOWN: Int = BatteryManager.BATTERY_STATUS_UNKNOWN
    const val DISCHARGING: Int = BatteryManager.BATTERY_STATUS_DISCHARGING
    const val CHARGING: Int = BatteryManager.BATTERY_STATUS_CHARGING
    const val NOT_CHARGING: Int = BatteryManager.BATTERY_STATUS_NOT_CHARGING
    const val FULL: Int = BatteryManager.BATTERY_STATUS_FULL

    /**
     * Register the status of battery changed listener.
     *
     * @param listener The status of battery changed listener.
     */
    @JvmStatic
    fun registerBatteryStatusChangedListener(listener: OnBatteryStatusChangedListener) {
        BatteryChangedReceiver.getInstance().registerListener(listener)
    }

    /**
     * Return whether the status of battery changed listener has been registered.
     *
     * @param listener The status of battery changed listener.
     * @return true to registered, false otherwise.
     */
    @JvmStatic
    fun isRegistered(listener: OnBatteryStatusChangedListener): Boolean {
        return BatteryChangedReceiver.getInstance().isRegistered(listener)
    }

    /**
     * Unregister the status of battery changed listener.
     *
     * @param listener The status of battery changed listener.
     */
    @JvmStatic
    fun unregisterBatteryStatusChangedListener(listener: OnBatteryStatusChangedListener) {
        BatteryChangedReceiver.getInstance().unregisterListener(listener)
    }

    @IntDef(value = [UNKNOWN, DISCHARGING, CHARGING, NOT_CHARGING, FULL])
    @Retention(AnnotationRetention.SOURCE)
    annotation class BatteryStatus

    class BatteryChangedReceiver : BroadcastReceiver() {

        private val mListeners: MutableSet<OnBatteryStatusChangedListener> = HashSet()

        fun registerListener(listener: OnBatteryStatusChangedListener) {
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

        fun isRegistered(listener: OnBatteryStatusChangedListener): Boolean {
            return mListeners.contains(listener)
        }

        fun unregisterListener(listener: OnBatteryStatusChangedListener) {
            ThreadUtils.runOnUiThread {
                val preSize = mListeners.size
                mListeners.remove(listener)
                if (preSize == 1 && mListeners.size == 0) {
                    Utils.app.unregisterReceiver(getInstance())
                }
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_BATTERY_CHANGED == intent.action) {
                ThreadUtils.runOnUiThread {
                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val status: Int =
                        intent.getIntExtra(BatteryManager.EXTRA_STATUS, UNKNOWN)
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
                return LazyHolder.INSTANCE
            }
        }
    }

    interface OnBatteryStatusChangedListener {
        fun onBatteryStatusChanged(status: Status)
    }

    class Status internal constructor(var level: Int, @BatteryStatus var status: Int) {

        override fun toString(): String {
            return batteryStatus2String(status) + ": " + level + "%"
        }

        companion object {
            fun batteryStatus2String(@BatteryStatus status: Int): String {
                if (status == DISCHARGING) {
                    return "discharging"
                }
                if (status == CHARGING) {
                    return "charging"
                }
                if (status == NOT_CHARGING) {
                    return "not_charging"
                }
                return if (status == FULL) {
                    "full"
                } else "unknown"
            }
        }
    }
}