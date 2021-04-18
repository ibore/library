package me.ibore.utils.gson.factory.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2020/05/05
 * desc   : float / Float 类型解析适配器，参考：[com.google.gson.internal.bind.TypeAdapters.FLOAT]
 */
class FloatTypeAdapter : TypeAdapter<Float?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Float? {
        return when (reader.peek()) {
            JsonToken.NUMBER -> reader.nextDouble().toFloat()
            JsonToken.STRING -> {
                val result = reader.nextString()
                if (result == null || "" == result) {
                    0f
                } else result.toFloat()
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
    override fun write(out: JsonWriter, value: Float?) {
        out.value(value)
    }
}