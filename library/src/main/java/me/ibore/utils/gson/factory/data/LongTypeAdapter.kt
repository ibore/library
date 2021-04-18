package me.ibore.utils.gson.factory.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.math.BigDecimal

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2020/05/05
 * desc   : long / Long 类型解析适配器，参考：[com.google.gson.internal.bind.TypeAdapters.LONG]
 */
@Suppress("UNREACHABLE_CODE")
class LongTypeAdapter : TypeAdapter<Long?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Long? {
        return when (reader.peek()) {
            JsonToken.NUMBER -> {
                return try {
                    reader.nextLong()
                } catch (e: NumberFormatException) {
                    // 如果带小数点则会抛出这个异常
                    BigDecimal(reader.nextString()).toLong()
                }
                val result = reader.nextString()
                return if (result == null || "" == result) {
                    0L
                } else try {
                    result.toLong()
                } catch (e: NumberFormatException) {
                    // 如果带小数点则会抛出这个异常
                    BigDecimal(result).toLong()
                }
                reader.nextNull()
                null
            }
            JsonToken.STRING -> {
                val result = reader.nextString()
                return if (result == null || "" == result) {
                    0L
                } else try {
                    result.toLong()
                } catch (e: NumberFormatException) {
                    BigDecimal(result).toLong()
                }
                reader.nextNull()
                null
            }
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Long?) {
        out.value(value)
    }
}