package me.ibore.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.ktx.layoutInflater
import me.ibore.utils.UtilsBridge.activityList
import me.ibore.utils.UtilsBridge.addActivityLifecycleCallbacks
import me.ibore.utils.UtilsBridge.isActivityAlive
import me.ibore.utils.UtilsBridge.isAppForeground
import me.ibore.utils.UtilsBridge.removeActivityLifecycleCallbacks
import me.ibore.utils.UtilsBridge.view2Bitmap

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/29
 * desc  : utils about toast
</pre> *
 */
class ToastUtils {

    @StringDef(MODE.LIGHT, MODE.DARK)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class MODE {
        companion object {
            const val LIGHT = "light"
            const val DARK = "dark"
        }
    }

    private var mMode: String? = null
    private var mGravity = -1
    private var mXOffset = -1
    private var mYOffset = -1
    private var mBgColor = COLOR_DEFAULT
    private var mBgResource = -1
    private var mTextColor = COLOR_DEFAULT
    private var mTextSize = -1
    private var isLong = false
    private val mIcons = arrayOfNulls<Drawable>(4)
    private var isNotUseSystemToast = false

    /**
     * @param mode The mode.
     * @return the single [ToastUtils] instance
     */
    fun setMode(@MODE mode: String?): ToastUtils {
        mMode = mode
        return this
    }

    /**
     * Set the gravity.
     *
     * @param gravity The gravity.
     * @param xOffset X-axis offset, in pixel.
     * @param yOffset Y-axis offset, in pixel.
     * @return the single [ToastUtils] instance
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int): ToastUtils {
        mGravity = gravity
        mXOffset = xOffset
        mYOffset = yOffset
        return this
    }

    /**
     * Set the color of background.
     *
     * @param backgroundColor The color of background.
     * @return the single [ToastUtils] instance
     */
    fun setBgColor(@ColorInt backgroundColor: Int): ToastUtils {
        mBgColor = backgroundColor
        return this
    }

    /**
     * Set the resource of background.
     *
     * @param bgResource The resource of background.
     * @return the single [ToastUtils] instance
     */
    fun setBgResource(@DrawableRes bgResource: Int): ToastUtils {
        mBgResource = bgResource
        return this
    }

    /**
     * Set the text color of toast.
     *
     * @param msgColor The text color of toast.
     * @return the single [ToastUtils] instance
     */
    fun setTextColor(@ColorInt msgColor: Int): ToastUtils {
        mTextColor = msgColor
        return this
    }

    /**
     * Set the text size of toast.
     *
     * @param textSize The text size of toast.
     * @return the single [ToastUtils] instance
     */
    fun setTextSize(textSize: Int): ToastUtils {
        mTextSize = textSize
        return this
    }

    /**
     * Set the toast for a long period of time.
     *
     * @return the single [ToastUtils] instance
     */
    fun setDurationIsLong(isLong: Boolean): ToastUtils {
        this.isLong = isLong
        return this
    }

    /**
     * Set the left icon of toast.
     *
     * @param resId The left icon resource identifier.
     * @return the single [ToastUtils] instance
     */
    fun setLeftIcon(@DrawableRes resId: Int): ToastUtils {
        return setLeftIcon(ContextCompat.getDrawable(Utils.app, resId))
    }

    /**
     * Set the left icon of toast.
     *
     * @param drawable The left icon drawable.
     * @return the single [ToastUtils] instance
     */
    fun setLeftIcon(drawable: Drawable?): ToastUtils {
        mIcons[0] = drawable
        return this
    }

    /**
     * Set the top icon of toast.
     *
     * @param resId The top icon resource identifier.
     * @return the single [ToastUtils] instance
     */
    fun setTopIcon(@DrawableRes resId: Int): ToastUtils {
        return setTopIcon(ContextCompat.getDrawable(Utils.app, resId))
    }

    /**
     * Set the top icon of toast.
     *
     * @param drawable The top icon drawable.
     * @return the single [ToastUtils] instance
     */
    fun setTopIcon(drawable: Drawable?): ToastUtils {
        mIcons[1] = drawable
        return this
    }

    /**
     * Set the right icon of toast.
     *
     * @param resId The right icon resource identifier.
     * @return the single [ToastUtils] instance
     */
    fun setRightIcon(@DrawableRes resId: Int): ToastUtils {
        return setRightIcon(ContextCompat.getDrawable(Utils.app, resId))
    }

