package me.ibore.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context

/**
 * 剪贴板
 */
object ClipboardUtils {

    /**
     *  将文本复制到剪贴板
     *
     * @param label 标签
     * @param text  文本
     */
    @JvmStatic
    @JvmOverloads
    fun copyText(
        text: CharSequence?,
        label: CharSequence = Utils.app.packageName,
        context: Context = Utils.app
    ) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    /**
     * 清除剪贴板
     */
    @JvmStatic
    @JvmOverloads
    fun clear(context: Context = Utils.app) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(null, ""))
    }

    /**
     * 返回剪贴板的标签
     */
    @JvmStatic
    @JvmOverloads
    fun getLabel(context: Context = Utils.app): CharSequence {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val des = cm.primaryClipDescription ?: return ""
        return des.label ?: return ""
    }

    /**
     * 返回剪贴板的文本
     */
    @JvmStatic
    @JvmOverloads
    fun getText(context: Context = Utils.app): CharSequence {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = cm.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).coerceToText(Utils.app)
            if (text != null) {
                return text
            }
        }
        return ""
    }

    /**
     * 添加剪贴板更改监听
     */
    @JvmStatic
    @JvmOverloads
    fun addChangedListener(
        listener: OnPrimaryClipChangedListener?,
        context: Context = Utils.app
    ) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.addPrimaryClipChangedListener(listener)
    }

    /**
     * 移除剪贴板更改监听
     */
    @JvmStatic
    @JvmOverloads
    fun removeChangedListener(
        listener: OnPrimaryClipChangedListener?,
        context: Context = Utils.app
    ) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.removePrimaryClipChangedListener(listener)
    }
}