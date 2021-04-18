package me.ibore.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import me.ibore.utils.NetworkUtils

object XWebViewSettings {

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled", "MissingPermission")
    fun setSettings(webView: WebView, context: Context) {
        val settings: WebSettings = webView.settings
        // 存储(storage)
        // 启用HTML5 DOM storage API，默认值 false
        settings.domStorageEnabled = true
        // 启用Web SQL Database API，这个设置会影响同一进程内的所有WebView，默认值 false
        // 此API已不推荐使用，参考：https://www.w3.org/TR/webdatabase/
        // 启用Web SQL Database API，这个设置会影响同一进程内的所有WebView，默认值 false
        // 此API已不推荐使用，参考：https://www.w3.org/TR/webdatabase/
        settings.databaseEnabled = true
        // 启用Application Caches API，必需设置有效的缓存路径才能生效，默认值 false
        // 此API已废弃，参考：https://developer.mozilla.org/zh-CN/docs/Web/HTML/Using_the_application_cache
        // 启用Application Caches API，必需设置有效的缓存路径才能生效，默认值 false
        // 此API已废弃，参考：https://developer.mozilla.org/zh-CN/docs/Web/HTML/Using_the_application_cache
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(context.cacheDir.absolutePath)
        // 定位(location)
        settings.setGeolocationEnabled(true)
        // 是否保存表单数据
        settings.saveFormData = true
        // 是否当webview调用requestFocus时为页面的某个元素设置焦点，默认值 true
        settings.setNeedInitialFocus(true)
        // 是否支持viewport属性，默认值 false
        // 页面通过`<meta name="viewport" ... />`自适应手机屏幕
        settings.useWideViewPort = true
        // 是否使用overview mode加载页面，默认值 false
        // 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
        settings.loadWithOverviewMode = true
        // 布局算法
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        // 是否支持Javascript，默认值false
        settings.javaScriptEnabled = true
        // 是否支持多窗口，默认值false
        settings.setSupportMultipleWindows(false)
        // 是否可用Javascript(window.open)打开窗口，默认值 false
        settings.javaScriptCanOpenWindowsAutomatically = false
        // 资源访问
        settings.allowContentAccess = true // 是否可访问Content Provider的资源，默认值 true
        settings.allowFileAccess = true // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.allowFileAccessFromFileURLs = false
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.allowUniversalAccessFromFileURLs = false
        // 资源加载
        settings.loadsImagesAutomatically = true // 是否自动加载图片
        settings.blockNetworkImage = false // 禁止加载网络图片
        settings.blockNetworkLoads = false // 禁止加载所有网络资源
        // 缩放(zoom)
        settings.setSupportZoom(true) // 是否支持缩放
        settings.builtInZoomControls = false // 是否使用内置缩放机制
        settings.displayZoomControls = true // 是否显示内置缩放控件
        // 默认文本编码，默认值 "UTF-8"
        settings.defaultTextEncodingName = "UTF-8"
        settings.defaultFontSize = 16 // 默认文字尺寸，默认值16，取值范围1-72
        settings.defaultFixedFontSize = 16 // 默认等宽字体尺寸，默认值16
        settings.minimumFontSize = 8 // 最小文字尺寸，默认值 8
        settings.minimumLogicalFontSize = 8 // 最小文字逻辑尺寸，默认值 8
        settings.textZoom = 100 // 文字缩放百分比，默认值 100
        // 字体
        settings.standardFontFamily = "sans-serif" // 标准字体，默认值 "sans-serif"
        settings.serifFontFamily = "serif" // 衬线字体，默认值 "serif"
        settings.sansSerifFontFamily = "sans-serif" // 无衬线字体，默认值 "sans-serif"
        settings.fixedFontFamily = "monospace" // 等宽字体，默认值 "monospace"
        settings.cursiveFontFamily = "cursive" // 手写体(草书)，默认值 "cursive"
        settings.fantasyFontFamily = "fantasy" // 幻想体，默认值 "fantasy"
        // 用户是否需要通过手势播放媒体(不会自动播放)，默认值 true
        settings.mediaPlaybackRequiresUserGesture = true
        // 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 是否在离开屏幕时光栅化(会增加内存消耗)，默认值 false
            settings.offscreenPreRaster = false
        }
        if (NetworkUtils.isConnected) {
            // 根据cache-control决定是否从网络上取数据
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        } else {
            // 没网，离线加载，优先加载缓存(即使已经过期)
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        // deprecated
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        settings.databasePath = context.getDir("database", Context.MODE_PRIVATE).path
        settings.setGeolocationDatabasePath(context.filesDir.path)
    }


}