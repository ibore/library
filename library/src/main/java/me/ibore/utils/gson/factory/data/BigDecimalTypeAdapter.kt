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
 * time   : 2021/01/01
 * desc   : BigDecimal 类型解析适配器，参考：[com.google.gson.internal.bind.TypeAdapters.BIG_DECIMAL]
 */
class BigDecimalTypeAdapter : TypeAdapter<BigDecimal?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): BigDecimal? {
        return when (reader.peek()) {
            JsonToken.NUMBER, JsonToken.STRING -> {
                val result = reader.nextString()
                if (result == null || "" == result) {
                    BigDecimal(0)
                } else BigDecimal(reader.nextString())
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
    override fun write(out: JsonWriter, value: BigDecimal?) {
        out.value(value)
    }
}