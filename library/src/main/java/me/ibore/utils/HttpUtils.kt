package me.ibore.utils

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

object HttpUtils {

    private const val CONNECT_TIMEOUT = 15 * 1000
    private const val READ_TIMEOUT = 15 * 1000
    private const val CHARSET = "UTF-8"
    private const val GET = "GET"
    private const val POST = "POST"

    fun request(url: String): RequestBuilder {
        return RequestBuilder(url)
    }

    private fun request(builder: RequestBuilder): Any? {
        var connection: HttpURLConnection? = null
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        var reader: BufferedReader? = null
        var writer: BufferedWriter? = null
        var raf: RandomAccessFile? = null
        try {
            if (GET == builder.method) {
                if (!builder.args.isNullOrEmpty()) {
                    if (builder.url.contains("?")) {
                        if (builder.url.endsWith("&")) {
                            builder.url = builder.url + builder.args
                        } else {
                            builder.url = builder.url + "&" + builder.args
                        }
                    } else {
                        builder.url = builder.url + "?" + builder.args
                    }
                }
            }
            val mURL = URL(builder.url)
            connection = mURL.openConnection() as HttpURLConnection
            connection.connectTimeout = CONNECT_TIMEOUT
            connection.readTimeout = READ_TIMEOUT
            connection.requestMethod = builder.method
            prepareRequestProperty(connection, builder.properties)
            if (builder.download != null && builder.download!!.downloadUnit != null) {
                val startBytes = builder.download!!.downloadUnit!!.startBytes
                val endBytes = builder.download!!.downloadUnit!!.endBytes
                connection.setRequestProperty(
                    "Range", "bytes=" + startBytes + "-" + if (endBytes > 0) endBytes else ""
                )
            }
            if (POST == builder.method && !builder.args.isNullOrEmpty()) {
                connection.doOutput = true
                connection.useCaches = false
                val out = connection.outputStream
                writer = BufferedWriter(OutputStreamWriter(out, CHARSET))
                writer.write(builder.args)
                writer.flush()
            }
            val code = connection.responseCode
            if (HttpURLConnection.HTTP_OK == code || HttpURLConnection.HTTP_PARTIAL == code) {
                if (builder.download != null && builder.download!!.downloadUnit == null) {
                    return connection.contentLength
                }
                val `in` = connection.inputStream
                return if (builder.download != null) {
                    bis = BufferedInputStream(`in`)
                    if (builder.download!!.downloadUnit!!.startBytes > 0 || builder.download!!.downloadUnit!!.endBytes > 0) {
                        raf = RandomAccessFile(builder.download!!.targetFile, "rw")
                        raf.seek(builder.download!!.downloadUnit!!.startBytes.toLong())
                    } else {
                        bos = BufferedOutputStream(FileOutputStream(builder.download!!.targetFile))
                    }
                    downloadResult(builder, bis, bos, raf)
                    if (builder.download!!.isSupportBreakpoint) {
                        checkRangeFile(builder)
                    }
                    true
                } else {
                    reader = BufferedReader(InputStreamReader(`in`, CHARSET))
                    normalResult(reader)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIOQuietly(bis, bos, reader, writer, raf)
            connection?.disconnect()
        }
        return null
    }

    private fun prepareParams(args: Map<String, String>?): String? {
        if (!args.isNullOrEmpty()) {
            val sb = StringBuilder()
            for (a in args.entries) {
                var key = a.key
                var value = a.value
                key = URLEncoder.encode(key, CHARSET)
                value = URLEncoder.encode(value, CHARSET)
                sb.append(key).append("=").append(value).append("&")
            }
            if (sb.isNotEmpty()) {
                sb.setLength(sb.length - 1)
            }
            return sb.toString()
        }
        return null
    }

    private fun prepareRequestProperty(connection: HttpURLConnection?, property: Map<String, String>?) {
        if (!property.isNullOrEmpty()) {
            for ((key, value) in property) {
                if (connection!!.getRequestProperty(key) == null) {
                    connection.setRequestProperty(key, value)
                } else {
                    connection.addRequestProperty(key, value)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadResult(
        builder: RequestBuilder, bis: BufferedInputStream, bos: BufferedOutputStream?,
        raf: RandomAccessFile?
    ) {
        var totalLength = 0
        try {
            var length: Int
            val buffer = ByteArray(1024 * 4)
            while (bis.read(buffer).also { length = it } > 0 && length != -1) {
                bos?.write(buffer, 0, length) ?: raf?.write(buffer, 0, length)
                totalLength += length
            }
        } catch (e: Exception) {
            if (builder.download!!.isSupportBreakpoint) {
                writeUnitConfig(builder, totalLength)
            }
            throw e
        }
    }

    @Throws(Exception::class)
    private fun normalResult(reader: BufferedReader): String {
        var length: Int
        val buffer = CharArray(1024 * 2)
        val sb = StringBuilder()
        while (reader.read(buffer).also { length = it } > 0 && length != -1) {
            sb.append(buffer, 0, length)
        }
        return sb.toString()
    }

    private fun createDownloadUnits(length: Int, units: Int): Array<DownloadUnit?> {
        return if (units > 1) {
            val unitLength = length / units
            if (unitLength > 0) {
                val unitOverLength = length % units
                val downloadUnits = arrayOfNulls<DownloadUnit>(units)
                var i = 0
                var j = 0
                while (i < length && j < units) {
                    val du = DownloadUnit()
                    du.id = j
                    du.startBytes = i
                    du.endBytes = du.startBytes + unitLength
                    downloadUnits[j] = du
                    i += unitLength
                    j++
                }
                downloadUnits[units - 1]!!.endBytes += unitOverLength
                downloadUnits
            } else {
                val du = createOneDownloadUnit()
                arrayOf(du)
            }
        } else {
            val du = createOneDownloadUnit()
            arrayOf(du)
        }
    }

    private fun createOneDownloadUnit(): DownloadUnit {
        val du = DownloadUnit()
        du.id = 0
        du.startBytes = 0
        du.endBytes = -1
        return du
    }

    private fun createUnitsConfig(rangeFile: File?, downloadUnits: Array<DownloadUnit?>?) {
        var raf: RandomAccessFile? = null
        try {
            raf = RandomAccessFile(rangeFile, "rw")
            if (downloadUnits != null) {
                raf.writeInt(downloadUnits.size)
                for (du in downloadUnits) {
                    raf.writeInt(du!!.startBytes)
                    raf.writeInt(du.endBytes)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIOQuietly(raf)
        }
    }

    private fun readUnitsConfig(rangeFile: File): Array<DownloadUnit?>? {
        var raf: RandomAccessFile? = null
        try {
            raf = RandomAccessFile(rangeFile, "r")
            val units = raf.readInt()
            val downloadUnits = arrayOfNulls<DownloadUnit>(units)
            for (i in 0 until units) {
                val du = DownloadUnit()
                du.id = i
                du.startBytes = raf.readInt()
                du.endBytes = raf.readInt()
                downloadUnits[i] = du
            }
            return downloadUnits
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIOQuietly(raf)
        }
        return null
    }

    private fun writeUnitConfig(builder: RequestBuilder, length: Int) {
        val rangeFile = File(builder.download!!.targetFile!!.absolutePath + ".range")
        var raf: RandomAccessFile? = null
        try {
            val id = builder.download!!.downloadUnit!!.id
            val nowLength = builder.download!!.downloadUnit!!.startBytes + length
            raf = RandomAccessFile(rangeFile, "rw")
            raf.seek((4 + id * 8).toLong())
            raf.writeInt(nowLength)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIOQuietly(raf)
        }
    }

    private fun checkRangeFile(builder: RequestBuilder) {
        val rangeFile = File(builder.download!!.targetFile!!.absolutePath + ".range")
        var raf: RandomAccessFile? = null
        try {
            val id = builder.download!!.downloadUnit!!.id
            val nowLength = builder.download!!.downloadUnit!!.endBytes
            raf = RandomAccessFile(rangeFile, "rw")
            raf.seek((4 + id * 8).toLong())
            raf.writeInt(nowLength)
            raf.seek(0)
            val units = raf.readInt()
            for (i in 0 until units) {
                if (i != id) {
                    val startBytes = raf.readInt()
                    val endBytes = raf.readInt()
                    if (startBytes < endBytes) {
                        return
                    }
                } else {
                    raf.skipBytes(8)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIOQuietly(raf)
        }
        rangeFile.delete()
    }

    class RequestBuilder(var url: String) {
        var method = GET
        var args: String? = null
        var properties: MutableMap<String, String>? = null
        private var params: MutableMap<String, String>? = null
        var download: DownloadBuilder? = null
        fun args(args: String?): RequestBuilder {
            this.args = args
            return this
        }

        fun params(params: MutableMap<String, String>?): RequestBuilder {
            if (this.params == null) {
                this.params = params
            } else {
                this.params!!.putAll(params!!)
            }
            return this
        }

        fun param(key: String, value: String): RequestBuilder {
            if (params == null) {
                params = HashMap()
            }
            params!![key] = value
            return this
        }

        fun properties(properties: MutableMap<String, String>?): RequestBuilder {
            if (this.properties == null) {
                this.properties = properties
            } else {
                this.properties!!.putAll(properties!!)
            }
            return this
        }

        fun property(key: String, value: String): RequestBuilder {
            if (properties == null) {
                properties = HashMap()
            }
            properties!![key] = value
            return this
        }

        fun toRequest(): Any? {
            return if (url.isEmpty()) {
                null
            } else {
                if (args.isNullOrEmpty()) {
                    args = prepareParams(params)
                    params = null
                }
                request(this)
            }
        }

        fun get(): String? {
            method = GET
            val obj = toRequest()
            return if (obj is String) {
                obj
            } else {
                null
            }
        }

        fun post(): String? {
            method = POST
            val obj = toRequest()
            return if (obj is String) {
                obj
            } else {
                null
            }
        }

        fun json(json: String?): String? {
            args = json
            property("Content-Type", "application/json; charset=$CHARSET")
            return post()
        }

        fun download(): DownloadBuilder {
            download = DownloadBuilder(this)
            return download!!
        }
    }

    class DownloadBuilder(private val mRequestBuilder: RequestBuilder) {
        var targetFile: File? = null
        private var targetFilePath: String? = null
        private var targetFileName: String? = null
        var downloadUnit: DownloadUnit? = null
        var isSupportBreakpoint // 是否支持断点续传
                = false

        fun target(target: File?): DownloadBuilder {
            targetFile = target
            return this
        }

        fun targetPath(path: String?): DownloadBuilder {
            targetFilePath = path
            return this
        }

        fun targetPath(path: File?): DownloadBuilder {
            return targetPath(path?.absolutePath)
        }

        fun targetName(name: String?): DownloadBuilder {
            targetFileName = name
            return this
        }

        fun breakpoint(breakpoint: Boolean): DownloadBuilder {
            isSupportBreakpoint = breakpoint
            return this
        }

        fun units(units: Int): Array<DownloadUnit?>? {
            if (!getTargetFile()) {
                return null
            }
            var downloadUnits: Array<DownloadUnit?>?
            var rangeFile: File? = null
            if (isSupportBreakpoint) {
                rangeFile = File(targetFile!!.absolutePath + ".range")
                if (rangeFile.exists()) {
                    downloadUnits = readUnitsConfig(rangeFile)
                    if (downloadUnits != null) {
                        return downloadUnits
                    }
                }
            }
            val length: Int
            if (units <= 1) {
                length = -1
            } else {
                length = contentLength
                if (length < 0) {
                    return null
                }
            }
            downloadUnits = createDownloadUnits(length, units)
            if (isSupportBreakpoint) {
                createUnitsConfig(rangeFile, downloadUnits)
            }
            return downloadUnits
        }

        fun downloadUnit(du: DownloadUnit?): Boolean {
            downloadUnit = du
            return toDownload()
        }

        fun downloadInOneUnit(): Boolean {
            val downloadUnits = units(1)
            return if (downloadUnits != null) {
                downloadUnit = downloadUnits[0]
                toDownload()
            } else {
                false
            }
        }

        private val contentLength: Int
            get() {
                mRequestBuilder.method = GET
                val obj = mRequestBuilder.toRequest()
                return if (obj is Int) {
                    obj
                } else {
                    -1
                }
            }

        private fun getTargetFile(): Boolean {
            if (targetFile == null) {
                if (targetFilePath.isNullOrEmpty() || targetFileName.isNullOrEmpty()) {
                    return false
                }
                targetFile = File(targetFilePath, targetFileName!!)
                targetFileName = null
                targetFilePath = targetFileName
            } else {
                if (!targetFilePath.isNullOrEmpty() && !targetFileName.isNullOrEmpty()) {
                    targetFile = File(targetFilePath, targetFileName!!)
                    targetFileName = null
                    targetFilePath = targetFileName
                }
            }
            val path = targetFile!!.parentFile!!
            return !(!path.exists() && !path.mkdirs())
        }

        private fun toDownload(): Boolean {
            if (!getTargetFile()) {
                return false
            }
            mRequestBuilder.method = GET
            val obj = mRequestBuilder.toRequest()
            return if (obj is Boolean) {
                obj
            } else {
                false
            }
        }
    }

    data class DownloadUnit(
        var id: Int = -1, var startBytes: Int = -1, var endBytes: Int = -1
    )
}