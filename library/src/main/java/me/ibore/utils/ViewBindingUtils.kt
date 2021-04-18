package me.ibore.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import me.ibore.exception.ClientException
import java.lang.reflect.ParameterizedType
import kotlin.jvm.Throws

object ViewBindingUtils {

    @Throws
    @Suppress("UNCHECKED_CAST")
    fun <T> inflate(any: Any, layoutInflater: LayoutInflater, container: ViewGroup?): T {
        return try {
            val type = any.javaClass.genericSuperclass as ParameterizedType
            val clazz = type.actualTypeArguments[0] as Class<T>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
            method.invoke(null, layoutInflater, container, false) as T
        } catch (e: Exception) {
            throw ClientException( "${any.javaClass.simpleName} ViewBinding inflate failure")
        }
    }

}