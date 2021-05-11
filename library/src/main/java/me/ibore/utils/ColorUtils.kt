package me.ibore.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/01/15
 * desc  : utils about color
</pre> *
 */
object ColorUtils {
    /**
     * Returns a color associated with a particular resource ID.
     *
     * @param id The desired resource identifier.
     * @return a color associated with a particular resource ID
     */
    @JvmStatic
    fun color(@ColorRes id: Int, context: Context = Utils.app): Int {
        return ContextCompat.getColor(context, id)
    }

    /**
     * Set the alpha  of `color` to be `alpha`.
     *
     * @param color The color.
     * @param alpha Alpha  \([0..255]\) of the color.
     * @return the `color` with `alpha`
     */
    @JvmStatic
    fun alpha(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) alpha: Int): Int {
        return color and 0x00ffffff or (alpha shl 24)
    }

    /**
     * Set the alpha  of `color` to be `alpha`.
     *
     * @param color The color.
     * @param alpha Alpha  \([0..1]\) of the color.
     * @return the `color` with `alpha`
     */
    @JvmStatic
    fun alpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
        return color and 0x00ffffff or ((alpha * 255.0f + 0.5f).toInt() shl 24)
    }

    /**
     * Set the red  of `color` to be `red`.
     *
     * @param color The color.
     * @param red   Red  \([0..255]\) of the color.
     * @return the `color` with `red`
     */
    @JvmStatic
    fun red(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) red: Int): Int {
        return color and -0xff0001 or (red shl 16)
    }

    /**
     * Set the red  of `color` to be `red`.
     *
     * @param color The color.
     * @param red   Red  \([0..1]\) of the color.
     * @return the `color` with `red`
     */
    @JvmStatic
    fun red(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) red: Float): Int {
        return color and -0xff0001 or ((red * 255.0f + 0.5f).toInt() shl 16)
    }

    /**
     * Set the green  of `color` to be `green`.
     *
     * @param color The color.
     * @param green Green  \([0..255]\) of the color.
     * @return the `color` with `green`
     */
    @JvmStatic
    fun green(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) green: Int): Int {
        return color and -0xff01 or (green shl 8)
    }

    /**
     * Set the green  of `color` to be `green`.
     *
     * @param color The color.
     * @param green Green  \([0..1]\) of the color.
     * @return the `color` with `green`
     */
    @JvmStatic
    fun green(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) green: Float): Int {
        return color and -0xff01 or ((green * 255.0f + 0.5f).toInt() shl 8)
    }

    /**
     * Set the blue  of `color` to be `blue`.
     *
     * @param color The color.
     * @param blue  Blue  \([0..255]\) of the color.
     * @return the `color` with `blue`
     */
    @JvmStatic
    fun blue(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) blue: Int): Int {
        return color and -0x100 or blue
    }

    /**
     * Set the blue  of `color` to be `blue`.
     *
     * @param color The color.
     * @param blue  Blue  \([0..1]\) of the color.
     * @return the `color` with `blue`
     */
    @JvmStatic
    fun blue(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) blue: Float): Int {
        return color and -0x100 or (blue * 255.0f + 0.5f).toInt()
    }

    /**
     * Color-string to color-int.
     *
     * Supported formats are:
     *
     *
     *  * `#RRGGBB`
     *  * `#AARRGGBB`
     *
     *
     *
     * The following names are also accepted: `red`, `blue`,
     * `green`, `black`, `white`, `gray`,
     * `cyan`, `magenta`, `yellow`, `lightgray`,
     * `darkgray`, `grey`, `lightgrey`, `darkgrey`,
     * `aqua`, `fuchsia`, `lime`, `maroon`,
     * `navy`, `olive`, `purple`, `silver`,
     * and `teal`.
     *
     * @param colorString The color-string.
     * @return color-int
     * @throws IllegalArgumentException The string cannot be parsed.
     */
    @JvmStatic
    fun color(colorString: String): Int {
        return Color.parseColor(colorString)
    }

    /**
     * Color-int to color-string.
     *
     * @param colorInt The color-int.
     * @return color-string
     */
    @JvmStatic
    fun int2RgbString(@ColorInt colorInt: Int): String {
        var colorIntTemp = colorInt
        colorIntTemp = colorIntTemp and 0x00ffffff
        var color = Integer.toHexString(colorIntTemp)
        while (color.length < 6) {
            color = "0$color"
        }
        return "#$color"
    }

    /**
     * Color-int to color-string.
     *
     * @param colorInt The color-int.
     * @return color-string
     */
    @JvmStatic
    fun int2ArgbString(@ColorInt colorInt: Int): String {
        var color = Integer.toHexString(colorInt)
        while (color.length < 6) {
            color = "0$color"
        }
        while (color.length < 8) {
            color = "f$color"
        }
        return "#$color"
    }

    /**
     * Return the random color.
     *
     * @return the random color
     */
    @JvmStatic
    val randomColor: Int
        get() = randomColor(true)

    /**
     * Return the random color.
     *
     * @param supportAlpha True to support alpha, false otherwise.
     * @return the random color
     */
    @JvmStatic
    fun randomColor(supportAlpha: Boolean): Int {
        val high = if (supportAlpha) (Math.random() * 0x100).toInt() shl 24 else -0x1000000
        return high or (Math.random() * 0x1000000).toInt()
    }

    /**
     * Return whether the color is light.
     *
     * @param color The color.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isLightColor(@ColorInt color: Int): Boolean {
        return 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color) >= 127.5
    }
}