package me.ibore.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/01/04
 * desc  : utils about double cache
</pre> *
 */
object CacheDoubleStaticUtils {

    private var sDefaultCacheDoubleUtils: CacheDoubleUtils? = null

    /**
     * Return the size of cache in disk.
     *
     * @return the size of cache in disk
     */
    val cacheDiskSize: Long
        get() = getCacheDiskSize(defaultCacheDoubleUtils)

    /**
     * Return the count of cache in disk.
     *
     * @return the count of cache in disk
     */
    val cacheDiskCount: Int
        get() = getCacheDiskCount(defaultCacheDoubleUtils)

    /**
     * Return the count of cache in memory.
     *
     * @return the count of cache in memory.
     */
    val cacheMemoryCount: Int
        get() = getCacheMemoryCount(defaultCacheDoubleUtils)
    ///////////////////////////////////////////////////////////////////////////
    // dividing line
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Put bytes in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: ByteArray?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the bytes in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the bytes if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getBytes(
        key: String,
        defaultValue: ByteArray? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): ByteArray? {
        return cacheDoubleUtils.getBytes(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put string value in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: String?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the string value in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the string value if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getString(
        key: String,
        defaultValue: String? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): String? {
        return cacheDoubleUtils.getString(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Put JSONObject in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONObject?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONObject in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getJSONObject(
        key: String,
        defaultValue: JSONObject? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): JSONObject? {
        return cacheDoubleUtils.getJSONObject(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put JSONArray in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONArray?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONArray in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getJSONArray(
        key: String,
        defaultValue: JSONArray? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): JSONArray? {
        return cacheDoubleUtils.getJSONArray(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put bitmap in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Bitmap?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the bitmap in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmStatic
    @JvmOverloads
    fun getBitmap(
        key: String,
        defaultValue: Bitmap? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): Bitmap? {
        return cacheDoubleUtils.getBitmap(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put drawable in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Drawable?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the drawable in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the drawable if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getDrawable(
        key: String,
        defaultValue: Drawable? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): Drawable? {
        return cacheDoubleUtils.getDrawable(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put parcelable in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Parcelable?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the parcelable in cache.
     *
     * @param key              The key of cache.
     * @param creator          The creator.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @param <T>              The value type.
     * @return the parcelable if cache exists or defaultValue otherwise
    </T> */
    @JvmOverloads
    fun <T : Parcelable> getParcelable(
        key: String,
        creator: Parcelable.Creator<T>,
        defaultValue: T? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): T? {
        return cacheDoubleUtils.getParcelable(key, creator, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put serializable in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Serializable?,
        saveTime: Int = -1,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the serializable in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun <T : Serializable> getSerializable(
        key: String,
        defaultValue: T? = null,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils
    ): T? {
        return cacheDoubleUtils.getSerializable(key, defaultValue)
    }

    /**
     * Return the size of cache in disk.
     *
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the size of cache in disk
     */
    fun getCacheDiskSize(cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils): Long {
        return cacheDoubleUtils.cacheDiskSize
    }

    /**
     * Return the count of cache in disk.
     *
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the count of cache in disk
     */
    fun getCacheDiskCount(cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils): Int {
        return cacheDoubleUtils.cacheDiskCount
    }

    /**
     * Return the count of cache in memory.
     *
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     * @return the count of cache in memory.
     */
    fun getCacheMemoryCount(cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils): Int {
        return cacheDoubleUtils.cacheMemoryCount
    }

    /**
     * Remove the cache by key.
     *
     * @param key              The key of cache.
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun remove(key: String, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils) {
        cacheDoubleUtils.remove(key)
    }

    /**
     * Clear all of the cache.
     *
     * @param cacheDoubleUtils The instance of [CacheDoubleUtils].
     */
    @JvmOverloads
    fun clear(cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils) {
        cacheDoubleUtils.clear()
    }

    /**
     * Set the default instance of [CacheDoubleUtils].
     *
     * @param cacheDoubleUtils The default instance of [CacheDoubleUtils].
     */
    private var defaultCacheDoubleUtils: CacheDoubleUtils
        get() = if (sDefaultCacheDoubleUtils != null) sDefaultCacheDoubleUtils!! else CacheDoubleUtils.getInstance()
        set(cacheDoubleUtils) {
            sDefaultCacheDoubleUtils = cacheDoubleUtils
        }
}