package me.ibore.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/01/07
 * desc  : utils about json
</pre> *
 */
object JsonUtils {

    private const val TYPE_BOOLEAN: Byte = 0x00
    private const val TYPE_INT: Byte = 0x01
    private const val TYPE_LONG: Byte = 0x02
    private const val TYPE_DOUBLE: Byte = 0x03
    private const val TYPE_STRING: Byte = 0x04
    private const val TYPE_JSON_OBJECT: Byte = 0x05
    private const val TYPE_JSON_ARRAY: Byte = 0x06

    @JvmStatic
    @JvmOverloads
    fun getBoolean(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Boolean = false
    ): Boolean {
        return getValueByType(jsonObject, key, defaultValue, TYPE_BOOLEAN)
    }

    @JvmStatic
    @JvmOverloads
    fun getBoolean(
        json: String?,
        key: String?,
        defaultValue: Boolean = false
    ): Boolean {
        return getValueByType(json, key, defaultValue, TYPE_BOOLEAN)
    }

    @JvmStatic
    @JvmOverloads
    fun getInt(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Int = -1
    ): Int {
        return getValueByType(jsonObject, key, defaultValue, TYPE_INT)
    }

    @JvmStatic
    @JvmOverloads
    fun getInt(
        json: String?,
        key: String?,
        defaultValue: Int = -1
    ): Int {
        return getValueByType(json, key, defaultValue, TYPE_INT)
    }

    @JvmStatic
    @JvmOverloads
    fun getLong(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Long = -1
    ): Long {
        return getValueByType(jsonObject, key, defaultValue, TYPE_LONG)
    }

    @JvmStatic
    @JvmOverloads
    fun getLong(
        json: String?,
        key: String?,
        defaultValue: Long = -1
    ): Long {
        return getValueByType(json, key, defaultValue, TYPE_LONG)
    }

    @JvmStatic
    @JvmOverloads
    fun getDouble(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Double = -1.0
    ): Double {
        return getValueByType(jsonObject, key, defaultValue, TYPE_DOUBLE)
    }

    @JvmStatic
    @JvmOverloads
    fun getDouble(
        json: String?,
        key: String?,
        defaultValue: Double = -1.0
    ): Double {
        return getValueByType(json, key, defaultValue, TYPE_DOUBLE)
    }

    @JvmStatic
    @JvmOverloads
    fun getString(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: String = ""
    ): String {
        return getValueByType(jsonObject, key, defaultValue, TYPE_STRING)
    }

    @JvmStatic
    @JvmOverloads
    fun getString(
        json: String?,
        key: String?,
        defaultValue: String = ""
    ): String {
        return getValueByType(json, key, defaultValue, TYPE_STRING)
    }

    fun getJSONObject(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: JSONObject
    ): JSONObject {
        return getValueByType(jsonObject, key, defaultValue, TYPE_JSON_OBJECT)
    }

    fun getJSONObject(
        json: String?,
        key: String?,
        defaultValue: JSONObject
    ): JSONObject {
        return getValueByType(json, key, defaultValue, TYPE_JSON_OBJECT)
    }

    fun getJSONArray(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: JSONArray
    ): JSONArray {
        return getValueByType(jsonObject, key, defaultValue, TYPE_JSON_ARRAY)
    }

    fun getJSONArray(
        json: String?,
        key: String?,
        defaultValue: JSONArray
    ): JSONArray {
        return getValueByType(json, key, defaultValue, TYPE_JSON_ARRAY)
    }

    private fun <T> getValueByType(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: T,
        type: Byte
    ): T {
        return if (jsonObject == null || key == null || key.isEmpty()) {
            defaultValue
        } else try {
            val ret: Any = when (type) {
                TYPE_BOOLEAN -> {
                    jsonObject.getBoolean(key)
                }
                TYPE_INT -> {
                    jsonObject.getInt(key)
                }
                TYPE_LONG -> {
                    jsonObject.getLong(key)
                }
                TYPE_DOUBLE -> {
                    jsonObject.getDouble(key)
                }
                TYPE_STRING -> {
                    jsonObject.getString(key)
                }
                TYPE_JSON_OBJECT -> {
                    jsonObject.getJSONObject(key)
                }
                TYPE_JSON_ARRAY -> {
                    jsonObject.getJSONArray(key)
                }
                else -> {
                    return defaultValue
                }
            }
            ret as T
        } catch (e: JSONException) {
            e.printStackTrace()
            defaultValue
        }
    }

    private fun <T> getValueByType(
        json: String?,
        key: String?,
        defaultValue: T,
        type: Byte
    ): T {
        return if (json == null || json.isEmpty() || key == null || key.isEmpty()) {
            defaultValue
        } else try {
            getValueByType(JSONObject(json), key, defaultValue, type)
        } catch (e: JSONException) {
            e.printStackTrace()
            defaultValue
        }
    }

    @JvmOverloads
    fun formatJson(json: String, indentSpaces: Int = 4): String {
        try {
            var i = 0
            val len = json.length
            while (i < len) {
                val c = json[i]
                if (c == '{') {
                    return JSONObject(json).toString(indentSpaces)
                } else if (c == '[') {
                    return JSONArray(json).toString(indentSpaces)
                } else if (!Character.isWhitespace(c)) {
                    return json
                }
                i++
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json
    }
}