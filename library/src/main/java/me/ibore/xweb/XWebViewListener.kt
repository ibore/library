package me.ibore.xweb

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient

interface XWebViewListener {

    fun onReceivedTitle(title: String?)

    fun onReceivedIcon(icon: Bitmap?)

    fun onPageStarted(webView: XWebView, url: String?, favicon: Bitmap?)

    fun onProgressChanged(view: XWebView, newProgress: Int)

    fun onPageFinished(webView: XWebView, url: String?)

    fun onDownload(url: String, suggestedFilename: String?, userAgent: String?, contentDisposition: String?, mimeType: String?, contentLength: Long)

    fun shouldOverrideUrlLoading(webView: XWebView, uri: Uri, requestHeaders: MutableMap<String, String>?): Boolean

    fun onShowCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?)

    fun onHideCustomView()

    fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?)

    fun onGeolocationPermissionsHidePrompt()

    fun onJsAlert(view: XWebView, url: String, message: String?, result: JsResult): Boolean

    fun onJsConfirm(webView: XWebView, url: String, message: String?, result: JsResult): Boolean

    fun onJsPrompt(webView: XWebView, url: String, message: String?, defaultValue: String?, result: JsPromptResult): Boolean

    fun onJsBeforeUnload(webView: XWebView, url: String, message: String?, result: JsResult): Boolean

    fun onCreateWindow(webView: XWebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message?): Boolean

    fun onCloseWindow(webView: XWebView)

    fun onRequestFocus(webView: XWebView)

}