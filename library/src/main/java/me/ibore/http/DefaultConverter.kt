package me.ibore.http

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import me.ibore.exception.XException
import me.ibore.exception.ConvertException
import me.ibore.exception.HttpException
import me.ibore.utils.GsonUtils
import me.ibore.utils.Utils
import okhttp3.internal.closeQuietly
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import java.io.File
import java.lang.reflect.Type


class DefaultConverter : Converter {

    @Suppress("UNCHECKED_CAST")
    @Throws(XException::class)
    override fun <T> convert(type: Type, response: okhttp3.Response): T {
        if (response.isSuccessful) {
            try {
                val body: Any = when (type) {
                    String::class.java -> {
                        response.body!!.string()
                    }
                    Bitmap::class.java -> {
                        val bytes = response.body!!.bytes()
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                    File::class.java -> {
                        val fileName = HttpFileName.getFileName(response)
                        val tempFile = File(Utils.app.filesDir, fileName)
                        if (tempFile.exists() && tempFile.length() == response.body!!.contentLength()) {
                            response.closeQuietly()
                        } else {
                            val source: BufferedSource = response.body!!.source()
                            val sink: BufferedSink = tempFile.sink().buffer()
                            sink.writeAll(source)
                            sink.close()
                            source.close()
                        }
                        tempFile
                    }
                    else -> {
                        GsonUtils.fromJson(response.body!!.string(), type)
                    }
                }
                return body as T
            } catch (e: Exception) {
                throw ConvertException(e)
            }
        } else {
            throw HttpException(response.code)
        }
    }

}