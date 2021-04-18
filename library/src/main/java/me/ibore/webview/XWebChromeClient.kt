package me.ibore.webview

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.view.View
import android.webkit.*
import me.ibore.utils.LogUtils


class XWebChromeClient(private val xWebViewListener: XWebViewListener) : WebChromeClient() {

    // 获得所有访问历史项目的列表，用于链接着色。
    override fun getVisitedHistory(callback: ValueCallback<Array<String>?>?) {}

    // <video /> 控件在未播放时，会展示为一张海报图，HTML中可通过它的'poster'属性来指定。
    // 如果未指定'poster'属性，则通过此方法提供一个默认的海报图。
    override fun getDefaultVideoPoster(): Bitmap? {
        return null
    }

    // 当全屏的视频正在缓冲时，此方法返回一个占位视图(比如旋转的菊花)。
    override fun getVideoLoadingProgressView(): View? {
        return null
    }

    // 接收当前页面的加载进度
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        xWebViewListener.onProgressChanged(view as XWebView, newProgress)
    }

    // 接收文档标题
    override fun onReceivedTitle(view: WebView?, title: String?) {
        xWebViewListener.onReceivedTitle(title)
    }

    // 接收图标(favicon)
    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        xWebViewListener.onReceivedIcon(icon)
    }

    // Android中处理Touch Icon的方案
    // http://droidyue.com/blog/2015/01/18/deal-with-touch-icon-in-android/index.html
    override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {

    }

    // 通知应用当前页进入了全屏模式，此时应用必须显示一个包含网页内容的自定义View
    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        xWebViewListener.onShowCustomView(view, callback)
    }

    // 通知应用当前页退出了全屏模式，此时应用必须隐藏之前显示的自定义View
    override fun onHideCustomView() {
        xWebViewListener.onHideCustomView()
    }

    // 显示一个alert对话框
    override fun onJsAlert(view: WebView, url: String, message: String?, result: JsResult): Boolean {
        return xWebViewListener.onJsAlert(view as XWebView, url, message, result)
    }

    // 显示一个confirm对话框
    override fun onJsConfirm(view: WebView, url: String, message: String?, result: JsResult): Boolean {
        return xWebViewListener.onJsConfirm(view as XWebView, url, message, result)
    }

    // 显示一个prompt对话框
    override fun onJsPrompt(view: WebView, url: String, message: String?, defaultValue: String?, result: JsPromptResult): Boolean {
        return xWebViewListener.onJsPrompt(view as XWebView, url, message, defaultValue, result)
    }

    // 显示一个对话框让用户选择是否离开当前页面
    override fun onJsBeforeUnload(view: WebView, url: String, message: String?, result: JsResult): Boolean {
        return xWebViewListener.onJsBeforeUnload(view as XWebView, url, message, result)
    }

    // 指定源的网页内容在没有设置权限状态下尝试使用地理位置API。
    // 从API24开始，此方法只为安全的源(https)调用，非安全的源会被自动拒绝
    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        LogUtils.d("onGeolocationPermissionsShowPrompt")
        xWebViewListener.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    // 当前一个调用 onGeolocationPermissionsShowPrompt() 取消时，隐藏相关的UI。
    override fun onGeolocationPermissionsHidePrompt() {
        LogUtils.d("onGeolocationPermissionsHidePrompt")
        xWebViewListener.onGeolocationPermissionsHidePrompt()
    }

    // 通知应用打开新窗口
    override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        LogUtils.d("onCreateWindow:", isDialog, isUserGesture, resultMsg)
        return xWebViewListener.onCreateWindow(view as XWebView, isDialog, isUserGesture, resultMsg)
    }

    // 通知应用关闭窗口
    override fun onCloseWindow(window: WebView?) {
        LogUtils.d("onCloseWindow")
        return xWebViewListener.onCloseWindow(window as XWebView)
    }

    // 请求获取取焦点
    override fun onRequestFocus(view: WebView?) {
        LogUtils.d("onRequestFocus")
        xWebViewListener.onRequestFocus(view as XWebView)
    }

    // 通知应用网页内容申请访问指定资源的权限(该权限未被授权或拒绝)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionRequest(request: PermissionRequest) {
        LogUtils.d("onPermissionRequest")
        request.deny()
    }

    // 通知应用权限的申请被取消，隐藏相关的UI。
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        LogUtils.d("onPermissionRequestCanceled")
    }

    // 为'<input type="file" />'显示文件选择器，返回false使用默认处理
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>?>, fileChooserParams: FileChooserParams): Boolean {
        (webView as XWebView).onShowFileChooser(filePathCallback, fileChooserParams)
        return true
    }

    // 接收JavaScript控制台消息
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        LogUtils.d(consoleMessage?.message())
        return true
    }
}
