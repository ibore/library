package me.ibore.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import me.ibore.utils.ContextUtils
import java.lang.ref.WeakReference

class XWebView : WebView {

    companion object {
        private const val REQUEST_CODE_FILE_CHOOSER = 1246
    }

    private var mActivity: WeakReference<Activity>? = null
    private var mFragment: WeakReference<Fragment>? = null
    private val mHttpHeaders: MutableMap<String, String> = HashMap()
    private var mFilePathCallback: ValueCallback<Array<Uri>?>? = null
    private var mXWebViewListener: XWebViewListener? = null
    private var mRequestCodeFileChooser = REQUEST_CODE_FILE_CHOOSER
    private var mPermitSchemes = LinkedHashSet<String>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        XWebViewSettings.setSettings(this, context)
        mActivity = WeakReference(ContextUtils.getXActivity(context)!!)
        setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val suggestedFilename = URLUtil.guessFileName(url, contentDisposition, mimeType)
            mXWebViewListener?.onDownload(url, suggestedFilename, userAgent, contentDisposition, mimeType, contentLength)
        }
    }

    fun setXWebViewListener(fragment: Fragment, xWebViewListener: XWebViewListener) {
        this.mFragment = WeakReference(fragment)
        setXWebViewListener(xWebViewListener)
    }

    fun setXWebViewListener(xWebViewListener: XWebViewListener) {
        this.mXWebViewListener = xWebViewListener
        webChromeClient = XWebChromeClient(xWebViewListener)
        webViewClient = XWebViewClient(xWebViewListener)
    }

    fun addHttpHeader(name: String, value: String) {
        mHttpHeaders[name] = value
    }

    fun removeHttpHeader(name: String) {
        mHttpHeaders.remove(name)
    }

    fun addPermitScheme(scheme: String) {
        mPermitSchemes.add(scheme)
    }

    fun removePermitScheme(scheme: String) {
        mPermitSchemes.remove(scheme)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        val httpHeaders = LinkedHashMap<String, String>()
        httpHeaders.putAll(mHttpHeaders)
        httpHeaders.putAll(additionalHttpHeaders)
        super.loadUrl(url, httpHeaders)
    }

    override fun loadUrl(url: String) {
        if (mHttpHeaders.isNotEmpty()) {
            super.loadUrl(url, mHttpHeaders)
        } else {
            super.loadUrl(url)
        }
    }

    fun loadUrl(url: String, preventCaching: Boolean) {
        var tempUrl = url
        if (preventCaching) {
            tempUrl = makeUrlUnique(tempUrl)
        }
        loadUrl(tempUrl)
    }

    fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>, preventCaching: Boolean) {
        var tempUrl = url
        if (preventCaching) {
            tempUrl = makeUrlUnique(tempUrl)
        }
        loadUrl(tempUrl, additionalHttpHeaders)
    }

    /**
     * 支持Cookie
     */
    fun setAcceptCookie(enabled: Boolean) {
        CookieManager.getInstance().setAcceptCookie(enabled)
    }

    /**
     * Cookie跨域问题
     */
    fun setAcceptThirdPartyCookies(enabled: Boolean) {
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, enabled)
    }

    /**
     * 桌面模式
     */
    fun setDesktopMode(enabled: Boolean) {
        val webSettings = settings
        val newUserAgent: String = if (enabled) {
            webSettings.userAgentString.replace("Mobile", "eliboM").replace("Android", "diordnA")
        } else {
            webSettings.userAgentString.replace("eliboM", "Mobile").replace("diordnA", "Android")
        }
        webSettings.userAgentString = newUserAgent
        webSettings.useWideViewPort = enabled
        webSettings.loadWithOverviewMode = enabled
        webSettings.setSupportZoom(enabled)
        webSettings.builtInZoomControls = enabled
    }

    internal fun shouldOverrideUrlLoading(uri: Uri, requestHeaders: MutableMap<String, String>) {
        if (uri.scheme.equals("http") || uri.scheme.equals("https")) {
            loadUrl(uri.toString(), requestHeaders)
        } else if (mPermitSchemes.contains(uri.scheme)) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }

    internal fun onShowFileChooser(filePathCallback: ValueCallback<Array<Uri>?>, fileChooserParams: WebChromeClient.FileChooserParams) {
        mFilePathCallback?.onReceiveValue(null)
        mFilePathCallback = filePathCallback
        if (null != mFragment) {
            mFragment?.get()?.startActivityForResult(fileChooserParams.createIntent(), mRequestCodeFileChooser)
        } else {
            mActivity?.get()?.startActivityForResult(fileChooserParams.createIntent(), mRequestCodeFileChooser)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mRequestCodeFileChooser) {
            mFilePathCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            mFilePathCallback = null
        } else {
            mFilePathCallback?.onReceiveValue(null)
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTimers()
    }

    override fun onPause() {
        pauseTimers()
        super.onPause()
    }

    fun onDestroy() {
        try {
            (parent as ViewGroup).removeView(this)
        } catch (ignored: Exception) {
        }
        try {
            removeAllViews()
        } catch (ignored: Exception) {
        }
        destroy()
    }

    private fun makeUrlUnique(url: String): String {
        val unique = StringBuilder()
        unique.append(url)
        if (url.contains("?")) {
            unique.append('&')
        } else {
            if (url.lastIndexOf('/') <= 7) {
                unique.append('/')
            }
            unique.append('?')
        }
        unique.append(System.currentTimeMillis())
        unique.append('=')
        unique.append(1)
        return unique.toString()
    }
}