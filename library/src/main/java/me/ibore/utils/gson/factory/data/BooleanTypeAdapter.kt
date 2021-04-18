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
 * desc   : boolean / Boolean 类型解析适配器，参考：[com.google.gson.internal.bind.TypeAdapters.BOOLEAN]
 */
class BooleanTypeAdapter : TypeAdapter<Boolean?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Boolean? {
        return when (reader.peek()) {
            JsonToken.BOOLEAN -> reader.nextBoolean()
            JsonToken.STRING ->                 // 如果后台返回 "true" 或者 "TRUE"，则处理为 true，否则为 false
                reader.nextString().toBoolean()
            JsonToken.NUMBER ->                 // 如果后台返回的是非 0 的数值则处理为 true，否则为 false
                reader.nextInt() != 0
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
    override fun write(out: JsonWriter, value: Boolean?) {
        out.value(value)
    }
}