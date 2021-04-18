package me.ibore.http

import me.ibore.http.progress.ProgressListener
import me.ibore.http.progress.ProgressRequestBody
import me.ibore.utils.GsonUtils
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLConnection
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

open class RequestHasBody(XHttp: XHttp) : Request<RequestHasBody>(XHttp) {

    private var fileParams = LinkedHashMap<String, MutableList<FileWrapper>>()
    private var requestBody: RequestBody? = null
    private var isMultipart: Boolean = false
    private var uploadListener: ProgressListener? = null

    fun body(json: Any): RequestHasBody {
        this.requestBody = GsonUtils.toJson(json).toRequestBody(MEDIA_TYPE_JSON)
        return this
    }

    fun body(bs: ByteArray): RequestHasBody {
        this.requestBody = bs.toRequestBody(MEDIA_TYPE_STREAM)
        return this
    }

    fun body(requestBody: RequestBody): RequestHasBody {
        this.requestBody = requestBody
        return this
    }

    fun body(file: File): RequestHasBody {
        this.requestBody = file.asRequestBody(MEDIA_TYPE_STREAM)
        return this
    }

    fun param(key: String, file: File, vararg isReplace: Boolean): RequestHasBody {
        param(key, file, file.name, *isReplace)
        return this
    }

    fun param(key: String, file: File, fileName: String, vararg isReplace: Boolean): RequestHasBody {
        val tempFileName = fileName.replace("#", "")   //解决文件名中含有#号异常的问题
        val contentType = URLConnection.getFileNameMap().getContentTypeFor(tempFileName)
        param(key, file, tempFileName, contentType.toMediaTypeOrNull()
                ?: MEDIA_TYPE_STREAM, *isReplace)
        return this
    }

    fun param(key: String, file: File, fileName: String, contentType: MediaType, vararg isReplace: Boolean): RequestHasBody {
        param(key, FileWrapper(file, fileName, contentType), *isReplace)
        return this
    }

    fun param(key: String, fileWrapper: FileWrapper, vararg isReplace: Boolean): RequestHasBody {
        val urlValues = fileParams[key] ?: ArrayList()
        fileParams[key] = urlValues
        if (isReplace.isNotEmpty()) {
            if (isReplace[0]) urlValues.clear()
        } else {
            urlValues.clear()
        }
        urlValues.add(fileWrapper)
        return this
    }

    fun isMultipart(isMultipart: Boolean): RequestHasBody {
        this.isMultipart = isMultipart
        return this
    }

    fun upload(uploadListener: ProgressListener): RequestHasBody {
        this.uploadListener = uploadListener
        return this
    }

    override fun generateRequest(): okhttp3.Request {
        var tempUrl = url!!
        if (!tempUrl.startsWith("http")) {
            tempUrl = xHttp.baseUrl() + tempUrl
        }
        var tempRequestBody = requestBody
        if (null == tempRequestBody) {
            if (fileParams.isEmpty() && !isMultipart) {
                //表单提交，没有文件
                val builder = FormBody.Builder()
                for (key in params.keys) {
                    val urlValues = params[key]
                    for (value in urlValues!!) {
                        builder.addEncoded(key, value)
                    }
                }
                tempRequestBody = builder.build()
            } else {
                //表单提交，有文件
                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                //拼接键值对
                if (params.isNotEmpty()) {
                    for ((key, urlValues) in params) {
                        for (value in urlValues) {
                            builder.addFormDataPart(key, value)
                        }
                    }
                }
                //拼接文件
                for ((key, fileValues) in fileParams) {
                    for (fileWrapper in fileValues) {
                        val fileBody = fileWrapper.file.asRequestBody(fileWrapper.contentType)
                        builder.addFormDataPart(key, fileWrapper.fileName, fileBody)
                    }
                }
                tempRequestBody = builder.build()
            }
        }
        if (null != uploadListener) {
            tempRequestBody = ProgressRequestBody.create(tempRequestBody, uploadListener!!, isUIThread, refreshTime)
        }
        return okhttp3.Request.Builder()
                .url(tempUrl)
                .tag(tag)
                .headers(headers.build())
                .method(method!!, tempRequestBody)
                .build()
    }

}