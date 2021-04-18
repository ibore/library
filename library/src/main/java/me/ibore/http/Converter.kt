package me.ibore.http

import me.ibore.exception.XException
import java.lang.reflect.Type

interface Converter {

    @Throws(XException::class)
    fun <T> convert(type: Type, response: okhttp3.Response): T

}