package me.ibore.utils

import android.util.LruCache
import me.ibore.utils.constant.CacheConstants
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2017/05/24
 * desc  : utils about memory cache
</pre> *
 */
object CacheMemoryUtils {

    private const val DEFAULT_MAX_COUNT = 256
    private val MAP_CACHE: MutableMap<String, MemoryCache> = HashMap()

    /**
     * Return the single [CacheMemoryUtils] instance.
     *
     * @param cacheKey The key of cache.
     * @param maxCount The max count of cache.
     * @return the single [CacheMemoryUtils] instance
     */
    @JvmStatic
    @JvmOverloads
    fun getInstance(
        maxCount: Int = DEFAULT_MAX_COUNT, cacheKey: String = maxCount.toString()
    ): MemoryCache {
        var cache = MAP_CACHE[cacheKey]
        if (cache == null) {
            synchronized(CacheMemoryUtils::class.java) {
                cache = MAP_CACHE[cacheKey]
                if (cache == null) {
                    cache = MemoryCache(cacheKey, LruCache(maxCount))
                    MAP_CACHE[cacheKey] = cache!!
                }
            }
        }
        return cache!!
    }

    /**
     * Put bytes in cache.
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param memoryCache The instance of [CacheMemoryUtils].
     */
    @JvmStatic
    @JvmOverloads
    fun put(key: String, value: Any?, saveTime: Int = -1, memoryCache: MemoryCache = defaultCache) {
        memoryCache.put(key, value, saveTime)
    }

    /**
     * Return the value in cache.
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param memoryCache The instance of [CacheMemoryUtils].
     * @param <T>              The value type.
     * @return the value if cache exists or defaultValue otherwise
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> get(key: String, defaultValue: T? = null, memoryCache: MemoryCache = defaultCache): T? {
        return memoryCache[key, defaultValue]
    }

    /**
     * Return the count of cache.
     *
     * @param memoryCache The instance of [CacheMemoryUtils].
     * @return the count of cache
     */
    @JvmStatic
    @JvmOverloads
    fun getCacheCount(memoryCache: MemoryCache = defaultCache): Int {
        return memoryCache.cacheCount
    }

    /**
     * Remove the cache by key.
     *
     * @param key              The key of cache.
     * @param memoryCache The instance of [CacheMemoryUtils].
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun remove(key: String, memoryCache: MemoryCache = defaultCache): Any? {
        return memoryCache.remove(key)
    }

    /**
     * Clear all of the cache.
     *
     * @param memoryCache The instance of [CacheMemoryUtils].
     */
    @JvmStatic
    @JvmOverloads
    fun clear(memoryCache: MemoryCache = defaultCache) {
        memoryCache.clear()
    }

    /**
     * Set the default instance of [CacheMemoryUtils].
     *
     * @param CacheMemoryUtils The default instance of [CacheMemoryUtils].
     */
    @JvmStatic
    private var defaultCache: MemoryCache = getInstance()
        @Synchronized get
        @Synchronized set

    class MemoryCache internal constructor(
        private val mCacheKey: String, private val mMemoryCache: LruCache<String, CacheValue>
    ) : CacheConstants {
        /**
         * Put bytes in cache.
         *
         * @param key      The key of cache.
         * @param value    The value of cache.
         * @param saveTime The save time of cache, in seconds.
         */
        @JvmOverloads
        fun put(key: String, value: Any?, saveTime: Int = -1) {
            if (value == null) return
            val dueTime = if (saveTime < 0) -1 else System.currentTimeMillis() + saveTime * 1000
            mMemoryCache.put(key, CacheValue(dueTime, value))
        }

        /**
         * Return the value in cache.
         *
         * @param key          The key of cache.
         * @param defaultValue The default value if the cache doesn't exist.
         * @param <T>          The value type.
         * @return the value if cache exists or defaultValue otherwise
        </T> */
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

        /**
         * Return the count of cache in memory.
         *
         * @return the count of cache in memory.
         */
        /**
         * Return the count of cache.
         *
         * @return the count of cache
         */
        val cacheCount: Int
            get() = mMemoryCache.size()

        /**
         * Remove the cache by key.
         *
         * @param key The key of cache.
         * @return `true`: success<br></br>`false`: fail
         */
        fun remove(key: String): Any? {
            val remove = mMemoryCache.remove(key) ?: return null
            return remove.value
        }

        /**
         * Clear all of the cache.
         */
        fun clear() {
            mMemoryCache.evictAll()
        }

        override fun toString(): String {
            return mCacheKey + "@" + Integer.toHexString(hashCode())
        }

    }

    class CacheValue(var dueTime: Long, var value: Any)

}