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
 * desc  : utils about disk cache
</pre> *
 */
object CacheDiskStaticUtils {
    private var sDefaultCacheDiskUtils: CacheDiskUtils? = null

    /**
     * Return the size of cache, in bytes.
     *
     * @return the size of cache, in bytes
     */
    val cacheSize: Long
        get() = getCacheSize(defaultCacheDiskUtils)

    /**
     * Return the count of cache.
     *
     * @return the count of cache
     */
    val cacheCount: Int
        get() = getCacheCount(defaultCacheDiskUtils)
    ///////////////////////////////////////////////////////////////////////////
    // dividing line
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Put bytes in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: ByteArray?,
        saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the bytes in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the bytes if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getBytes(
        key: String,
        defaultValue: ByteArray? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): ByteArray? {
        return cacheDiskUtils.getBytes(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Put string value in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: String?,
        saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the string value in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the string value if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getString(
        key: String,
        defaultValue: String? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): String? {
        return cacheDiskUtils.getString(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put JSONObject in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONObject?,
        saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONObject in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    fun getJSONObject(
        key: String,
        defaultValue: JSONObject? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): JSONObject? {
        return cacheDiskUtils.getJSONObject(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put JSONArray in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONArray?,
        saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONArray in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getJSONArray(
        key: String,
        defaultValue: JSONArray? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): JSONArray? {
        return cacheDiskUtils.getJSONArray(key, defaultValue)
    }

    /**
     * Put bitmap in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String, value: Bitmap?, saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the bitmap in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    fun getBitmap(
        key: String,
        defaultValue: Bitmap? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): Bitmap? {
        return cacheDiskUtils.getBitmap(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Put drawable in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Drawable?,
        saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the drawable in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the drawable if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getDrawable(
        key: String,
        defaultValue: Drawable? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): Drawable? {
        return cacheDiskUtils.getDrawable(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Put parcelable in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Parcelable?,
        saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the parcelable in cache.
     *
     * @param key            The key of cache.
     * @param creator        The creator.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @param <T>            The value type.
     * @return the parcelable if cache exists or defaultValue otherwise
    </T> */
    @JvmOverloads
    fun <T : Parcelable> getParcelable(
        key: String,
        creator: Parcelable.Creator<T>,
        defaultValue: T? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): T? {
        return cacheDiskUtils.getParcelable(key, creator, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put serializable in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Serializable?,
        saveTime: Int = -1,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }


    /**
     * Return the serializable in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun <T : Serializable> getSerializable(
        key: String,
        defaultValue: T? = null,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils
    ): T? {
        return cacheDiskUtils.getSerializable(key, defaultValue)
    }

    /**
     * Return the size of cache, in bytes.
     *
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the size of cache, in bytes
     */
    fun getCacheSize(cacheDiskUtils: CacheDiskUtils): Long {
        return cacheDiskUtils.cacheDiskSize
    }

    /**
     * Return the count of cache.
     *
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return the count of cache
     */
    fun getCacheCount(cacheDiskUtils: CacheDiskUtils): Int {
        return cacheDiskUtils.cacheDiskCount
    }

    /**
     * Remove the cache by key.
     *
     * @param key            The key of cache.
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun remove(key: String, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils): Boolean {
        return cacheDiskUtils.remove(key)
    }

    /**
     * Clear all of the cache.
     *
     * @param cacheDiskUtils The instance of [CacheDiskUtils].
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun clear(cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils): Boolean {
        return cacheDiskUtils.clear()
    }

    /**
     * Set the default instance of [CacheDiskUtils].
     *
     * @param cacheDiskUtils The default instance of [CacheDiskUtils].
     */
    private var defaultCacheDiskUtils: CacheDiskUtils
        get() = if (sDefaultCacheDiskUtils != null) sDefaultCacheDiskUtils!! else CacheDiskUtils.getInstance()
        set(cacheDiskUtils) {
            sDefaultCacheDiskUtils = cacheDiskUtils
        }
}