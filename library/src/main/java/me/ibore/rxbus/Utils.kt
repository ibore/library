package me.ibore.rxbus

import android.util.Log
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object Utils {
    /**
     * 判断对象是否相等
     *
     * @param o1 对象1
     * @param o2 对象2
     * @return `true`: 相等<br></br>`false`: 不相等
     */
    fun equals(o1: Any?, o2: Any?): Boolean {
        return o1 == o2 || (o1 != null && o1 == o2)
    }

    /**
     * Require the objects are not null.
     *
     * @param objects The object.
     * @throws NullPointerException if any object is null in objects
     */
    fun requireNonNull(vararg objects: Any?) {
        for (any in objects) {
            if (any == null) throw NullPointerException()
        }
    }

    fun <T> getTypeClassFromParadigm(callback: RxBus.Callback<T>?): Class<T>? {
        if (callback == null) return null
        val genericInterfaces: Array<Type> = callback.javaClass.genericInterfaces
        var type: Type?
        type = if (genericInterfaces.size == 1) {
            genericInterfaces[0]
        } else {
            callback.javaClass.genericSuperclass
        }
        type = (type as ParameterizedType).actualTypeArguments[0]
        while (type is ParameterizedType) {
            type = type.rawType
        }
        var className: String = type.toString()
        if (className.startsWith("class ")) {
            className = className.substring(6)
        } else if (className.startsWith("interface ")) {
            className = className.substring(10)
        }
        try {
            return Class.forName(className) as Class<T>
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    fun getClassFromObject(obj: Any?): Class<*>? {
        if (obj == null) return null
        val objClass: Class<*> = obj.javaClass
        if (objClass.isAnonymousClass || objClass.isSynthetic) {
            val genericInterfaces: Array<Type> = objClass.genericInterfaces
            var className: String
            if (genericInterfaces.size == 1) { // interface
                var type: Type = genericInterfaces[0]
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = type.toString()
            } else { // abstract class or lambda
                var type: Type? = objClass.genericSuperclass
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = type.toString()
            }
            if (className.startsWith("class ")) {
                className = className.substring(6)
            } else if (className.startsWith("interface ")) {
                className = className.substring(10)
            }
            try {
                return Class.forName(className)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return objClass
    }

    fun logW(msg: String) {
        Log.w("RxBus", msg)
    }

    fun logE(msg: String) {
        Log.e("RxBus", msg)
    }
}