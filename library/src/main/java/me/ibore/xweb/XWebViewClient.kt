package me.ibore.xweb

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import android.webkit.*


class XWebViewClient(private val xWebViewListener: XWebViewListener) : WebViewClient() {

    // 拦截页面加载，返回true表示宿主app拦截并处理了该url，否则返回false由当前WebView处理
    // 此方法在API24被废弃，不处理POST请求
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val overrideUrlLoading = xWebViewListener.shouldOverrideUrlLoading(view as XWebView, Uri.parse(url), null)
        if (!overrideUrlLoading) {
            view.shouldOverrideUrlLoading(Uri.parse(url), LinkedHashMap())
        }
        return true
    }

    // 拦截页面加载，返回true表示宿主app拦截并处理了该url，否则返回false由当前WebView处理
    // 此方法添加于API24，不处理POST请求，可拦截处理子frame的非http请求
    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val overrideUrlLoading = xWebViewListener.shouldOverrideUrlLoading(view as XWebView,
                request.url, request.requestHeaders ?: HashMap())
        if (!overrideUrlLoading) {
            view.shouldOverrideUrlLoading(request.url, request.requestHeaders ?: HashMap())
        }
        return true
    }

    // 此方法废弃于API21，调用于非UI线程
    // 拦截资源请求并返回响应数据，返回null时WebView将继续加载资源
    // 注意：API21以下的AJAX请求会走onLoadResource，无法通过此方法拦截
    override fun shouldInterceptRequest(view: WebView, url: String?): WebResourceResponse? {
        return null
    }


    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        return super.onRenderProcessGone(view, detail)
    }

    override fun onSafeBrowsingHit(view: WebView?, request: WebResourceRequest?, threatType: Int, callback: SafeBrowsingResponse?) {
        super.onSafeBrowsingHit(view, request, threatType, callback)
    }

    // 页面(url)开始加载
    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        xWebViewListener.onPageStarted(view as XWebView, url, favicon)
    }

    // 页面(url)完成加载
    override fun onPageFinished(view: WebView, url: String?) {
        xWebViewListener.onPageFinished(view as XWebView, url)
    }

    // 将要加载资源(url)
    override fun onLoadResource(view: WebView, url: String?) {

    }

    // 这个回调添加于API23，仅用于主框架的导航
    // 通知应用导航到之前页面时，其遗留的WebView内容将不再被绘制。
    // 这个回调可以用来决定哪些WebView可见内容能被安全地回收，以确保不显示陈旧的内容
    // 它最早被调用，以此保证WebView.onDraw不会绘制任何之前页面的内容，随后绘制背景色或需要加载的新内容。
    // 当HTTP响应body已经开始加载并体现在DOM上将在随后的绘制中可见时，这个方法会被调用。
    // 这个回调发生在文档加载的早期，因此它的资源(css,和图像)可能不可用。
    // 如果需要更细粒度的视图更新，查看 postVisualStateCallback(long, WebView.VisualStateCallback).
    // 请注意这上边的所有条件也支持 postVisualStateCallback(long ,WebView.VisualStateCallback)
    override fun onPageCommitVisible(view: WebView?, url: String?) {

    }

    // 加载资源时出错，通常意味着连接不到服务器
    // 由于所有资源加载错误都会调用此方法，所以此方法应尽量逻辑简单
    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest, error: WebResourceError) {
//        if (request.isForMainFrame) {
//
//        }
    }

    // 在加载资源(iframe,image,js,css,ajax...)时收到了 HTTP 错误(状态码>=400)
    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {

    }


    // 是否重新提交表单，默认不重发
    override fun onFormResubmission(view: WebView?, dontResend: Message, resend: Message?) {
        dontResend.sendToTarget()
    }

    // 通知应用可以将当前的url存储在数据库中，意味着当前的访问url已经生效并被记录在内核当中。
    // 此方法在网页加载过程中只会被调用一次，网页前进后退并不会回调这个函数。
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {

    }

    // 加载资源时发生了一个SSL错误，应用必需响应(继续请求或取消请求)
    // 处理决策可能被缓存用于后续的请求，默认行为是取消请求
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        handler.cancel()
    }

    // 此方法添加于API21，在UI线程被调用
    // 处理SSL客户端证书请求，必要的话可显示一个UI来提供KEY。
    // 有三种响应方式：proceed()/cancel()/ignore()，默认行为是取消请求
    // 如果调用proceed()或cancel()，Webview 将在内存中保存响应结果且对相同的"host:port"不会再次调用 onReceivedClientCertRequest
    // 多数情况下，可通过KeyChain.choosePrivateKeyAlias启动一个Activity供用户选择合适的私钥
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest) {
        request.cancel()
    }

    // 处理HTTP认证请求，默认行为是取消请求
    override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler, host: String?, realm: String?) {
        handler.cancel()
    }

    // 通知应用有个已授权账号自动登陆了
    override fun onReceivedLoginRequest(view: WebView?, realm: String?, account: String?, args: String?) {}

    // 给应用一个机会处理按键事件
    // 如果返回true，WebView不处理该事件，否则WebView会一直处理，默认返回false
    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return false
    }

    // 处理未被WebView消费的按键事件
    // WebView总是消费按键事件，除非是系统按键或shouldOverrideKeyEvent返回true
    // 此方法在按键事件分派时被异步调用
    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        super.onUnhandledKeyEvent(view, event)
    }

    // 通知应用页面缩放系数变化
    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {

    }
}