    /**
     * Set the right icon of toast.
     *
     * @param drawable The right icon drawable.
     * @return the single [ToastUtils] instance
     */
    fun setRightIcon(drawable: Drawable?): ToastUtils {
        mIcons[2] = drawable
        return this
    }

    /**
     * Set the left bottom of toast.
     *
     * @param resId The bottom icon resource identifier.
     * @return the single [ToastUtils] instance
     */
    fun setBottomIcon(resId: Int): ToastUtils {
        return setBottomIcon(ContextCompat.getDrawable(Utils.app, resId))
    }

    /**
     * Set the bottom icon of toast.
     *
     * @param drawable The bottom icon drawable.
     * @return the single [ToastUtils] instance
     */
    fun setBottomIcon(drawable: Drawable?): ToastUtils {
        mIcons[3] = drawable
        return this
    }

    /**
     * Set not use system toast.
     *
     * @return the single [ToastUtils] instance
     */
    fun setNotUseSystemToast(): ToastUtils {
        isNotUseSystemToast = true
        return this
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param text The text.
     */
    fun show(text: CharSequence) {
        show(text, duration, this)
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     */
    fun show(@StringRes resId: Int) {
        show(StringUtils.getString(resId), duration, this)
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    fun show(@StringRes resId: Int, vararg args: Any?) {
        show(StringUtils.getString(resId, *args), duration, this)
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    fun show(format: String?, vararg args: Any?) {
        show(StringUtils.format(format!!, *args), duration, this)
    }

    /**
     * Show custom toast.
     */
    fun show(view: View) {
        show(view, duration, this)
    }

    private val duration: Int
        get() = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT

    private fun tryApplyUtilsToastView(text: CharSequence?): View? {
        if (MODE.DARK != mMode && MODE.LIGHT != mMode
            && mIcons[0] == null && mIcons[1] == null && mIcons[2] == null && mIcons[3] == null
        ) {
            return null
        }
        val toastView = Utils.app.applicationContext.layoutInflater.inflate(R.layout.utils_toast_view, null)
        val messageTv = toastView.findViewById<TextView>(android.R.id.message)
        if (MODE.DARK == mMode) {
            val bg = toastView.background.mutate() as GradientDrawable
            bg.setColor(Color.parseColor("#BB000000"))
            messageTv.setTextColor(Color.WHITE)
        }
        messageTv.text = text
        if (mIcons[0] != null) {
            val leftIconView = toastView.findViewById<View>(R.id.utvLeftIconView)
            ViewCompat.setBackground(leftIconView, mIcons[0])
            leftIconView.visibility = View.VISIBLE
        }
        if (mIcons[1] != null) {
            val topIconView = toastView.findViewById<View>(R.id.utvTopIconView)
            ViewCompat.setBackground(topIconView, mIcons[1])
            topIconView.visibility = View.VISIBLE
        }
        if (mIcons[2] != null) {
            val rightIconView = toastView.findViewById<View>(R.id.utvRightIconView)
            ViewCompat.setBackground(rightIconView, mIcons[2])
            rightIconView.visibility = View.VISIBLE
        }
        if (mIcons[3] != null) {
            val bottomIconView = toastView.findViewById<View>(R.id.utvBottomIconView)
            ViewCompat.setBackground(bottomIconView, mIcons[3])
            bottomIconView.visibility = View.VISIBLE
        }
        return toastView
    }

    internal class SystemToast(toastUtils: ToastUtils) : AbsToast(toastUtils) {

        init {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                try {
                    val mTNField = Toast::class.java.getDeclaredField("mTN")
                    mTNField.isAccessible = true
                    val mTN = mTNField[mToast]
                    val mTNmHandlerField = mTNField.type.getDeclaredField("mHandler")
                    mTNmHandlerField.isAccessible = true
                    val tnHandler = mTNmHandlerField[mTN] as Handler
                    mTNmHandlerField[mTN] = SafeHandler(tnHandler)
                } catch (ignored: Exception) { /**/
                }
            }
        }

        override fun show(duration: Int) {
            if (mToast == null) return
            mToast!!.duration = duration
            mToast!!.show()
        }

        internal class SafeHandler(private val impl: Handler) : Handler() {
            override fun handleMessage(msg: Message) {
                impl.handleMessage(msg)
            }

            override fun dispatchMessage(msg: Message) {
                try {
                    impl.dispatchMessage(msg)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal class WindowManagerToast(toastUtils: ToastUtils, type: Int) : AbsToast(toastUtils) {

        private var mWM: WindowManager? = null

        private val mParams: WindowManager.LayoutParams = WindowManager.LayoutParams()

        private val mActivityLifecycleCallbacks: Utils.ActivityLifecycleCallbacks? = null

        init {
            mParams.type = type
        }

        override fun show(duration: Int) {
            if (mToast == null) return
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            mParams.format = PixelFormat.TRANSLUCENT
            mParams.windowAnimations = android.R.style.Animation_Toast
            mParams.title = "ToastWithoutNotification"
            mParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mParams.packageName = AppUtils.appPackageName
            mParams.gravity = mToast!!.gravity
            if (mParams.gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f
            }
            if (mParams.gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f
            }
            mParams.x = mToast!!.xOffset
            mParams.y = mToast!!.yOffset
            mParams.horizontalMargin = mToast!!.horizontalMargin
            mParams.verticalMargin = mToast!!.verticalMargin
            mWM = Utils.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            try {
                if (mWM != null) {
                    mWM!!.addView(mToastView, mParams)
                }
            } catch (ignored: Exception) {
            }
            ThreadUtils.runOnUiThreadDelayed(
                { cancel() },
                if (duration == Toast.LENGTH_SHORT) 2000 else 3500.toLong()
            )
        }

        override fun cancel() {
            try {
                if (mWM != null) {
                    mWM!!.removeViewImmediate(mToastView)
                    mWM = null
                }
            } catch (ignored: Exception) { /**/
            }
            super.cancel()
        }

    }

    internal class ActivityToast(toastUtils: ToastUtils) : AbsToast(toastUtils) {
        private var mActivityLifecycleCallbacks: Utils.ActivityLifecycleCallbacks? = null
        override fun show(duration: Int) {
            if (mToast == null) return
            if (!isAppForeground) {
                // try to use system toast
                showSystemToast(duration)
                return
            }
            var hasAliveActivity = false
            for (activity in activityList) {
                if (!isActivityAlive(activity)) {
                    continue
                }
                hasAliveActivity = true
                showWithActivity(activity, sShowingIndex, true)
            }
            if (hasAliveActivity) {
                registerLifecycleCallback()
                ThreadUtils.runOnUiThreadDelayed(
                    { cancel() },
                    if (duration == Toast.LENGTH_SHORT) 2000 else 3500.toLong()
                )
                ++sShowingIndex
            } else {
                // try to use system toast
                showSystemToast(duration)
            }
        }

        override fun cancel() {
            if (isShowing) {
                unregisterLifecycleCallback()
                for (activity in activityList) {
                    if (!isActivityAlive(activity)) {
                        continue
                    }
                    val window = activity.window
                    if (window != null) {
                        val decorView = window.decorView as ViewGroup
                        val toastView =
                            decorView.findViewWithTag<View>(TAG_TOAST + (sShowingIndex - 1))
                        if (toastView != null) {
                            try {
                                decorView.removeView(toastView)
                            } catch (ignored: Exception) {
                            }
                        }
                    }
                }
            }
            super.cancel()
        }

        private fun showSystemToast(duration: Int) {
            val systemToast = SystemToast(mToastUtils)
            systemToast.mToast = mToast
            systemToast.show(duration)
        }

        private fun showWithActivity(activity: Activity, index: Int, useAnim: Boolean) {
            val window = activity.window
            if (window != null) {
                val decorView = window.decorView as ViewGroup
                val lp = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.gravity = mToast!!.gravity
                lp.bottomMargin = mToast!!.yOffset + BarUtils.getNavBarHeight(activity)
                lp.leftMargin = mToast!!.xOffset
                val toastViewSnapshot = getToastViewSnapshot(index)
                if (useAnim) {
                    toastViewSnapshot.alpha = 0f
                    toastViewSnapshot.animate().alpha(1f).setDuration(200).start()
                }
                decorView.addView(toastViewSnapshot, lp)
            }
        }

        private fun getToastViewSnapshot(index: Int): View {
            val bitmap = view2Bitmap(mToastView!!)
            val toastIv = ImageView(Utils.app)
            toastIv.tag = TAG_TOAST + index
            toastIv.setImageBitmap(bitmap)
            return toastIv
        }

        private fun registerLifecycleCallback() {
            val index = sShowingIndex
            mActivityLifecycleCallbacks = object : Utils.ActivityLifecycleCallbacks() {
                override fun onActivityCreated(activity: Activity) {
                    if (isShowing) {
                        showWithActivity(activity, index, false)
                    }
                }
            }
            addActivityLifecycleCallbacks(mActivityLifecycleCallbacks!!)
        }

        private fun unregisterLifecycleCallback() {
            if (null != mActivityLifecycleCallbacks) {
                removeActivityLifecycleCallbacks(mActivityLifecycleCallbacks!!)
            }
            mActivityLifecycleCallbacks = null
        }

        private val isShowing: Boolean
            get() = mActivityLifecycleCallbacks != null

        companion object {
            private var sShowingIndex = 0
        }
    }

    internal abstract class AbsToast(toastUtils: ToastUtils) : IToast {
        var mToast: Toast?
        protected var mToastUtils: ToastUtils
        protected var mToastView: View? = null
        override fun setToastView(view: View?) {
            mToastView = view
            mToast!!.view = mToastView
        }

        override fun setToastView(text: CharSequence?) {
            val utilsToastView = mToastUtils.tryApplyUtilsToastView(text)
            if (utilsToastView != null) {
                setToastView(utilsToastView)
                return
            }
            mToastView = mToast!!.view
            if (mToastView == null || mToastView!!.findViewById<View?>(android.R.id.message) == null) {
                setToastView(Utils.app.applicationContext.layoutInflater.inflate(R.layout.utils_toast_view, null))
            }
            val messageTv = mToastView!!.findViewById<TextView>(android.R.id.message)
            messageTv.text = text
            if (mToastUtils.mTextColor != COLOR_DEFAULT) {
                messageTv.setTextColor(mToastUtils.mTextColor)
            }
            if (mToastUtils.mTextSize != -1) {
                messageTv.textSize = mToastUtils.mTextSize.toFloat()
            }
            setBg(messageTv)
        }

        private fun setBg(msgTv: TextView) {
            if (mToastUtils.mBgResource != -1) {
                mToastView!!.setBackgroundResource(mToastUtils.mBgResource)
                msgTv.setBackgroundColor(Color.TRANSPARENT)
            } else if (mToastUtils.mBgColor != COLOR_DEFAULT) {
                val toastBg = mToastView!!.background
                val msgBg = msgTv.background
                if (toastBg != null && msgBg != null) {
                    toastBg.mutate().colorFilter =
                        PorterDuffColorFilter(mToastUtils.mBgColor, PorterDuff.Mode.SRC_IN)
                    msgTv.setBackgroundColor(Color.TRANSPARENT)
                } else if (toastBg != null) {
                    toastBg.mutate().colorFilter =
                        PorterDuffColorFilter(mToastUtils.mBgColor, PorterDuff.Mode.SRC_IN)
                } else if (msgBg != null) {
                    msgBg.mutate().colorFilter =
                        PorterDuffColorFilter(mToastUtils.mBgColor, PorterDuff.Mode.SRC_IN)
                } else {
                    mToastView!!.setBackgroundColor(mToastUtils.mBgColor)
                }
            }
        }

        @CallSuper
        override fun cancel() {
            if (mToast != null) {
                mToast!!.cancel()
            }
            mToast = null
            mToastView = null
        }

        init {
            mToast = Toast(Utils.app)
            mToastUtils = toastUtils
            if (mToastUtils.mGravity != -1 || mToastUtils.mXOffset != -1 || mToastUtils.mYOffset != -1) {
                mToast!!.setGravity(
                    mToastUtils.mGravity,
                    mToastUtils.mXOffset,
                    mToastUtils.mYOffset
                )
            }
        }
    }

    internal interface IToast {
        fun setToastView(view: View?)
        fun setToastView(text: CharSequence?)
        fun show(duration: Int)
        fun cancel()
    }

    class UtilsMaxWidthRelativeLayout : RelativeLayout {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val widthMaxSpec =
                MeasureSpec.makeMeasureSpec(ScreenUtils.screenWidth - SPACING, MeasureSpec.AT_MOST)
            super.onMeasure(widthMaxSpec, heightMeasureSpec)
        }

        companion object {
            private val SPACING = dp2px(80f)
        }
    }

    companion object {
        private const val TAG_TOAST = "TAG_TOAST"
        private const val COLOR_DEFAULT = -0x1000001
        private const val NULL = "toast null"
        private const val NOTHING = "toast nothing"

        /**
         * Return the default [ToastUtils] instance.
         *
         * @return the default [ToastUtils] instance
         */
        val defaultMaker = make()
        private var iToast: IToast? = null

        /**
         * Make a toast.
         *
         * @return the single [ToastUtils] instance
         */
        @JvmStatic
        fun make(): ToastUtils {
            return ToastUtils()
        }

        /**
         * Show the toast for a short period of time.
         *
         * @param text The text.
         */
        @JvmStatic
        fun showShort(text: CharSequence?) {
            show(text, Toast.LENGTH_SHORT, defaultMaker)
        }

        /**
         * Show the toast for a short period of time.
         *
         * @param resId The resource id for text.
         */
        @JvmStatic
        fun showShort(@StringRes resId: Int) {
            show(StringUtils.getString(resId), Toast.LENGTH_SHORT, defaultMaker)
        }

        /**
         * Show the toast for a short period of time.
         *
         * @param resId The resource id for text.
         * @param args  The args.
         */
        @JvmStatic
        fun showShort(@StringRes resId: Int, vararg args: Any?) {
            show(StringUtils.getString(resId, *args), Toast.LENGTH_SHORT, defaultMaker)
        }

        /**
         * Show the toast for a short period of time.
         *
         * @param format The format.
         * @param args   The args.
         */
        @JvmStatic
        fun showShort(format: String?, vararg args: Any?) {
            show(StringUtils.format(format!!, *args), Toast.LENGTH_SHORT, defaultMaker)
        }

        /**
         * Show the toast for a long period of time.
         *
         * @param text The text.
         */
        @JvmStatic
        fun showLong(text: CharSequence?) {
            show(text, Toast.LENGTH_LONG, defaultMaker)
        }

        /**
         * Show the toast for a long period of time.
         *
         * @param resId The resource id for text.
         */
        @JvmStatic
        fun showLong(@StringRes resId: Int) {
            show(StringUtils.getString(resId), Toast.LENGTH_LONG, defaultMaker)
        }

        /**
         * Show the toast for a long period of time.
         *
         * @param resId The resource id for text.
         * @param args  The args.
         */
        @JvmStatic
        fun showLong(@StringRes resId: Int, vararg args: Any?) {
            show(StringUtils.getString(resId, *args), Toast.LENGTH_LONG, defaultMaker)
        }

        /**
         * Show the toast for a long period of time.
         *
         * @param format The format.
         * @param args   The args.
         */
        @JvmStatic
        fun showLong(format: String?, vararg args: Any?) {
            show(StringUtils.format(format!!, *args), Toast.LENGTH_LONG, defaultMaker)
        }

        /**
         * Cancel the toast.
         */
        @JvmStatic
        fun cancel() {
            if (iToast != null) {
                iToast!!.cancel()
                iToast = null
            }
        }

        private fun show(text: CharSequence?, duration: Int, utils: ToastUtils) {
            show(null, getToastFriendlyText(text), duration, utils)
        }

        private fun show(view: View, duration: Int, utils: ToastUtils) {
            show(view, null, duration, utils)
        }

        private fun show(view: View?, text: CharSequence?, duration: Int, utils: ToastUtils) {
            ThreadUtils.runOnUiThread {
                cancel()
                iToast = newToast(utils)
                if (view != null) {
                    iToast!!.setToastView(view)
                } else {
                    iToast!!.setToastView(text)
                }
                iToast!!.show(duration)
            }
        }

        private fun getToastFriendlyText(src: CharSequence?): CharSequence {
            var text = src
            if (text == null) {
                text = NULL
            } else if (text.isEmpty()) {
                text = NOTHING
            }
            return text
        }

        private fun newToast(toastUtils: ToastUtils): IToast {
            if (!toastUtils.isNotUseSystemToast) {
                if (NotificationManagerCompat.from(Utils.app).areNotificationsEnabled()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        return SystemToast(toastUtils)
                    }
                    if (!Settings.canDrawOverlays(Utils.app)) {
                        return SystemToast(toastUtils)
                    }
                }
            }

            // not use system or notification disable
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                return WindowManagerToast(toastUtils, WindowManager.LayoutParams.TYPE_TOAST)
            } else if (Settings.canDrawOverlays(Utils.app)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManagerToast(
                        toastUtils,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    )
                } else {
                    WindowManagerToast(toastUtils, WindowManager.LayoutParams.TYPE_PHONE)
                }
            }
            return ActivityToast(toastUtils)
        }
    }
}