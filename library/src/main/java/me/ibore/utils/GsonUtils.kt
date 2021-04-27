package me.ibore.utils

import android.text.TextUtils
import android.util.ArrayMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.ibore.utils.gson.factory.GsonFactory
import java.io.Reader
import java.lang.reflect.Type

/**
 * Gson工具类
 */
object GsonUtils {

    private const val KEY_DEFAULT = "defaultGson"
    private const val KEY_DELEGATE = "delegateGson"

    private val GSONS: ArrayMap<String, Gson> = ArrayMap()

    /**
     * Set the delegate of [Gson].
     *
     * @param delegate The delegate of [Gson].
     */
    fun setGsonDelegate(delegate: Gson) {
        GSONS[KEY_DELEGATE] = delegate
    }

    /**
     * Set the [Gson] with key.
     *
     * @param key  The key.
     * @param gson The [Gson].
     */
    fun setGson(key: String, gson: Gson) {
        if (TextUtils.isEmpty(key)) return
        GSONS[key] = gson
    }

    /**
     * Return the [Gson] with key.
     *
     * @param key The key.
     * @return the [Gson] with key
     */
    fun getGson(key: String): Gson? {
        return GSONS[key]
    }

    val gson: Gson
        get() {
            val gsonDelegate = GSONS[KEY_DELEGATE]
            if (gsonDelegate != null) {
                return gsonDelegate
            }
            var gsonDefault = GSONS[KEY_DEFAULT]
            if (gsonDefault == null) {
                gsonDefault = createGson()
                GSONS[KEY_DEFAULT] = gsonDefault
            }
            return gsonDefault
        }

    /**
     * Serializes an object into json.
     *
     * @param gson   The gson.
     * @param any The object to serialize.
     * @return object serialized into json.
     */
    @JvmStatic
    @JvmOverloads
    fun toJson(gson: Gson = GsonUtils.gson, any: Any?): String {
        return gson.toJson(any)
    }

    /**
     * Serializes an object into json.
     *
     * @param gson      The gson.
     * @param src       The object to serialize.
     * @param typeOfSrc The specific genericized type of src.
     * @return object serialized into json.
     */
    @JvmStatic
    @JvmOverloads
    fun toJson(gson: Gson = GsonUtils.gson, src: Any?, typeOfSrc: Type): String {
        return gson.toJson(src, typeOfSrc)
    }

    /**
     * Converts [String] to given type.
     *
     * @param gson The gson.
     * @param json The json to convert.
     * @param type Type json will be converted to.
     * @return instance of type
     */
    @JvmStatic
    @JvmOverloads
    fun <T> fromJson(gson: Gson = GsonUtils.gson, json: String?, type: Class<T>): T {
        return gson.fromJson(json, type)
    }

    /**
     * Converts [String] to given type.
     *
     * @param gson The gson.
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @return instance of type
     */
    @JvmStatic
    @JvmOverloads
    fun <T> fromJson(gson: Gson = GsonUtils.gson, json: String?, type: Type): T {
        return gson.fromJson(json, type)
    }

    /**
     * Converts [Reader] to given type.
     *
     * @param gson   The gson.
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */

    @JvmStatic
    @JvmOverloads
    fun <T> fromJson(gson: Gson = GsonUtils.gson, reader: Reader?, type: Class<T>): T {
        return gson.fromJson(reader, type)
    }

    /**
     * Converts [Reader] to given type.
     *
     * @param gson   The gson.
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */

    @JvmStatic
    @JvmOverloads
    fun <T> fromJson(gson: Gson = GsonUtils.gson, reader: Reader?, type: Type): T {
        return gson.fromJson(reader, type)
    }

    /**
     * Return the type of [List] with the `type`.
     *
     * @param type The type.
     * @return the type of [List] with the `type`
     */

    @JvmStatic
    fun getListType(type: Type): Type {
        return TypeToken.getParameterized(MutableList::class.java, type).type
    }

    /**
     * Return the type of [Set] with the `type`.
     *
     * @param type The type.
     * @return the type of [Set] with the `type`
     */

    @JvmStatic
    fun getSetType(type: Type): Type {
        return TypeToken.getParameterized(MutableSet::class.java, type).type
    }

    /**
     * Return the type of map with the `keyType` and `valueType`.
     *
     * @param keyType   The type of key.
     * @param valueType The type of value.
     * @return the type of map with the `keyType` and `valueType`
     */

    @JvmStatic
    fun getMapType(keyType: Type, valueType: Type): Type {
        return TypeToken.getParameterized(MutableMap::class.java, keyType, valueType).type
    }

    /**
     * Return the type of array with the `type`.
     *
     * @param type The type.
     * @return the type of map with the `type`
     */
    @JvmStatic
    fun getArrayType(type: Type): Type {
        return TypeToken.getArray(type).type
    }

    /**
     * Return the type of `rawType` with the `typeArguments`.
     *
     * @param rawType       The raw type.
     * @param typeArguments The type of arguments.
     * @return the type of map with the `type`
     */
    @JvmStatic
    fun getType(rawType: Type, vararg typeArguments: Type): Type {
        return TypeToken.getParameterized(rawType, *typeArguments).type
    }

    @JvmStatic
    private fun createGson(): Gson {
        return GsonFactory.createGsonBuilder().serializeNulls().disableHtmlEscaping().create()
    }
}