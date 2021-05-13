package me.ibore.http

import io.reactivex.*
import io.reactivex.disposables.Disposable
import me.ibore.exception.XException
import me.ibore.base.XObserver
import me.ibore.base.XSubscriber
import me.ibore.exception.HttpException
import me.ibore.http.progress.ProgressListener
import me.ibore.http.progress.ProgressResponseBody
import me.ibore.utils.DisposablesUtils
import me.ibore.utils.ReflectUtils
import me.ibore.utils.ReflexUtils
import okhttp3.Call
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import java.io.File
import java.lang.reflect.Type
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@Suppress("UNCHECKED_CAST")
abstract class Request<R : Request<R>>(protected val xHttp: XHttp) {

    companion object {
        val MEDIA_TYPE_PLAIN = "text/plain;charset=utf-8".toMediaType()
        val MEDIA_TYPE_JSON = "application/json;charset=utf-8".toMediaType()
        val MEDIA_TYPE_STREAM = "application/octet-stream".toMediaType()
    }

    protected var url: String? = null
    protected var tag: Any? = null
    protected var method: String? = null
    protected var refreshTime: Int = xHttp.refreshTime()
    protected var headers = Headers.Builder()
    protected var params = LinkedHashMap<String, MutableList<String>>()
    protected var converter: Converter? = null
    protected var downloadListener: ProgressListener? = null
    protected var isUIThread = true
    protected var call: Call? = null

    init {
        for (key in xHttp.headers().keys) {
            header(key, xHttp.headers()[key]!!)
        }
        for (key in xHttp.params().keys) {
            param(key, xHttp.params()[key]!!)
        }
    }

    fun url(url: String): R {
        this.url = url
        return this as R
    }

    fun tag(tag: Any): R {
        this.tag = tag
        return this as R
    }

    fun method(method: String): R {
        this.method = method
        return this as R
    }

    fun refreshTime(refreshTime: Int): R {
        this.refreshTime = refreshTime
        return this as R
    }

    fun header(key: String, value: String): R {
        headers.add(key, value)
        return this as R
    }

    fun header(headers: Map<String, String>): R {
        for (key in headers.keys) {
            this.headers.add(key, headers[key] ?: error(""))
        }
        return this as R
    }

    fun header(headers: Headers): R {
        for (key in headers.names()) {
            this.headers.add(key, headers[key] ?: error(""))
        }
        return this as R
    }

    fun param(key: String, value: Int, vararg isReplace: Boolean): R {
        return param(key, value.toString(), *isReplace)
    }

    fun param(key: String, value: Long, vararg isReplace: Boolean): R {
        return param(key, value.toString(), *isReplace)
    }

    fun param(key: String, value: Double, vararg isReplace: Boolean): R {
        return param(key, value.toString(), *isReplace)
    }

    fun param(key: String, value: Float, vararg isReplace: Boolean): R {
        return param(key, value.toString(), *isReplace)
    }

    fun param(key: String, value: CharSequence, vararg isReplace: Boolean): R {
        return param(key, value.toString(), *isReplace)
    }

    fun param(key: String, value: String, vararg isReplace: Boolean): R {
        val urlValues = params[key] ?: ArrayList()
        params[key] = urlValues
        if (isReplace.isNotEmpty()) {
            if (isReplace[0]) urlValues.clear()
        } else {
            urlValues.clear()
        }
        urlValues.add(value)
        return this as R
    }

    fun param(params: MutableMap<String, String>, vararg isReplace: Boolean): R {
        for ((key, value) in params) {
            param(key, value, *isReplace)
        }
        return this as R
    }

    fun param(params: MutableMap<String, MutableList<String>>): R {
        this.params.putAll(params)
        return this as R
    }

    fun converter(converter: Converter): R {
        this.converter = converter
        return this as R
    }

    fun download(downloadListener: ProgressListener): R {
        this.downloadListener = downloadListener
        return this as R
    }

    fun uiThread(uiThread: Boolean): R {
        this.isUIThread = uiThread
        return this as R
    }

    @Throws(XException::class)
    fun <T> execute(type: Type): T {
        call = xHttp.client().newCall(generateRequest())
        val response = call!!.execute()
        if (response.isSuccessful) {
            if (null == converter) converter = xHttp.converter()
            return if (null != downloadListener) {
                converter!!.convert(type, response.newBuilder().body(ProgressResponseBody.create(response.body,
                        downloadListener, isUIThread, refreshTime)).build())
            } else {
                converter!!.convert(type, response)
            }
        } else {
            throw HttpException(response.code, response.message)
        }
    }

    fun cancel() {
        call?.cancel()
    }

    protected abstract fun generateRequest(): okhttp3.Request

    @Suppress("NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
    fun <T> observable(observer: XObserver<T>): Disposable {
        return DisposablesUtils.add(tag ?: observer.tag!!, Observable.create {
            try {
                val body: T = execute(ReflexUtils.getTypeByAbstract(observer, 0)!!)!!
                it.onNext(body)
                it.onComplete()
            } catch (e: Exception) {
                if (!observer.isDisposed) it.onError(e)
            }
        }, observer)
    }

    @Suppress("NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
    fun <T> subscriber(subscriber: XSubscriber<T>): Disposable {
        return DisposablesUtils.add(tag ?: subscriber.tag!!, Flowable.create({
            try {
                val body: T = execute(ReflexUtils.getTypeByAbstract(subscriber, 0)!!)
                it.onNext(body)
                it.onComplete()
            } catch (e: Exception) {
                if (!subscriber.isDisposed) it.onError(e)
            }
        }, BackpressureStrategy.DROP), subscriber)
    }

    data class FileWrapper(val file: File, val fileName: String, val contentType: MediaType)

}