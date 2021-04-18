package me.ibore.utils

import android.Manifest.permission
import android.content.Context
import android.os.Vibrator
import androidx.annotation.RequiresPermission

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/29
 * desc  : utils about vibrate
</pre> *
 */
object VibrateUtils {

    private var vibrator: Vibrator? = null
        get() {
            if (field == null) {
                field = Utils.app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
            }
            return field
        }

    /**
     * Vibrate.
     *
     * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
     *
     * @param milliseconds The number of milliseconds to vibrate.
     */
    @RequiresPermission(permission.VIBRATE)
    fun vibrate(milliseconds: Long) {
        val vibrator = vibrator ?: return
        vibrator.vibrate(milliseconds)
    }

    /**
     * Vibrate.
     *
     * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
     *
     * @param pattern An array of longs of times for which to turn the vibrator on or off.
     * @param repeat  The index into pattern at which to repeat, or -1 if you don't want to repeat.
     */
    @RequiresPermission(permission.VIBRATE)
    fun vibrate(pattern: LongArray?, repeat: Int) {
        val vibrator = vibrator ?: return
        vibrator.vibrate(pattern, repeat)
    }

    /**
     * Cancel vibrate.
     *
     * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
     */
    @RequiresPermission(permission.VIBRATE)
    fun cancel() {
        val vibrator = vibrator ?: return
        vibrator.cancel()
    }
}