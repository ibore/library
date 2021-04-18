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
 * desc   : String 类型解析适配器，参考：[com.google.gson.internal.bind.TypeAdapters.STRING]
 */
class StringTypeAdapter : TypeAdapter<String?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonToken.STRING, JsonToken.NUMBER -> reader.nextString()
            JsonToken.BOOLEAN ->                 // 对于布尔类型比较特殊，需要做针对性处理
                if (reader.nextBoolean()) "true" else "false"
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
    override fun write(out: JsonWriter, value: String?) {
        out.value(value)
    }
}