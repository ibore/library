package me.ibore.utils

import android.Manifest.permission
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.drawerlayout.widget.DrawerLayout
import me.ibore.ktx.getActivity

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/23
 * desc  : utils about bar
</pre> *
 */
object BarUtils {

    private const val TAG_STATUS_BAR = "TAG_STATUS_BAR"
    private const val TAG_OFFSET = "TAG_OFFSET"
    private const val KEY_OFFSET = -123

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    @JvmStatic
    @JvmOverloads
    fun getStatusBarHeight(context: Context = Utils.app): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * Set the status bar's visibility.
     *
     * @param activity  The activity.
     * @param isVisible True to set status bar visible, false otherwise.
     */
    @JvmStatic
    fun setStatusBarVisibility(activity: Activity, isVisible: Boolean) {
        setStatusBarVisibility(activity.window, isVisible)
    }

    /**
     * Set the status bar's visibility.
     *
     * @param window    The window.
     * @param isVisible True to set status bar visible, false otherwise.
     */
    @JvmStatic
    fun setStatusBarVisibility(window: Window, isVisible: Boolean) {
        if (isVisible) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            showStatusBarView(window)
            addMarginTopEqualStatusBarHeight(window)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            hideStatusBarView(window)
            subtractMarginTopEqualStatusBarHeight(window)
        }
    }

    /**
     * Return whether the status bar is visible.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isStatusBarVisible(activity: Activity): Boolean {
        val flags = activity.window.attributes.flags
        return flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0
    }

    /**
     * Set the status bar's light mode.
     *
     * @param activity    The activity.
     * @param isLightMode True to set status bar light mode, false otherwise.
     */
    @JvmStatic
    fun setStatusBarLightMode(activity: Activity, isLightMode: Boolean) {
        setStatusBarLightMode(activity.window, isLightMode)
    }

    /**
     * Set the status bar's light mode.
     *
     * @param window      The window.
     * @param isLightMode True to set status bar light mode, false otherwise.
     */
    @JvmStatic
    fun setStatusBarLightMode(window: Window, isLightMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            var vis = decorView.systemUiVisibility
            vis = if (isLightMode) {
                vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = vis
        }
    }

    /**
     * Is the status bar light mode.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isStatusBarLightMode(activity: Activity): Boolean {
        return isStatusBarLightMode(activity.window)
    }

    /**
     * Is the status bar light mode.
     *
     * @param window The window.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isStatusBarLightMode(window: Window): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            val vis = decorView.systemUiVisibility
            return vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0
        }
        return false
    }

    /**
     * Add the top margin size equals status bar's height for view.
     *
     * @param view The view.
     */
    @JvmStatic
    fun addMarginTopEqualStatusBarHeight(view: View) {
        view.tag = TAG_OFFSET
        val haveSetOffset = view.getTag(KEY_OFFSET)
        if (haveSetOffset != null && haveSetOffset as Boolean) return
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(
            layoutParams.leftMargin,
            layoutParams.topMargin + getStatusBarHeight(),
            layoutParams.rightMargin,
            layoutParams.bottomMargin
        )
        view.setTag(KEY_OFFSET, true)
    }

    /**
     * Subtract the top margin size equals status bar's height for view.
     *
     * @param view The view.
     */
    @JvmStatic
    fun subtractMarginTopEqualStatusBarHeight(view: View) {
        val haveSetOffset = view.getTag(KEY_OFFSET)
        if (haveSetOffset == null || !(haveSetOffset as Boolean)) return
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(
            layoutParams.leftMargin,
            layoutParams.topMargin - getStatusBarHeight(),
            layoutParams.rightMargin,
            layoutParams.bottomMargin
        )
        view.setTag(KEY_OFFSET, false)
    }

    @JvmStatic
    private fun addMarginTopEqualStatusBarHeight(window: Window) {
        val withTag = window.decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
        addMarginTopEqualStatusBarHeight(withTag)
    }

    @JvmStatic
    private fun subtractMarginTopEqualStatusBarHeight(window: Window) {
        val withTag = window.decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
        subtractMarginTopEqualStatusBarHeight(withTag)
    }

    /**
     * Set the status bar's color.
     *
     * @param activity The activity.
     * @param color    The status bar's color.
     */
    @JvmStatic
    fun setStatusBarColor(activity: Activity, @ColorInt color: Int): View {
        return setStatusBarColor(activity, color, false)
    }

    /**
     * Set the status bar's color.
     *
     * @param activity The activity.
     * @param color    The status bar's color.
     * @param isDecor  True to add fake status bar in DecorView,
     * false to add fake status bar in ContentView.
     */
    @JvmStatic
    fun setStatusBarColor(activity: Activity, @ColorInt color: Int, isDecor: Boolean): View {
        transparentStatusBar(activity)
        return applyStatusBarColor(activity, color, isDecor)
    }

    /**
     * Set the status bar's color.
     *
     * @param window The window.
     * @param color  The status bar's color.
     */
    @JvmStatic
    fun setStatusBarColor(window: Window, @ColorInt color: Int): View? {
        return setStatusBarColor(window, color, false)
    }

    /**
     * Set the status bar's color.
     *
     * @param window  The window.
     * @param color   The status bar's color.
     * @param isDecor True to add fake status bar in DecorView,
     * false to add fake status bar in ContentView.
     */
    @JvmStatic
    fun setStatusBarColor(window: Window, @ColorInt color: Int, isDecor: Boolean): View {
        transparentStatusBar(window)
        return applyStatusBarColor(window, color, isDecor)
    }

    /**
     * Set the status bar's color.
     *
     * @param fakeStatusBar The fake status bar view.
     * @param color         The status bar's color.
     */
    @JvmStatic
    fun setStatusBarColor(fakeStatusBar: View, @ColorInt color: Int) {
        val activity = fakeStatusBar.context.getActivity() ?: return
        transparentStatusBar(activity)
        fakeStatusBar.visibility = View.VISIBLE
        val layoutParams = fakeStatusBar.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = getStatusBarHeight()
        fakeStatusBar.setBackgroundColor(color)
    }

    /**
     * Set the custom status bar.
     *
     * @param fakeStatusBar The fake status bar view.
     */
    @JvmStatic
    fun setStatusBarCustom(fakeStatusBar: View) {
        val activity = fakeStatusBar.context.getActivity() ?: return
        transparentStatusBar(activity)
        fakeStatusBar.visibility = View.VISIBLE
        var layoutParams = fakeStatusBar.layoutParams
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight()
            )
            fakeStatusBar.layoutParams = layoutParams
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = getStatusBarHeight()
        }
    }

    /**
     * Set the status bar's color for DrawerLayout.
     *
     * DrawLayout must add `android:fitsSystemWindows="true"`
     *
     * @param drawer        The DrawLayout.
     * @param fakeStatusBar The fake status bar view.
     * @param color         The status bar's color.
     */
    @JvmStatic
    fun setStatusBarColor4Drawer(drawer: DrawerLayout, fakeStatusBar: View, @ColorInt color: Int) {
        setStatusBarColor4Drawer(drawer, fakeStatusBar, color, false)
    }

    /**
     * Set the status bar's color for DrawerLayout.
     *
     * DrawLayout must add `android:fitsSystemWindows="true"`
     *
     * @param drawer        The DrawLayout.
     * @param fakeStatusBar The fake status bar view.
     * @param color         The status bar's color.
     * @param isTop         True to set DrawerLayout at the top layer, false otherwise.
     */
    @JvmStatic
    fun setStatusBarColor4Drawer(
        drawer: DrawerLayout, fakeStatusBar: View, @ColorInt color: Int, isTop: Boolean
    ) {
        val activity = fakeStatusBar.context.getActivity() ?: return
        transparentStatusBar(activity)
        drawer.fitsSystemWindows = false
        setStatusBarColor(fakeStatusBar, color)
        var i = 0
        val count = drawer.childCount
        while (i < count) {
            drawer.getChildAt(i).fitsSystemWindows = false
            i++
        }
        if (isTop) {
            hideStatusBarView(activity)
        } else {
            setStatusBarColor(activity, color, false)
        }
    }

    @JvmStatic
    private fun applyStatusBarColor(activity: Activity, color: Int, isDecor: Boolean): View {
        return applyStatusBarColor(activity.window, color, isDecor)
    }

    @JvmStatic
    private fun applyStatusBarColor(window: Window, color: Int, isDecor: Boolean): View {
        val parent = if (isDecor) window.decorView as ViewGroup
        else (window.findViewById<View>(R.id.content) as ViewGroup)
        var fakeStatusBarView = parent.findViewWithTag<View>(TAG_STATUS_BAR)
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.visibility == View.GONE) {
                fakeStatusBarView.visibility = View.VISIBLE
            }
            fakeStatusBarView.setBackgroundColor(color)
        } else {
            fakeStatusBarView = createStatusBarView(window.context, color)
            parent.addView(fakeStatusBarView)
        }
        return fakeStatusBarView
    }

    @JvmStatic
    private fun hideStatusBarView(activity: Activity) {
        hideStatusBarView(activity.window)
    }

    @JvmStatic
    private fun hideStatusBarView(window: Window) {
        val decorView = window.decorView as ViewGroup
        val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_STATUS_BAR)
            ?: return
        fakeStatusBarView.visibility = View.GONE
    }

    @JvmStatic
    private fun showStatusBarView(window: Window) {
        val decorView = window.decorView as ViewGroup
        val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_STATUS_BAR) ?: return
        fakeStatusBarView.visibility = View.VISIBLE
    }

    @JvmStatic
    private fun createStatusBarView(context: Context, color: Int): View {
        val statusBarView = View(context)
        statusBarView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight()
        )
        statusBarView.setBackgroundColor(color)
        statusBarView.tag = TAG_STATUS_BAR
        return statusBarView
    }

    @JvmStatic
    fun transparentStatusBar(activity: Activity) {
        transparentStatusBar(activity.window)
    }

    @JvmStatic
    fun transparentStatusBar(window: Window) {
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
    }

    /**
     * Return the action bar's height.
     *
     * @return the action bar's height
     */
    @JvmStatic
    val actionBarHeight: Int
        get() {
            val tv = TypedValue()
            return if (Utils.app.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                TypedValue.complexToDimensionPixelSize(tv.data, Utils.app.resources.displayMetrics)
            } else 0
        }
    ///////////////////////////////////////////////////////////////////////////
    // notification bar
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Set the notification bar's visibility.
     *
     * Must hold `<uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />`
     *
     * @param isVisible True to set notification bar visible, false otherwise.
     */
    @JvmStatic
    @RequiresPermission(permission.EXPAND_STATUS_BAR)
    fun setNotificationBarVisibility(isVisible: Boolean) {
        val methodName: String = if (isVisible) "expandNotificationsPanel"
        else "collapsePanels"
        invokePanels(methodName)
    }

    @JvmStatic
    private fun invokePanels(methodName: String) {
        try {
            @SuppressLint("WrongConstant")
            val service = Utils.app.getSystemService("statusbar")

            @SuppressLint("PrivateApi")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod(methodName)
            expand.invoke(service)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // navigation bar
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Return the navigation bar's height.
     *
     * @return the navigation bar's height
     */
    @JvmStatic
    fun getNavBarHeight(context: Context = Utils.app): Int {
        val res = context.resources
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) res.getDimensionPixelSize(resourceId)
        else 0
    }
    /**
     * Set the navigation bar's visibility.
     *
     * @param activity  The activity.
     * @param isVisible True to set navigation bar visible, false otherwise.
     */
    fun setNavBarVisibility(activity: Activity, isVisible: Boolean) {
        setNavBarVisibility(activity.window, isVisible)
    }

    /**
     * Set the navigation bar's visibility.
     *
     * @param window    The window.
     * @param isVisible True to set navigation bar visible, false otherwise.
     */
    @JvmStatic
    fun setNavBarVisibility(window: Window, isVisible: Boolean) {
        val decorView = window.decorView as ViewGroup
        var i = 0
        val count = decorView.childCount
        while (i < count) {
            val child = decorView.getChildAt(i)
            val id = child.id
            if (id != View.NO_ID) {
                val resourceEntryName = getResNameById(id)
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
            decorView.systemUiVisibility = decorView.systemUiVisibility and uiOptions.inv()
        } else {
            decorView.systemUiVisibility = decorView.systemUiVisibility or uiOptions
        }
    }

    /**
     * Return whether the navigation bar visible.
     *
     * Call it in onWindowFocusChanged will get right result.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isNavBarVisible(activity: Activity): Boolean {
        return isNavBarVisible(activity.window)
    }

    /**
     * Return whether the navigation bar visible.
     *
     * Call it in onWindowFocusChanged will get right result.
     *
     * @param window The window.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isNavBarVisible(window: Window): Boolean {
        var isVisible = false
        val decorView = window.decorView as ViewGroup
        var i = 0
        val count = decorView.childCount
        while (i < count) {
            val child = decorView.getChildAt(i)
            val id = child.id
            if (id != View.NO_ID) {
                val resourceEntryName = getResNameById(id)
                if ("navigationBarBackground" == resourceEntryName && child.visibility == View.VISIBLE) {
                    isVisible = true
                    break
                }
            }
            i++
        }
        if (isVisible && RomUtils.isSamsung && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // 对于三星手机，android10以下非OneUI2的版本，比如 s8，note8 等设备上，
            // 导航栏显示存在bug："当用户隐藏导航栏时显示输入法的时候导航栏会跟随显示"，会导致隐藏输入法之后判断错误
            // 这个问题在 OneUI 2 & android 10 版本已修复
            try {
                val name = "navigationbar_hide_bar_enabled"
                return Settings.Global.getInt(Utils.contentResolver, name) == 0
            } catch (ignore: Exception) {
            }
        } else if (isVisible) {
            val visibility = decorView.systemUiVisibility
            isVisible = visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
        }
        return isVisible
    }

    @JvmStatic
    private fun getResNameById(id: Int): String {
        return try {
            Utils.app.resources.getResourceEntryName(id)
        } catch (ignore: Exception) {
            ""
        }
    }

    /**
     * Set the navigation bar's color.
     *
     * @param activity The activity.
     * @param color    The navigation bar's color.
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setNavBarColor(activity: Activity, @ColorInt color: Int) {
        setNavBarColor(activity.window, color)
    }

    /**
     * Set the navigation bar's color.
     *
     * @param window The window.
     * @param color  The navigation bar's color.
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setNavBarColor(window: Window, @ColorInt color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = color
    }

    /**
     * Return the color of navigation bar.
     *
     * @param activity The activity.
     * @return the color of navigation bar
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getNavBarColor(activity: Activity): Int {
        return getNavBarColor(activity.window)
    }

    /**
     * Return the color of navigation bar.
     *
     * @param window The window.
     * @return the color of navigation bar
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getNavBarColor(window: Window): Int {
        return window.navigationBarColor
    }

    /**
     * Return whether the navigation bar visible.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    val isSupportNavBar: Boolean
        get() {
            val wm = Utils.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.y != size.y || realSize.x != size.x
        }

    /**
     * Set the nav bar's light mode.
     *
     * @param activity    The activity.
     * @param isLightMode True to set nav bar light mode, false otherwise.
     */
    @JvmStatic
    fun setNavBarLightMode(activity: Activity, isLightMode: Boolean) {
        setNavBarLightMode(activity.window, isLightMode)
    }

    /**
     * Set the nav bar's light mode.
     *
     * @param window      The window.
     * @param isLightMode True to set nav bar light mode, false otherwise.
     */
    @JvmStatic
    fun setNavBarLightMode(window: Window, isLightMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            var vis = decorView.systemUiVisibility
            vis = if (isLightMode) {
                vis or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            decorView.systemUiVisibility = vis
        }
    }

    /**
     * Is the nav bar light mode.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isNavBarLightMode(activity: Activity): Boolean {
        return isNavBarLightMode(activity.window)
    }

    /**
     * Is the nav bar light mode.
     *
     * @param window The window.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isNavBarLightMode(window: Window): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            val vis = decorView.systemUiVisibility
            return vis and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR != 0
        }
        return false
    }

    @JvmStatic
    fun getStatusNavBarHeight(context: Context = Utils.app): Int {
        return getStatusNavBarHeight(context) + getNavBarHeight(context)
    }

}