package me.ibore.http

import android.content.Context
import me.ibore.BuildConfig
import me.ibore.utils.Utils
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class XHttp(private val client: OkHttpClient, private val refreshTime: Int,
            private val maxRetry: Int, private val headers: MutableMap<String, String>,
            private val params: MutableMap<String, String>, private val baseUrl: String,
            private val converter: Converter) {

    companion object {

        private var xHttp: XHttp? = null

        @Synchronized
        fun getDefault(): XHttp {
            if (null == xHttp) {
                xHttp = Builder().builder()
            }
            return xHttp!!
        }
    }

    fun client(): OkHttpClient {
        return client
    }

    fun refreshTime(): Int {
        return refreshTime
    }

    fun maxRetry(): Int {
        return maxRetry
    }

    fun headers(): MutableMap<String, String> {
        return headers
    }

    fun params(): MutableMap<String, String> {
        return params
    }

    fun baseUrl(): String {
        return baseUrl
    }

    fun converter(): Converter {
        return converter
    }

    fun get(url: String): RequestNoBody {
        return RequestNoBody(this).url(url).method("GET")
    }

    fun get(tag: Any, url: String): RequestNoBody {
        return RequestNoBody(this).url(url).method("GET").tag(tag)
    }

    fun post(url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("POST")
    }

    fun post(tag: Any, url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("POST").tag(tag)
    }

    fun put(url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("PUT")
    }

    fun put(tag: Any, url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("PUT").tag(tag)
    }

    fun head(url: String): RequestNoBody {
        return RequestNoBody(this).url(url).method("HEAD")
    }

    fun head(tag: Any, url: String): RequestNoBody {
        return RequestNoBody(this).url(url).method("HEAD").tag(tag)
    }

    fun delete(url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("DELETE")
    }

    fun delete(tag: Any, url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("DELETE").tag(tag)
    }

    fun options(url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("OPTIONS")
    }

    fun options(tag: Any, url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("OPTIONS").tag(tag)
    }

    fun patch(url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("PATCH")
    }

    fun patch(tag: Any, url: String): RequestHasBody {
        return RequestHasBody(this).url(url).method("PATCH").tag(tag)
    }

    fun trace(url: String): RequestNoBody {
        return RequestNoBody(this).url(url).method("TRACE")
    }

    fun trace(tag: Any, url: String): RequestNoBody {
        return RequestNoBody(this).url(url).method("TRACE").tag(tag)
    }

    fun isRunning(tag: Any): Boolean {
        for (call in client().dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                return true
            }
        }
        for (call in client().dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                return true
            }
        }
        return false
    }

    fun cancel(tag: Any): Boolean {
        for (call in client().dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
                return true
            }
        }
        for (call in client().dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
                return true
            }
        }
        return false
    }

    fun cancelAll() {
        for (call in client().dispatcher.queuedCalls()) {
            call.cancel()
        }
        for (call in client().dispatcher.runningCalls()) {
            call.cancel()
        }
    }


    class Builder {
        private var context: Context? = null
        private var client: OkHttpClient? = null
        private var refreshTime: Int = 300
        private var maxRetry: Int = 0
        private var headers: MutableMap<String, String> = LinkedHashMap()
        private var params: MutableMap<String, String> = LinkedHashMap()
        private var baseUrl: String = ""
        private var converter: Converter? = null

        constructor() {
            this.context = Utils.app
        }

        constructor(context: Context) {
            this.context = context
        }

        constructor(xHttp: XHttp) {
            this.client = xHttp.client()
            this.refreshTime = xHttp.refreshTime()
            this.maxRetry = xHttp.maxRetry()
            this.headers.putAll(xHttp.headers())
            this.params.putAll(xHttp.params())
            this.baseUrl = xHttp.baseUrl()
            this.converter = xHttp.converter()
        }

        fun client(client: OkHttpClient): Builder {
            this.client = client
            return this
        }

        fun refreshTime(refreshTime: Int): Builder {
            this.refreshTime = refreshTime
            return this
        }

        fun maxRetry(maxRetry: Int): Builder {
            this.maxRetry = maxRetry
            return this
        }

        fun header(key: String, value: String): Builder {
            headers[key] = value
            return this
        }

        fun param(key: String, value: String): Builder {
            params[key] = value
            return this
        }

        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun converter(converter: Converter): Builder {
            this.converter = converter
            return this
        }

        fun builder(): XHttp {
            if (null == client) {
                val logInterceptor = XHttpInterceptor()
                if (BuildConfig.DEBUG) {
                    logInterceptor.setPrintLevel(XHttpInterceptor.Level.BODY)
                } else {
                    logInterceptor.setPrintLevel(XHttpInterceptor.Level.NONE)
                }
                logInterceptor.setColorLevel(Level.WARNING)
                client = OkHttpClient.Builder()
                        .addInterceptor(logInterceptor)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build()
            }
            if (null == converter) converter = DefaultConverter()
            return XHttp(client!!, refreshTime, maxRetry, headers, params, baseUrl, converter!!)
        }
    }

}