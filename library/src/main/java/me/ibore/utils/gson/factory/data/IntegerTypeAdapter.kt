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
 * desc   : int / Integer 类型解析适配器，参考：[com.google.gson.internal.bind.TypeAdapters.INTEGER]
 */
@Suppress("UNREACHABLE_CODE")
class IntegerTypeAdapter : TypeAdapter<Int?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Int? {
        return when (reader.peek()) {
            JsonToken.NUMBER -> {
                return try {
                    reader.nextInt()
                } catch (e: NumberFormatException) {
                    // 如果带小数点则会抛出这个异常
                    reader.nextDouble().toInt()
                }
                val result = reader.nextString()
                return if (result == null || "" == result) {
                    0
                } else try {
                    result.toInt()
                } catch (e: NumberFormatException) {
                    // 如果带小数点则会抛出这个异常
                    BigDecimal(result).toFloat().toInt()
                }
                reader.nextNull()
                null
            }
            JsonToken.STRING -> {
                val result = reader.nextString()
                return if (result == null || "" == result) {
                    0
                } else try {
                    result.toInt()
                } catch (e: NumberFormatException) {
                    BigDecimal(result).toFloat().toInt()
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
    override fun write(out: JsonWriter, value: Int?) {
        out.value(value)
    }
}