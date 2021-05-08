package me.ibore.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.LruCache
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.Serializable
import java.util.*

class CacheUtils {

    class MemoryCache private constructor(
        private val mCacheKey: String,
        private val mMemoryCache: LruCache<String, CacheValue>
    ) {
        companion object {
            private const val DEFAULT_MAX_COUNT = 256
            private val CACHE_MAP: MutableMap<String, MemoryCache> = HashMap()

            @JvmStatic
            @JvmOverloads
            fun getInstance(
                maxCount: Int = DEFAULT_MAX_COUNT, cacheKey: String = maxCount.toString()
            ): MemoryCache {
                var cache = CACHE_MAP[cacheKey]
                if (cache == null) {
                    synchronized(MemoryCache::class.java) {
                        cache = CACHE_MAP[cacheKey]
                        if (cache == null) {
                            cache = MemoryCache(cacheKey, LruCache(maxCount))
                            CACHE_MAP[cacheKey] = cache!!
                        }
                    }
                }
                return cache!!
            }
        }

        @JvmOverloads
        fun put(key: String, value: Any?, saveTime: Int = -1) {
            if (value == null) return
            val dueTime = if (saveTime < 0) -1 else System.currentTimeMillis() + saveTime * 1000
            mMemoryCache.put(key, CacheValue(dueTime, value))
        }

        @Suppress("UNCHECKED_CAST")
        @JvmOverloads
        operator fun <T> get(key: String, defaultValue: T? = null): T? {
            val cacheValue = mMemoryCache[key] ?: return defaultValue
            if (cacheValue.dueTime == -1L || cacheValue.dueTime >= System.currentTimeMillis()) {
                return cacheValue.value as T?
            }
            mMemoryCache.remove(key)
            return defaultValue
        }

        fun getCacheCount(): Int = mMemoryCache.size()

        fun remove(key: String): Any? {
            val remove = mMemoryCache.remove(key) ?: return null
            return remove.value
        }

        fun clear() {
            mMemoryCache.evictAll()
        }

        private class CacheValue(var dueTime: Long, var value: Any)

    }

    class DiskCache private constructor(
        private val cacheKey: String, private val cacheDir: File,
        private val maxSize: Long, private val maxCount: Int
    ) {

        fun put(key: String, value: ByteArray?, saveTime: Int = -1) {

        }

        fun getBytes(key: String, defaultValue: ByteArray? = null): ByteArray? {
            return defaultValue
        }

        fun put(key: String, value: String?, saveTime: Int = -1) {

        }

        fun getString(key: String, defaultValue: String? = null): String? {
            return defaultValue
        }

        fun put(key: String, value: JSONObject?, saveTime: Int = -1) {

        }

        fun getJSONObject(key: String, defaultValue: JSONObject? = null): JSONObject? {
            return defaultValue
        }

        fun put(key: String, value: JSONArray?, saveTime: Int = -1) {

        }

        fun getJSONArray(key: String, defaultValue: JSONArray? = null): JSONArray? {
            return defaultValue
        }

        fun put(key: String, value: Bitmap?, saveTime: Int = -1) {

        }

        fun getBitmap(key: String, defaultValue: Bitmap? = null): Bitmap? {
            return defaultValue
        }

        fun put(key: String, value: Drawable?, saveTime: Int = -1) {

        }

        fun getDrawable(key: String, defaultValue: Drawable? = null): Drawable? {
            return defaultValue
        }

        fun put(key: String, value: Parcelable?, saveTime: Int = -1) {

        }

        fun <T : Parcelable> getParcelable(
            key: String, creator: Parcelable.Creator<T>, defaultValue: T? = null
        ): T? {
            return defaultValue
        }

        fun put(key: String, value: Serializable?, saveTime: Int = -1) {

        }

        fun <T : Serializable> getSerializable(key: String, defaultValue: T? = null): T? {
            return defaultValue
        }

    }

}