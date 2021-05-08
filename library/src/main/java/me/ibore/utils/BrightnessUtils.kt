package me.ibore.utils

import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.Window
import androidx.annotation.IntRange
import me.ibore.ktx.logD

/**
 * utils about brightness
 */
object BrightnessUtils {
    /**
     * Return whether automatic brightness mode is enabled.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isAutoEnabled(): Boolean {
        try {
            val mode = Settings.System.getInt(
                Utils.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
            return mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (e: SettingNotFoundException) {
            logD(e)
        }
        return false
    }

    /**
     * Enable or disable automatic brightness mode.
     *
     * Must hold `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
     *
     * @param enabled True to enabled, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun setAutoEnabled(enabled: Boolean): Boolean {
        return Settings.System.putInt(
            Utils.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
            if (enabled) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
    }

    /**
     * 获取屏幕亮度
     *
     * @return 屏幕亮度 0-255
     */
    @JvmStatic
    fun getBrightness(): Int {
        try {
            return Settings.System.getInt(Utils.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: SettingNotFoundException) {
            logD(e)
        }
        return 0
    }

    /**
     * 设置屏幕亮度
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
     * 并得到授权
     *
     * @param brightness 亮度值
     */
    @JvmStatic
    fun setBrightness(@IntRange(from = 0, to = 255) brightness: Int): Boolean {
        val resolver = Utils.contentResolver
        val b = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null)
        return b
    }

    /**
     * 设置窗口亮度
     *
     * @param window     窗口
     * @param brightness 亮度值
     */
    @JvmStatic
    fun setWindowBrightness(window: Window, @IntRange(from = 0, to = 255) brightness: Int) {
        val lp = window.attributes
        lp.screenBrightness = brightness / 255f
        window.attributes = lp
    }

    /**
     * 获取窗口亮度
     *
     * @param window 窗口
     * @return 屏幕亮度 0-255
     */
    @JvmStatic
    fun getWindowBrightness(window: Window): Int {
        val lp = window.attributes
        val brightness = lp.screenBrightness
        return if (brightness < 0) getBrightness() else (brightness * 255).toInt()
    }
}
