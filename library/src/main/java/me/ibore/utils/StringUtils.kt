package me.ibore.utils

import android.content.res.Resources.NotFoundException
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/16
 * desc  : utils about string
</pre> *
 */
object StringUtils {
    /**
     * Return whether the string is null or 0-length.
     *
     * @param s The string.
     * @return `true`: yes<br></br> `false`: no
     */
    @JvmStatic
    fun isEmpty(s: CharSequence?): Boolean {
        return s == null || s.isEmpty()
    }

    /**
     * Return whether the string is null or whitespace.
     *
     * @param s The string.
     * @return `true`: yes<br></br> `false`: no
     */
    @JvmStatic
    fun isTrimEmpty(s: String?): Boolean {
        return s == null || s.trim { it <= ' ' }.isEmpty()
    }

    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return `true`: yes<br></br> `false`: no
     */
    @JvmStatic
    fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * Return whether string1 is equals to string2.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
        if (s1 === s2) return true
        var length = 0
        return if (s1 != null && s2 != null && s1.length.also { length = it } == s2.length) {
            if (s1 is String && s2 is String) s1 == s2
            else {
                for (i in 0 until length) {
                    if (s1[i] != s2[i]) return false
                }
                true
            }
        } else false
    }

    /**
     * Return whether string1 is equals to string2, ignoring case considerations..
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun equalsIgnoreCase(s1: String?, s2: String?): Boolean {
        return s1?.equals(s2, ignoreCase = true) ?: (s2 == null)
    }

    /**
     * Return `""` if string equals null.
     *
     * @param s The string.
     * @return `""` if string equals null
     */
    @JvmStatic
    fun null2Length0(s: String?): String {
        return s ?: ""
    }

    /**
     * Return the length of string.
     *
     * @param s The string.
     * @return the length of string
     */
    @JvmStatic
    fun length(s: CharSequence?): Int {
        return s?.length ?: 0
    }

    /**
     * Set the first letter of string upper.
     *
     * @param s The string.
     * @return the string with first letter upper.
     */
    @JvmStatic
    fun upperFirstLetter(s: String?): String {
        if (s == null || s.isEmpty()) return ""
        if (!Character.isLowerCase(s[0])) return s
        return (s[0].toInt() - 32).toString() + s.substring(1)
    }

    /**
     * Set the first letter of string lower.
     *
     * @param s The string.
     * @return the string with first letter lower.
     */
    @JvmStatic
    fun lowerFirstLetter(s: String?): String {
        if (s == null || s.isEmpty()) return ""
        if (!Character.isUpperCase(s[0])) return s
        return (s[0].toInt() + 32).toString() + s.substring(1)
    }

    /**
     * Reverse the string.
     *
     * @param s The string.
     * @return the reverse string.
     */
    @JvmStatic
    fun reverse(s: String?): String {
        if (s == null) return ""
        val len = s.length
        if (len <= 1) return s
        val mid = len shr 1
        val chars = s.toCharArray()
        var c: Char
        for (i in 0 until mid) {
            c = chars[i]
            chars[i] = chars[len - i - 1]
            chars[len - i - 1] = c
        }
        return String(chars)
    }

    /**
     * Convert string to DBC.
     *
     * @param s The string.
     * @return the DBC string
     */
    @JvmStatic
    fun toDBC(s: String?): String {
        if (s == null || s.isEmpty()) return ""
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            if (chars[i] == 12288.toChar()) {
                chars[i] = ' '
            } else if (65281.toChar() <= chars[i] && chars[i] <= 65374.toChar()) {
                chars[i] = (chars[i] - 65248)
            } else {
                chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }

    /**
     * Convert string to SBC.
     *
     * @param s The string.
     * @return the SBC string
     */
    @JvmStatic
    fun toSBC(s: String?): String {
        if (s == null || s.isEmpty()) return ""
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            if (chars[i] == ' ') {
                chars[i] = 12288.toChar()
            } else if (33.toChar() <= chars[i] && chars[i] <= 126.toChar()) {
                chars[i] = (chars[i] + 65248)
            } else {
                chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }

    /**
     * Return the string value associated with a particular resource ID.
     *
     * @param id The desired resource identifier.
     * @return the string value associated with a particular resource ID.
     */
    @JvmStatic
    fun getString(@StringRes id: Int): String? {
        return Utils.app.getString(id)
    }

    /**
     * Return the string value associated with a particular resource ID.
     *
     * @param id         The desired resource identifier.
     * @param formatArgs The format arguments that will be used for substitution.
     * @return the string value associated with a particular resource ID.
     */
    @JvmStatic
    fun getString(@StringRes id: Int, vararg formatArgs: Any?): String? {
        return try {
            format(Utils.app.getString(id), *formatArgs)
        } catch (e: NotFoundException) {
            e.printStackTrace()
            id.toString()
        }
    }

    /**
     * Return the string array associated with a particular resource ID.
     *
     * @param id The desired resource identifier.
     * @return The string array associated with the resource.
     */
    @JvmStatic
    fun getStringArray(@ArrayRes id: Int): Array<String> {
        return try {
            Utils.app.resources.getStringArray(id)
        } catch (e: NotFoundException) {
            e.printStackTrace()
            arrayOf(id.toString())
        }
    }

    /**
     * Format the string.
     *
     * @param str  The string.
     * @param args The args.
     * @return a formatted string.
     */
    @JvmStatic
    fun format(str: String?, vararg args: Any?): String? {
        var text = str ?: return null
        if (args.isNotEmpty()) {
            try {
                text = String.format(str, *args)
            } catch (e: IllegalFormatException) {
                e.printStackTrace()
            }
        }
        return text
    }
}