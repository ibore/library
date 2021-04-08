package me.ibore.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object ReflectionUtils {

    fun getTypeByAbstract(any: Any, position: Int): Type? {
        try {
            val type = any.javaClass.genericSuperclass as ParameterizedType
            return type.actualTypeArguments[position]
        } catch (e: Exception) {}
        return null
    }

    fun getTypeByInterface(any: Any, position: Int, clazz: Class<*>): Type? {
        try {
            for (type in any.javaClass.genericInterfaces) {
                if ((type as ParameterizedType).rawType == clazz) {
                    return type.actualTypeArguments[position]
                }
            }
        } catch (e: Exception) { }
        return null
    }

}