package me.ibore.utils

/**
 * <pre>
 * author: Faramarz Afzali
 * time  : 2020/09/05
 * desc  : This class is intended for converting temperatures into different units.
 * C refers to the Celsius unit
 * F refers to the Fahrenheit unit
 * K refers to the Kelvin unit
</pre> *
 */
object TemperatureUtils {

    fun cToF(temp: Float): Float {
        return temp * 9 / 5 + 32
    }

    fun cToK(temp: Float): Float {
        return temp + 273.15f
    }

    fun fToC(temp: Float): Float {
        return (temp - 32) * 5 / 9
    }

    fun fToK(temp: Float): Float {
        return temp + 255.3722222222f
    }

    fun kToC(temp: Float): Float {
        return temp - 273.15f
    }

    fun kToF(temp: Float): Float {
        return temp - 459.67f
    }
}