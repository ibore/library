package me.ibore.utils

import android.os.Build
import android.text.Html
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.jvm.Throws

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/07
 * desc  : utils about encode
</pre> *
 */
object EncodeUtils  {
    /**
     * Return the urlencoded string.
     *
     * @param input       The input.
     * @param charsetName The name of charset.
     * @return the urlencoded string
     */
    @JvmStatic
    @JvmOverloads
    fun urlEncode(input: String, charsetName: String = "UTF-8"): String {
        return if (input.isEmpty()) "" else try {
            URLEncoder.encode(input, charsetName)
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }
    }
    /**
     * Return the string of decode urlencoded string.
     *
     * @param input       The input.
     * @param charsetName The name of charset.
     * @return the string of decode urlencoded string
     */
    @JvmStatic
    @JvmOverloads
    @Throws
    fun urlDecode(input: String, charsetName: String = "UTF-8"): String {
        return if (input.isEmpty()) "" else try {
            val safeInput = input.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25")
                .replace("\\+".toRegex(), "%2B")
            URLDecoder.decode(safeInput, charsetName)
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }
    }

    /**
     * Return Base64-encode bytes.
     *
     * @param input The input.
     * @return Base64-encode bytes
     */
    @JvmStatic
    fun base64Encode(input: String?): ByteArray {
        return base64Encode(input?.toByteArray())
    }

    /**
     * Return Base64-encode bytes.
     *
     * @param input The input.
     * @return Base64-encode bytes
     */
    @JvmStatic
    fun base64Encode(input: ByteArray?): ByteArray {
        return if (input == null || input.isEmpty()) ByteArray(0)
        else Base64.encode(input, Base64.NO_WRAP)
    }

    /**
     * Return Base64-encode string.
     *
     * @param input The input.
     * @return Base64-encode string
     */
    @JvmStatic
    fun base64Encode2String(input: ByteArray?): String {
        return if (input == null || input.isEmpty()) ""
        else Base64.encodeToString(input, Base64.NO_WRAP)
    }

    /**
     * Return the bytes of decode Base64-encode string.
     *
     * @param input The input.
     * @return the string of decode Base64-encode string
     */
    @JvmStatic
    fun base64Decode(input: String?): ByteArray {
        return if (input == null || input.isEmpty()) ByteArray(0)
        else Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * Return the bytes of decode Base64-encode bytes.
     *
     * @param input The input.
     * @return the bytes of decode Base64-encode bytes
     */
    @JvmStatic
    fun base64Decode(input: ByteArray?): ByteArray {
        return if (input == null || input.isEmpty()) ByteArray(0)
        else Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * Return html-encode string.
     *
     * @param input The input.
     * @return html-encode string
     */
    @JvmStatic
    fun htmlEncode(input: CharSequence): String {
        if (input.isEmpty()) return ""
        val sb = StringBuilder()
        var c: Char
        var i = 0
        val len = input.length
        while (i < len) {
            c = input[i]
            when (c) {
                '<' -> sb.append("&lt;") //$NON-NLS-1$
                '>' -> sb.append("&gt;") //$NON-NLS-1$
                '&' -> sb.append("&amp;") //$NON-NLS-1$
                '\'' ->                     //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was
                    // introduced in XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead of &apos; to work as expected in HTML 4
                    // user agents.
                    sb.append("&#39;") //$NON-NLS-1$
                '"' -> sb.append("&quot;") //$NON-NLS-1$
                else -> sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    /**
     * Return the string of decode html-encode string.
     *
     * @param input The input.
     * @return the string of decode html-encode string
     */
    @JvmStatic
    fun htmlDecode(input: String): CharSequence {
        if (input.isEmpty()) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(input)
        }
    }

    /**
     * Return the binary encoded string padded with one space
     *
     * @param input The input.
     * @return binary string
     */
    @JvmStatic
    fun binaryEncode(input: String?): String {
        if (input == null || input.isEmpty()) return ""
        val sb = StringBuilder()
        for (i in input.toCharArray()) {
            sb.append(Integer.toBinaryString(i.toInt())).append(" ")
        }
        return sb.deleteCharAt(sb.length - 1).toString()
    }

    /**
     * Return UTF-8 String from binary
     *
     * @param input binary string
     * @return UTF-8 String
     */
    @JvmStatic
    fun binaryDecode(input: String?): String {
        if (input == null || input.isEmpty()) return ""
        val splits = input.split(" ").toTypedArray()
        val sb = StringBuilder()
        for (split in splits) {
            sb.append(split.toInt(2).toChar())
        }
        return sb.toString()
    }
}