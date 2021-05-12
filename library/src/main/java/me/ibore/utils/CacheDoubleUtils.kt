package me.ibore.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import me.ibore.utils.constant.CacheConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/06/13
 * desc  : utils about double cache
</pre> *
 */
class CacheDoubleUtils private constructor(
    private val memoryCache: CacheMemoryUtils.MemoryCache,
    private val cacheDiskUtils: CacheDiskUtils
) : CacheConstants {
    /**
     * Put bytes in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: ByteArray?, saveTime: Int = -1) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }


    /**
     * Return the bytes in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bytes if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getBytes(key: String, defaultValue: ByteArray? = null): ByteArray? {
        val obj = memoryCache.get<ByteArray>(key)
        return obj ?: cacheDiskUtils.getBytes(key, defaultValue)
    }

    /**
     * Put string value in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: String?, saveTime: Int = -1) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the string value in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the string value if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getString(key: String, defaultValue: String? = null): String? {
        val obj = memoryCache.get<String>(key)
        return obj ?: cacheDiskUtils.getString(key, defaultValue)
    }

    /**
     * Put JSONObject in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONObject?,
        saveTime: Int = -1
    ) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONObject in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getJSONObject(key: String, defaultValue: JSONObject? = null): JSONObject? {
        val obj = memoryCache.get<JSONObject>(key)
        return obj ?: cacheDiskUtils.getJSONObject(key, defaultValue)
    }

    /**
     * Put JSONArray in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: JSONArray?, saveTime: Int = -1) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONArray in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getJSONArray(key: String, defaultValue: JSONArray? = null): JSONArray? {
        val obj = memoryCache.get<JSONArray>(key)
        return obj ?: cacheDiskUtils.getJSONArray(key, defaultValue)
    }

    /**
     * Put bitmap in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Bitmap?, saveTime: Int = -1) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the bitmap in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getBitmap(key: String, defaultValue: Bitmap? = null): Bitmap? {
        val obj = memoryCache.get<Bitmap>(key)
        return obj ?: cacheDiskUtils.getBitmap(key, defaultValue)
    }

    /**
     * Put drawable in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Drawable?, saveTime: Int = -1) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the drawable in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the drawable if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getDrawable(key: String, defaultValue: Drawable? = null): Drawable? {
        val obj = memoryCache.get<Drawable>(key)
        return obj ?: cacheDiskUtils.getDrawable(key, defaultValue)
    }

    /**
     * Put parcelable in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Parcelable?, saveTime: Int = -1) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the parcelable in cache.
     *
     * @param key          The key of cache.
     * @param creator      The creator.
     * @param defaultValue The default value if the cache doesn't exist.
     * @param <T>          The value type.
     * @return the parcelable if cache exists or defaultValue otherwise
    </T> */
    @JvmOverloads
    fun <T : Parcelable> getParcelable(
        key: String,
        creator: Parcelable.Creator<T>,
        defaultValue: T? = null
    ): T? {
        val value: T? = memoryCache[key]
        return value ?: cacheDiskUtils.getParcelable(
            key,
            creator,
            defaultValue
        )
    }

    /**
     * Put serializable in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Serializable?, saveTime: Int = -1) {
        memoryCache.put(key, value, saveTime)
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the serializable in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun <T : Serializable> getSerializable(key: String, defaultValue: T? = null): T? {
        val obj = memoryCache.get<T>(key)
        return obj ?: cacheDiskUtils.getSerializable(key, defaultValue)
    }

    val cacheDiskSize: Long
        get() = cacheDiskUtils.cacheDiskSize

    val cacheDiskCount: Int
        get() = cacheDiskUtils.cacheDiskCount

    val cacheMemoryCount: Int
        get() = memoryCache.cacheCount

    /**
     * Remove the cache by key.
     *
     * @param key The key of cache.
     */
    fun remove(key: String) {
        memoryCache.remove(key)
        cacheDiskUtils.remove(key)
    }

    /**
     * Clear all of the cache.
     */
    fun clear() {
        memoryCache.clear()
        cacheDiskUtils.clear()
    }

    companion object {
        private val CACHE_MAP: MutableMap<String, CacheDoubleUtils> = HashMap()

        /**
         * Return the single [CacheDoubleUtils] instance.
         *
         * @param memoryCache The instance of [CacheMemoryUtils].
         * @param cacheDiskUtils   The instance of [CacheDiskUtils].
         * @return the single [CacheDoubleUtils] instance
         */
        @JvmStatic
        @JvmOverloads
        fun getInstance(
            memoryCache: CacheMemoryUtils.MemoryCache = CacheMemoryUtils.getInstance(),
            cacheDiskUtils: CacheDiskUtils = CacheDiskUtils.getInstance()
        ): CacheDoubleUtils {
            val cacheKey = cacheDiskUtils.toString() + "_" + memoryCache.toString()
            var cache = CACHE_MAP[cacheKey]
            if (cache == null) {
                synchronized(CacheDoubleUtils::class.java) {
                    cache = CACHE_MAP[cacheKey]
                    if (cache == null) {
                        cache = CacheDoubleUtils(memoryCache, cacheDiskUtils)
                        CACHE_MAP[cacheKey] = cache!!
                    }
                }
            }
            return cache!!
        }
    }
}