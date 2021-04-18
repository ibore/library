package me.ibore.utils.gson.factory.element

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2020/12/08
 * desc   : 字段信息存放，参考：[com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.BoundField]
 */
abstract class BoundField(
    /** 字段名称  */
    val name: String,
    /** 序列化标记  */
    val isSerialized: Boolean,
    /** 反序列化标记  */
    val isDeserialized: Boolean
) {

    @Throws(IOException::class, IllegalAccessException::class)
    abstract fun writeField(value: Any?): Boolean
    @Throws(IOException::class, IllegalAccessException::class)
    abstract fun write(writer: JsonWriter?, value: Any?)
    @Throws(IOException::class, IllegalAccessException::class)
    abstract fun read(reader: JsonReader?, value: Any?)
}