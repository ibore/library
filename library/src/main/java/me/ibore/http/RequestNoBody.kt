package me.ibore.http

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

open class RequestNoBody(XHttp: XHttp) : Request<RequestNoBody>(XHttp) {

    private var appendUrl = ""

    fun appendUrl(appendUrl: String): RequestNoBody {
        this.appendUrl = appendUrl
        return this
    }

    override fun generateRequest(): okhttp3.Request {
        var tempUrl = url!! + appendUrl
        if (!tempUrl.startsWith("http")) {
            tempUrl = xHttp.baseUrl() + tempUrl
        }
        try {
            val sb = StringBuilder()
            sb.append(tempUrl)
            if (tempUrl.indexOf('&') > 0 || tempUrl.indexOf('?') > 0) {
                sb.append("&")
            } else {
                sb.append("?")
            }
            for ((key, urlValues) in params) {
                for (value in urlValues) {
                    //对参数进行 utf-8 编码,防止头信息传中文
                    val urlValue = URLEncoder.encode(value, "UTF-8")
                    sb.append(key).append("=").append(urlValue).append("&")
                }
            }
            sb.deleteCharAt(sb.length - 1)
            tempUrl = sb.toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return okhttp3.Request.Builder()
                .url(tempUrl)
                .tag(tag)
                .headers(headers.build())
                .method(method!!, null)
                .build()
    }

}