package me.ibore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * detail: 软键盘相关工具类
 * @author Ttt
 * <pre>
 * 避免软键盘面板遮挡 manifest.xml 中 activity 中设置
 * android:windowSoftInputMode="adjustPan"
 * android:windowSoftInputMode="adjustUnspecified|stateHidden"
</pre> *
 */
object KeyBoardUtils {

    // 日志 TAG
    @JvmStatic
    private val TAG = KeyBoardUtils::class.java.simpleName

    // 主线程 Handler
    @JvmStatic
    private val sMainHandler: Handler = Handler(Looper.getMainLooper())

    // 默认延迟时间 ( 毫秒 )
    @JvmStatic
    private var DELAY_MILLIS: Long = 300

    // 键盘显示
    const val KEYBOARD_DISPLAY = 930

    // 键盘隐藏
    const val KEYBOARD_HIDE = 931

    /**
     * 设置延迟时间
     * @param delayMillis 延迟时间 ( 毫秒 )
     */
    @JvmStatic
    fun setDelayMillis(delayMillis: Long) {
        DELAY_MILLIS = delayMillis
    }

    /**
     * 设置 Window 软键盘是否显示
     * @param activity     [Activity]
     * @param inputVisible 是否显示软键盘
     * @param clearFlag    是否清空 Flag ( FLAG_ALT_FOCUSABLE_IM | FLAG_NOT_FOCUSABLE )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun setSoftInputMode(
        activity: Activity, inputVisible: Boolean, clearFlag: Boolean = true
    ): Boolean {
        return setSoftInputMode(activity.window, inputVisible, clearFlag)
    }

    /**
     * 设置 Window 软键盘是否显示
     * @param window       [Window]
     * @param inputVisible 是否显示软键盘
     * @param clearFlag    是否清空 Flag ( FLAG_ALT_FOCUSABLE_IM | FLAG_NOT_FOCUSABLE )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun setSoftInputMode(
        window: Window, inputVisible: Boolean, clearFlag: Boolean = true
    ): Boolean {
        try {
            if (inputVisible) {
                if (clearFlag) {
                    window.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    )
                }
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            } else {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            }
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "setSoftInputMode")
        }
        return true
    }
    // ==============================
    // = 点击非 EditText 则隐藏软键盘 =
    // ==============================
    /**
     * 设置某个 View 内所有非 EditText 的子 View OnTouchListener 事件
     * @param view     [View]
     * @param activity [Activity]
     */
    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    fun judgeView(view: View, activity: Activity) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                closeKeyboard(activity)
                false
            }
        }
        // =
        if (view is ViewGroup) {
            val viewGroup: ViewGroup? = view as ViewGroup?
            if (viewGroup != null) {
                var i = 0
                val len: Int = viewGroup.childCount
                while (i < len) {
                    val innerView: View = viewGroup.getChildAt(i)
                    judgeView(innerView, activity)
                    i++
                }
            }
        }
    }
    // =================
    // = 软键盘隐藏显示 =
    // =================
    /**
     * 判断软键盘是否可见
     * @param activity             [Activity]
     * @param minHeightOfSoftInput 软键盘最小高度
     * @return `true` 可见, `false` 不可见
     */
    @JvmStatic
    @JvmOverloads
    fun isSoftInputVisible(activity: Activity, minHeightOfSoftInput: Int = 200): Boolean {
        return getContentViewInvisibleHeight(activity) >= minHeightOfSoftInput
    }

    /**
     * 计算 Activity content View 高度
     * @param activity [Activity]
     * @return View 的高度
     */
    @JvmStatic
    private fun getContentViewInvisibleHeight(activity: Activity): Int {
        return try {
            val contentView: View = activity.findViewById(android.R.id.content)
            val rect = Rect()
            contentView.getWindowVisibleDisplayFrame(rect)
            contentView.rootView.height - rect.height()
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "getContentViewInvisibleHeight")
            0
        }
    }

    /**
     * 注册软键盘改变监听
     * @param activity [Activity]
     * @param listener [OnSoftInputChangedListener]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun registerSoftInputChangedListener(
        activity: Activity, listener: OnSoftInputChangedListener
    ): Boolean {
        try {
            // 获取根 View
            val contentView: View = activity.findViewById(android.R.id.content)
            // 添加事件
            contentView.viewTreeObserver.addOnGlobalLayoutListener {
                // 获取高度
                val height = getContentViewInvisibleHeight(activity)
                // 判断是否相同
                listener.onSoftInputChanged(height >= 200, height)
            }
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "registerSoftInputChangedListener")
        }
        return false
    }

    /**
     * 注册软键盘改变监听
     * @param activity [Activity]
     * @param listener [OnSoftInputChangedListener]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun registerSoftInputChangedListener2(
        activity: Activity,
        listener: OnSoftInputChangedListener
    ): Boolean {
        try {
            val decorView: View = activity.window.decorView
            decorView.viewTreeObserver.addOnGlobalLayoutListener {
                try {
                    val rect = Rect()
                    decorView.getWindowVisibleDisplayFrame(rect)
                    // 计算出可见屏幕的高度
                    val displayHeight = rect.bottom - rect.top
                    // 获取屏幕整体的高度
                    val height = decorView.height
                    // 获取键盘高度
                    val keyboardHeight = height - displayHeight
                    // 计算一定比例
                    val visible = displayHeight.toDouble() / height.toDouble() < 0.8
                    // 判断是否显示
                    listener.onSoftInputChanged(visible, keyboardHeight)
                } catch (e: Exception) {
                    LogUtils.eTag(TAG, e, "registerSoftInputChangedListener2")
                }
            }
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "registerSoftInputChangedListener2")
        }
        return false
    }
    // =
    /**
     * 修复软键盘内存泄漏 在 Activity.onDestroy() 中使用
     * @param context [Context]
     */
    @JvmStatic
    fun fixSoftInputLeaks(context: Context) {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            val strArr = arrayOf("mCurRootView", "mServedView", "mNextServedView", "mLastSrvView")
            var i = 0
            val len = strArr.size
            while (i < len) {
                try {
                    val declaredField = imm.javaClass.getDeclaredField(strArr[i])
                    if (!declaredField.isAccessible) {
                        declaredField.isAccessible = true
                    }
                    val `object` = declaredField[imm]
                    if (`object` !is View) {
                        i++
                        continue
                    }
                    if (`object`.context === context) {
                        declaredField[imm] = null
                    } else {
                        return
                    }
                } catch (ignore: Throwable) {
                }
                i++
            }
        } catch (e: Exception) {
        }
        return
    }

    /**
     * 自动切换键盘状态, 如果键盘显示则隐藏反之显示
     * <pre>
     * // 无法获取键盘是否打开 ( 不准确 )
     * InputMethodManager.isActive()
     * // 获取状态有些版本可以, 不适用
     * Activity.getWindow().getAttributes().softInputMode
     *
     *
     * 可以配合 [.isSoftInputVisible] 判断是否显示输入法
    </pre> *
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun toggleKeyboard(): Boolean {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "toggleKeyboard")
        }
        return false
    }
    // =============
    // = 打开软键盘 =
    // =============
    /**
     * 打开软键盘
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun openKeyboard(): Boolean {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "openKeyboard")
        }
        return false
    }

    /**
     * 延时打开软键盘
     * @param delayMillis 延迟时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun openKeyboardDelay(delayMillis: Long = DELAY_MILLIS): Boolean {
        sMainHandler.postDelayed({ openKeyboard() }, delayMillis)
        return true
    }
    // =
    /**
     * 打开软键盘
     * @param editText [EditText]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun openKeyboard(editText: EditText): Boolean {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "openKeyboard")
        }
        return false
    }

    /**
     * 延时打开软键盘
     * @param editText    [EditText]
     * @param delayMillis 延迟时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun openKeyboardDelay(editText: EditText, delayMillis: Long = DELAY_MILLIS) {
        sMainHandler.postDelayed({
            try {
                editText.requestFocus()
                editText.setSelection(editText.text.toString().length)
            } catch (e: Exception) {
            }
            openKeyboard(editText)
        }, delayMillis)
    }
    // =============
    // = 关闭软键盘 =
    // =============
    /**
     * 关闭软键盘
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun closeKeyboard(): Boolean {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "closeKeyboard")
        }
        return false
    }

    /**
     * 关闭软键盘
     * @param editText [EditText]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun closeKeyboard(editText: EditText): Boolean {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "closeKeyboard")
        }
        return false
    }

    /**
     * 关闭软键盘
     * @param activity [Activity]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun closeKeyboard(activity: Activity): Boolean {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            imm.hideSoftInputFromWindow(activity.window.peekDecorView().windowToken, 0)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "closeKeyboard")
        }
        return false
    }

    /**
     * 关闭 dialog 中打开的键盘
     * @param dialog [Dialog]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun closeKeyboard(dialog: Dialog): Boolean {
        try {
            val imm: InputMethodManager = AppUtils.inputMethodManager
            imm.hideSoftInputFromWindow(dialog.window!!.peekDecorView().windowToken, 0)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "closeKeyboard")
        }
        return false
    }

    /**
     * 关闭软键盘
     * @param editText [EditText]
     * @param dialog   [Dialog]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun closeKeyBoardSpecial(editText: EditText, dialog: Dialog): Boolean {
        try {
            closeKeyboard()
            closeKeyboard(editText)
            closeKeyboard(dialog)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "closeKeyBoardSpecial")
        }
        return false
    }

    /**
     * 延时关闭软键盘
     * @param editText    [EditText]
     * @param dialog      [Dialog]
     * @param delayMillis 延迟时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun closeKeyBoardSpecialDelay(
        editText: EditText,
        dialog: Dialog,
        delayMillis: Long = DELAY_MILLIS
    ): Boolean {
        sMainHandler.postDelayed({ closeKeyBoardSpecial(editText, dialog) }, delayMillis)
        return true
    }

    /**
     * 延时关闭软键盘
     * @param delayMillis 延迟时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun closeKeyboardDelay(delayMillis: Long = DELAY_MILLIS): Boolean {
        sMainHandler.postDelayed({ closeKeyboard() }, delayMillis)
        return true
    }

    /**
     * 延时关闭软键盘
     * @param editText    [EditText]
     * @param delayMillis 延迟时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun closeKeyboardDelay(editText: EditText?, delayMillis: Long = DELAY_MILLIS): Boolean {
        if (editText != null) {
            sMainHandler.postDelayed({ closeKeyboard(editText) }, delayMillis)
            return true
        }
        return false
    }

    /**
     * 延时关闭软键盘
     * @param activity    [Activity]
     * @param delayMillis 延迟时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun closeKeyboardDelay(activity: Activity?, delayMillis: Long = DELAY_MILLIS): Boolean {
        if (activity != null) {
            sMainHandler.postDelayed({ closeKeyboard(activity) }, delayMillis)
            return true
        }
        return false
    }

    /**
     * 延时关闭软键盘
     * @param dialog      [Dialog]
     * @param delayMillis 延迟时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun closeKeyboardDelay(dialog: Dialog?, delayMillis: Long = DELAY_MILLIS): Boolean {
        if (dialog != null) {
            sMainHandler.postDelayed({ closeKeyboard(dialog) }, delayMillis)
            return true
        }
        return false
    }

    /**
     * 软键盘弹出、隐藏改变事件
     */
    interface OnSoftInputChangedListener {
        /**
         * 软键盘弹出、隐藏改变通知
         * @param visible 是否显示了软键盘
         * @param height  软键盘高度
         */
        fun onSoftInputChanged(visible: Boolean, height: Int)
    }
}