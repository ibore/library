package me.ibore.utils

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.*
import androidx.drawerlayout.widget.DrawerLayout

/**
 * detail: Bar 相关工具类
 * @author Blankj
 * @author Ttt
 * <pre>
 * 所需权限
 * <uses-permission android:name="android.permission.EXPAND_STATUS_BAR"></uses-permission>
</pre> *
 */
object BarUtils {
    // 日志 TAG
    private val TAG = BarUtils::class.java.simpleName

    // ==============
    // = status bar =
    // ==============
    private const val TAG_STATUS_BAR = "TAG_STATUS_BAR"
    private const val TAG_OFFSET = "TAG_OFFSET"
    private const val KEY_OFFSET = -123

    /**
     * 获取 StatusBar 高度
     * @return StatusBar 高度
     */
    val statusBarHeight: Int
        get() {
            try {
                val resources = Resources.getSystem()
                val id = resources.getIdentifier("status_bar_height", "dimen", "android")
                if (id != 0) {
                    return resources.getDimensionPixelSize(id)
                }
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getStatusBarHeight")
            }
            return 0
        }

    /**
     * 判断 StatusBar 是否显示
     * @param activity [Activity]
     * @return `true` yes, `false` no
     */
    fun isStatusBarVisible(activity: Activity): Boolean {
        try {
            val flags: Int = activity.window.attributes.flags
            return flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "isStatusBarVisible")
        }
        return true
    }

    /**
     * 设置 StatusBar 是否显示
     * @param activity  [Activity]
     * @param isVisible 是否显示 StatusBar
     * @return `true` success, `false` fail
     */
    fun setStatusBarVisibility(
        activity: Activity?,
        isVisible: Boolean
    ): Boolean {
        return setStatusBarVisibility(ActivityUtils.getWindow(activity), isVisible)
    }

    /**
     * 设置 StatusBar 是否显示
     * @param window    [Window]
     * @param isVisible 是否显示 StatusBar
     * @return `true` success, `false` fail
     */
    @Suppress("DEPRECATION")
    fun setStatusBarVisibility(
        window: Window,
        isVisible: Boolean
    ): Boolean {
        if (isVisible) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            showStatusBarView(window)
            addMarginTopEqualStatusBarHeight(window)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            hideStatusBarView(window)
            subtractMarginTopEqualStatusBarHeight(window)
        }
        return true
    }

    /**
     * 设置 StatusBar 是否高亮模式
     * @param activity    [Activity]
     * @param isLightMode 是否高亮模式
     * @return `true` success, `false` fail
     */
    fun setStatusBarLightMode(
        activity: Activity,
        isLightMode: Boolean
    ): Boolean {
        return setStatusBarLightMode(ActivityUtils.getWindow(activity), isLightMode)
    }

    /**
     * 设置 StatusBar 是否高亮模式
     * @param window      [Window]
     * @param isLightMode 是否高亮模式
     * @return `true` success, `false` fail
     */
    @Suppress("DEPRECATION")
    fun setStatusBarLightMode(
        window: Window,
        isLightMode: Boolean
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            var vis = decorView.systemUiVisibility
            vis = if (isLightMode) {
                vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = vis
            return true
        }
        return false
    }

    /**
     * 获取 StatusBar 是否高亮模式
     * @param activity [Activity]
     * @return `true` yes, `false` no
     */
    fun isStatusBarLightMode(activity: Activity?): Boolean {
        return isStatusBarLightMode(ActivityUtils.getWindow(activity))
    }

    /**
     * 获取 StatusBar 是否高亮模式
     * @param window [Window]
     * @return `true` yes, `false` no
     */
    @Suppress("DEPRECATION")
    fun isStatusBarLightMode(window: Window?): Boolean {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            val vis = decorView.systemUiVisibility
            return vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0
        }
        return false
    }
    // =
    /**
     * 添加 View 向上 StatusBar 同等高度边距
     * @param view [View]
     * @return `true` success, `false` fail
     */
    fun addMarginTopEqualStatusBarHeight(view: View?): Boolean {
        if (view != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.tag = TAG_OFFSET
            val haveSetOffset = view.getTag(KEY_OFFSET)
            if (haveSetOffset != null && haveSetOffset as Boolean) return false
            val layoutParams: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin, layoutParams.topMargin + statusBarHeight,
                layoutParams.rightMargin, layoutParams.bottomMargin
            )
            view.layoutParams = layoutParams
            view.setTag(KEY_OFFSET, true)
            return true
        }
        return false
    }

    /**
     * 移除 View 向上 StatusBar 同等高度边距
     * @param view [View]
     * @return `true` success, `false` fail
     */
    fun subtractMarginTopEqualStatusBarHeight(view: View): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val haveSetOffset = view.getTag(KEY_OFFSET)
            if (haveSetOffset == null || !haveSetOffset as Boolean) return false
            val layoutParams: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin, layoutParams.topMargin - statusBarHeight,
                layoutParams.rightMargin, layoutParams.bottomMargin
            )
            view.layoutParams = layoutParams
            view.setTag(KEY_OFFSET, false)
            return true
        }
        return false
    }

    /**
     * 添加 View 向上 StatusBar 同等高度边距
     * @param window [Window]
     */
    private fun addMarginTopEqualStatusBarHeight(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val withTag = window.decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
            addMarginTopEqualStatusBarHeight(withTag)
        }
    }

    /**
     * 移除 View 向上 StatusBar 同等高度边距
     * @param window [Window]
     */
    private fun subtractMarginTopEqualStatusBarHeight(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val withTag = window.decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
            subtractMarginTopEqualStatusBarHeight(withTag)
        }
    }

    /**
     * 设置 StatusBar 颜色
     * @param activity [Activity]
     * @param color    背景颜色
     * @param isDecor  `true` add DecorView, `false` add ContentView
     */
    @JvmStatic
    @JvmOverloads
    fun setStatusBarColor(
        activity: Activity,
        color: Int,
        isDecor: Boolean = false
    ): View? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return null
        transparentStatusBar(activity)
        return applyStatusBarColor(activity, color, isDecor)
    }

    /**
     * 设置 StatusBar 颜色
     * @param window  [Window]
     * @param color   背景颜色
     * @param isDecor `true` add DecorView, `false` add ContentView
     */
    @JvmStatic
    @JvmOverloads
    fun setStatusBarColor(
        window: Window,
        color: Int,
        isDecor: Boolean = false
    ): View? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return null
        transparentStatusBar(window)
        return applyStatusBarColor(window, color, isDecor)
    }

    /**
     * 设置 StatusBar 颜色
     * @param fakeStatusBar StatusBar View
     * @param color         背景颜色
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun setStatusBarColor(
        fakeStatusBar: View?,
        color: Int
    ): Boolean {
        if (fakeStatusBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val activity: Activity =
                ActivityUtils.getActivity(fakeStatusBar.context) ?: return false
            transparentStatusBar(activity)
            fakeStatusBar.visibility = View.VISIBLE
            val layoutParams: ViewGroup.LayoutParams = fakeStatusBar.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = statusBarHeight
            fakeStatusBar.layoutParams = layoutParams
            fakeStatusBar.setBackgroundColor(color)
            return true
        }
        return false
    }

    /**
     * 设置自定义 StatusBar View
     * @param fakeStatusBar StatusBar View
     * @return `true` success, `false` fail
     */
    fun setStatusBarCustom(fakeStatusBar: View): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val activity: Activity =
                ActivityUtils.getActivity(fakeStatusBar.context) ?: return false
            transparentStatusBar(activity)
            fakeStatusBar.visibility = View.VISIBLE
            var layoutParams: ViewGroup.LayoutParams? = fakeStatusBar.layoutParams
            if (layoutParams == null) {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    statusBarHeight
                )
                fakeStatusBar.layoutParams = layoutParams
            } else {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = statusBarHeight
                fakeStatusBar.layoutParams = layoutParams
            }
            return true
        }
        return false
    }

    /**
     * 设置 DrawerLayout StatusBar 颜色
     * <pre>
     * DrawLayout 必须添加
     * `android:fitsSystemWindows="true"`
    </pre> *
     * @param drawer        DrawLayout
     * @param fakeStatusBar StatusBar View
     * @param color         背景颜色
     * @param isTop         是否设置 DrawerLayout 为顶层
     * @return `true` success, `false` fail
     */
    @JvmStatic
    @JvmOverloads
    fun setStatusBarColorDrawer(
        drawer: DrawerLayout,
        fakeStatusBar: View,
        color: Int,
        isTop: Boolean = false
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val activity: Activity =
                ActivityUtils.getActivity(fakeStatusBar.context) ?: return false
            transparentStatusBar(activity)
            drawer.setFitsSystemWindows(false)
            setStatusBarColor(fakeStatusBar, color)
            var i = 0
            val count: Int = drawer.getChildCount()
            while (i < count) {
                drawer.getChildAt(i).setFitsSystemWindows(false)
                i++
            }
            if (isTop) {
                hideStatusBarView(activity)
            } else {
                setStatusBarColor(activity, color, false)
            }
            return true
        }
        return false
    }
    // =
    /**
     * 设置透明 StatusBar
     * @param activity [Activity]
     * @return `true` success, `false` fail
     */
    fun transparentStatusBar(activity: Activity): Boolean {
        return transparentStatusBar(ActivityUtils.getWindow(activity))
    }

    /**
     * 设置透明 StatusBar
     * @param window [Window]
     * @return `true` success, `false` fail
     */
    fun transparentStatusBar(window: Window): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                val option =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                val vis = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = option or vis
                window.statusBarColor = Color.TRANSPARENT
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            return true
        }
        return false
    }

    /**
     * 应用 StatusBar View
     * @param activity [Activity]
     * @param color    背景颜色
     * @param isDecor  是否添加在 DecorView 上
     * @return StatusBar View
     */
    private fun applyStatusBarColor(
        activity: Activity,
        color: Int,
        isDecor: Boolean
    ): View {
        return applyStatusBarColor(ActivityUtils.getWindow(activity), color, isDecor)
    }

    /**
     * 应用 StatusBar View
     * @param window  [Window]
     * @param color   背景颜色
     * @param isDecor 是否添加在 DecorView 上
     * @return StatusBar View
     */
    private fun applyStatusBarColor(
        window: Window?,
        color: Int,
        isDecor: Boolean
    ): View? {
        if (window == null) return null
        val parent: ViewGroup =
            if (isDecor) window.decorView as ViewGroup else window.findViewById<View>(
                R.id.content
            ) as ViewGroup
        val fakeStatusBarView: View = parent.findViewWithTag(TAG_STATUS_BAR)
        if (fakeStatusBarView.visibility == View.GONE) {
            fakeStatusBarView.visibility = View.VISIBLE
        }
        fakeStatusBarView.setBackgroundColor(color)
        return fakeStatusBarView
    }

    /**
     * 隐藏 StatusBar View
     * @param activity [Activity]
     */
    private fun hideStatusBarView(activity: Activity) {
        hideStatusBarView(ActivityUtils.getWindow(activity))
    }

    /**
     * 隐藏 StatusBar View
     * @param window [Window]
     */
    private fun hideStatusBarView(window: Window) {
        val decorView: ViewGroup = window.decorView as ViewGroup
        val fakeStatusBarView: View = decorView.findViewWithTag(TAG_STATUS_BAR)
            ?: return
        fakeStatusBarView.visibility = View.GONE
    }

    /**
     * 显示 StatusBar View
     * @param window [Window]
     */
    private fun showStatusBarView(window: Window) {
        val decorView: ViewGroup = window.decorView as ViewGroup
        val fakeStatusBarView: View = decorView.findViewWithTag<View>(TAG_STATUS_BAR)
            ?: return
        fakeStatusBarView.visibility = View.VISIBLE
    }

    /**
     * 创建 StatusBar View
     * @param context [Context]
     * @param color   背景颜色
     * @return StatusBar View
     */
    private fun createStatusBarView(
        context: Context,
        color: Int
    ): View {
        val statusBarView = View(context)
        statusBarView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight
        )
        statusBarView.setBackgroundColor(color)
        statusBarView.tag = TAG_STATUS_BAR
        return statusBarView
    }
    // ==============
    // = action bar =
    // ==============
    /**
     * 获取 ActionBar 高度
     * @return ActionBar 高度
     */
    val actionBarHeight: Int
        get() {
            val tv = TypedValue()
            try {
                if (ResourceUtils.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    return TypedValue.complexToDimensionPixelSize(
                        tv.data,
                        Resources.getSystem().displayMetrics
                    )
                }
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getActionBarHeight")
            }
            return 0
        }
    // ====================
    // = notification bar =
    // ====================
    /**
     * 设置 Notification Bar 是否显示
     * @param isVisible 是否显示 Notification Bar
     * @return `true` success, `false` fail
     */
    fun setNotificationBarVisibility(isVisible: Boolean): Boolean {
        val methodName: String = if (isVisible) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) "expand" else "expandNotificationsPanel"
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) "collapse" else "collapsePanels"
        }
        try {
            @SuppressLint("WrongConstant") val service: Any = AppUtils.getSystemService("statusbar")
            @SuppressLint("PrivateApi") val statusBarManager =
                Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod(methodName)
            expand.invoke(service)
            return true
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "setNotificationBarVisibility")
        }
        return false
    }
    // ==================
    // = navigation bar =
    // ==================
    /**
     * 获取 Navigation Bar 高度
     * @return Navigation Bar 高度
     */
    val navBarHeight: Int
        get() {
            try {
                val resources = Resources.getSystem()
                val id = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (id != 0) {
                    return resources.getDimensionPixelSize(id)
                }
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getNavBarHeight")
            }
            return 0
        }

    /**
     * 设置 Navigation Bar 是否可见
     * @param activity  [Activity]
     * @param isVisible 是否显示 Navigation Bar
     * @return `true` success, `false` fail
     */
    fun setNavBarVisibility(
        activity: Activity?,
        isVisible: Boolean
    ): Boolean {
        return setNavBarVisibility(ActivityUtils.getWindow(activity), isVisible)
    }

    /**
     * 设置 Navigation Bar 是否可见
     * @param window    [Window]
     * @param isVisible 是否显示 Navigation Bar
     * @return `true` success, `false` fail
     */
    fun setNavBarVisibility(
        window: Window?,
        isVisible: Boolean
    ): Boolean {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val decorView: ViewGroup = window.decorView as ViewGroup
            var i = 0
            val count: Int = decorView.getChildCount()
            while (i < count) {
                val child: View = decorView.getChildAt(i)
                val id = child.id
                if (id != View.NO_ID) {
                    val resourceEntryName = Resources.getSystem().getResourceEntryName(id)
                    if ("navigationBarBackground" == resourceEntryName) {
                        child.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
                    }
                }
                i++
            }
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            if (isVisible) {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() and uiOptions.inv())
            } else {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() or uiOptions)
            }
            return true
        }
        return false
    }

    /**
     * 判断 Navigation Bar 是否可见
     * @param activity [Activity]
     * @return `true` yes, `false` no
     */
    fun isNavBarVisible(activity: Activity?): Boolean {
        return isNavBarVisible(ActivityUtils.getWindow(activity))
    }

    /**
     * 判断 Navigation Bar 是否可见
     * @param window [Window]
     * @return `true` yes, `false` no
     */
    fun isNavBarVisible(window: Window?): Boolean {
        if (window != null) {
            var isVisible = false
            val decorView: ViewGroup = window.decorView as ViewGroup
            var i = 0
            val count: Int = decorView.getChildCount()
            while (i < count) {
                val child: View = decorView.getChildAt(i)
                val id = child.id
                if (id != View.NO_ID) {
                    val resourceEntryName = Resources.getSystem().getResourceEntryName(id)
                    if ("navigationBarBackground" == resourceEntryName && child.visibility == View.VISIBLE) {
                        isVisible = true
                        break
                    }
                }
                i++
            }
            if (isVisible) {
                val visibility: Int = decorView.getSystemUiVisibility()
                isVisible = visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
            }
            return isVisible
        }
        return false
    }

    /**
     * 判断是否支持 Navigation Bar
     * @return `true` yes, `false` no
     */
    val isSupportNavBar: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val windowManager: WindowManager = AppUtils.getWindowManager() ?: return false
                val display: Display = windowManager.getDefaultDisplay()
                val size = Point()
                val realSize = Point()
                display.getSize(size)
                display.getRealSize(realSize)
                return realSize.y != size.y || realSize.x != size.x
            }
            val menu: Boolean = ViewConfiguration.get(DevUtils.getContext()).hasPermanentMenuKey()
            val back: Boolean = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            return !menu && !back
        }

    /**
     * 设置 Navigation Bar 颜色
     * @param activity [Activity]
     * @param color    Navigation Bar 颜色
     * @return `true` success, `false` fail
     */
    fun setNavBarColor(
        activity: Activity?,
        color: Int
    ): Boolean {
        return setNavBarColor(ActivityUtils.getWindow(activity), color)
    }

    /**
     * 设置 Navigation Bar 颜色
     * @param window [Window]
     * @param color  Navigation Bar 颜色
     * @return `true` success, `false` fail
     */
    fun setNavBarColor(
        window: Window?,
        color: Int
    ): Boolean {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = color
            return true
        }
        return false
    }

    /**
     * 获取 Navigation Bar 颜色
     * @param activity [Activity]
     * @return Navigation Bar 颜色
     */
    fun getNavBarColor(activity: Activity?): Int {
        return getNavBarColor(ActivityUtils.getWindow(activity))
    }

    /**
     * 获取 Navigation Bar 颜色
     * @param window [Window]
     * @return Navigation Bar 颜色
     */
    fun getNavBarColor(window: Window?): Int {
        return if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor
        } else -1
    }

    /**
     * 设置 Navigation Bar 是否高亮模式
     * @param activity    [Activity]
     * @param isLightMode 是否高亮模式
     * @return `true` success, `false` fail
     */
    fun setNavBarLightMode(
        activity: Activity?,
        isLightMode: Boolean
    ): Boolean {
        return setNavBarLightMode(ActivityUtils.getWindow(activity), isLightMode)
    }

    /**
     * 设置 Navigation Bar 是否高亮模式
     * @param window      [Window]
     * @param isLightMode 是否高亮模式
     * @return `true` success, `false` fail
     */
    fun setNavBarLightMode(
        window: Window?,
        isLightMode: Boolean
    ): Boolean {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            var vis = decorView.systemUiVisibility
            vis = if (isLightMode) {
                vis or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            decorView.systemUiVisibility = vis
            return true
        }
        return false
    }

    /**
     * 获取 Navigation Bar 是否高亮模式
     * @param activity [Activity]
     * @return `true` yes, `false` no
     */
    fun isNavBarLightMode(activity: Activity?): Boolean {
        return isNavBarLightMode(ActivityUtils.getWindow(activity))
    }

    /**
     * 获取 Navigation Bar 是否高亮模式
     * @param window [Window]
     * @return `true` yes, `false` no
     */
    fun isNavBarLightMode(window: Window?): Boolean {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            val vis = decorView.systemUiVisibility
            return vis and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR != 0
        }
        return false
    }
}