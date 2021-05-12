package me.ibore.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.Log
import me.ibore.utils.constant.CacheConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * utils about disk cache
 */
object CacheDiskUtils {

    private const val DEFAULT_MAX_SIZE = Long.MAX_VALUE
    private const val DEFAULT_MAX_COUNT = Int.MAX_VALUE
    private const val CACHE_PREFIX = "cdu_"
    private const val TYPE_BYTE = "by_"
    private const val TYPE_STRING = "st_"
    private const val TYPE_JSON_OBJECT = "jo_"
    private const val TYPE_JSON_ARRAY = "ja_"
    private const val TYPE_BITMAP = "bi_"
    private const val TYPE_DRAWABLE = "dr_"
    private const val TYPE_PARCELABLE = "pa_"
    private const val TYPE_SERIALIZABLE = "se_"

    private val CACHE_MAP: MutableMap<String, DiskCache> = HashMap()

    /**
     * Put bytes in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String, value: ByteArray?, saveTime: Int = -1,
        diskCache: DiskCache = defaultCache
    ) {
        diskCache.put(key, value, saveTime)
    }

    /**
     * Return the bytes in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @return the bytes if cache exists or defaultValue otherwise
     */
    @JvmStatic
    @JvmOverloads
    fun getBytes(
        key: String, defaultValue: ByteArray? = null, diskCache: DiskCache = defaultCache
    ): ByteArray? {
        return diskCache.getBytes(key, defaultValue)
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
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(key: String, value: String?, saveTime: Int = -1, diskCache: DiskCache = defaultCache) {
        diskCache.put(key, value, saveTime)
    }

    /**
     * Return the string value in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @return the string value if cache exists or defaultValue otherwise
     */
    @JvmStatic
    @JvmOverloads
    fun getString(
        key: String, defaultValue: String? = null, diskCache: DiskCache = defaultCache
    ): String? {
        return diskCache.getString(key, defaultValue)
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
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String, value: JSONObject?, saveTime: Int = -1,
        diskCache: DiskCache = defaultCache
    ) {
        diskCache.put(key, value, saveTime)
    }

    /**
     * Return the JSONObject in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    @JvmStatic
    fun getJSONObject(
        key: String, defaultValue: JSONObject? = null,
        diskCache: DiskCache = defaultCache
    ): JSONObject? {
        return diskCache.getJSONObject(key, defaultValue)
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
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String, value: JSONArray?, saveTime: Int = -1,
        diskCache: DiskCache = defaultCache
    ) {
        diskCache.put(key, value, saveTime)
    }

    /**
     * Return the JSONArray in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    @JvmStatic
    @JvmOverloads
    fun getJSONArray(
        key: String, defaultValue: JSONArray? = null,
        diskCache: DiskCache = defaultCache
    ): JSONArray? {
        return diskCache.getJSONArray(key, defaultValue)
    }

    /**
     * Put bitmap in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String, value: Bitmap?, saveTime: Int = -1,
        diskCache: DiskCache = defaultCache
    ) {
        diskCache.put(key, value, saveTime)
    }

    /**
     * Return the bitmap in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmStatic
    @JvmOverloads
    fun getBitmap(
        key: String, defaultValue: Bitmap? = null,
        diskCache: DiskCache = defaultCache
    ): Bitmap? {
        return diskCache.getBitmap(key, defaultValue)
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
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String, value: Drawable?, saveTime: Int = -1,
        diskCache: DiskCache = defaultCache
    ) {
        diskCache.put(key, value, saveTime)
    }

    /**
     * Return the drawable in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @return the drawable if cache exists or defaultValue otherwise
     */
    @JvmStatic
    @JvmOverloads
    fun getDrawable(
        key: String, defaultValue: Drawable? = null,
        diskCache: DiskCache = defaultCache
    ): Drawable? {
        return diskCache.getDrawable(key, defaultValue)
    }

    /**
     * Put parcelable in cache.
     *
     * @param key            The key of cache.
     * @param value          The value of cache.
     * @param saveTime       The save time of cache, in seconds.
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String, value: Parcelable?, saveTime: Int = -1,
        diskCache: DiskCache = defaultCache
    ) {
        diskCache.put(key, value, saveTime)
    }

    /**
     * Return the parcelable in cache.
     *
     * @param key            The key of cache.
     * @param creator        The creator.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @param <T>            The value type.
     * @return the parcelable if cache exists or defaultValue otherwise
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T : Parcelable> getParcelable(
        key: String, creator: Parcelable.Creator<T>, defaultValue: T? = null,
        diskCache: DiskCache = defaultCache
    ): T? {
        return diskCache.getParcelable(key, creator, defaultValue)
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
     * @param diskCache The instance of [DiskCache].
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String, value: Serializable?, saveTime: Int = -1,
        diskCache: DiskCache = defaultCache
    ) {
        diskCache.put(key, value, saveTime)
    }


    /**
     * Return the serializable in cache.
     *
     * @param key            The key of cache.
     * @param defaultValue   The default value if the cache doesn't exist.
     * @param diskCache The instance of [DiskCache].
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmStatic
    @JvmOverloads
    fun <T : Serializable> getSerializable(
        key: String, defaultValue: T? = null,
        diskCache: DiskCache = defaultCache
    ): T? {
        return diskCache.getSerializable(key, defaultValue)
    }

    /**
     * Return the size of cache, in bytes.
     *
     * @param diskCache The instance of [DiskCache].
     * @return the size of cache, in bytes
     */
    @JvmStatic
    @JvmOverloads
    fun getCacheSize(diskCache: DiskCache = defaultCache): Long {
        return diskCache.cacheDiskSize
    }

    /**
     * Return the count of cache.
     *
     * @param diskCache The instance of [DiskCache].
     * @return the count of cache
     */
    @JvmStatic
    @JvmOverloads
    fun getCacheCount(diskCache: DiskCache = defaultCache): Int {
        return diskCache.cacheDiskCount
    }

    /**
     * Remove the cache by key.
     *
     * @param key            The key of cache.
     * @param diskCache The instance of [DiskCache].
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun remove(key: String, diskCache: DiskCache = defaultCache): Boolean {
        return diskCache.remove(key)
    }

    /**
     * Clear all of the cache.
     *
     * @param diskCache The instance of [DiskCache].
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun clear(diskCache: DiskCache = defaultCache): Boolean {
        return diskCache.clear()
    }

    /**
     * Set the default instance of [DiskCache].
     *
     * @param diskCache The default instance of [DiskCache].
     */
    var defaultCache: DiskCache = getInstance()
        @Synchronized get
        @Synchronized set

    /**
     * Return the single [DiskCache] instance.
     *
     * cache directory: /data/data/package/cache/cacheName
     *
     * @param cacheName The name of cache.
     * @param maxSize   The max size of cache, in bytes.
     * @param maxCount  The max count of cache.
     * @return the single [DiskCache] instance
     */
    @JvmStatic
    @JvmOverloads
    fun getInstance(
        cacheName: String = "", maxSize: Long = DEFAULT_MAX_SIZE, maxCount: Int = DEFAULT_MAX_COUNT
    ): DiskCache {
        val cacheNameTemp = if (cacheName.isBlank()) "cacheUtils" else cacheName
        val file = File(Utils.app.cacheDir, cacheNameTemp)
        return getInstance(file, maxSize, maxCount)
    }

    /**
     * Return the single [DiskCache] instance.
     *
     * @param cacheDir The directory of cache.
     * @param maxSize  The max size of cache, in bytes.
     * @param maxCount The max count of cache.
     * @return the single [DiskCache] instance
     */
    @JvmStatic
    @JvmOverloads
    fun getInstance(
        cacheDir: File, maxSize: Long = DEFAULT_MAX_SIZE, maxCount: Int = DEFAULT_MAX_COUNT
    ): DiskCache {
        val cacheKey = cacheDir.absoluteFile.toString() + "_" + maxSize + "_" + maxCount
        var cache = CACHE_MAP[cacheKey]
        if (cache == null) {
            synchronized(DiskCache::class.java) {
                cache = CACHE_MAP[cacheKey]
                if (cache == null) {
                    cache = DiskCache(cacheKey, cacheDir, maxSize, maxCount)
                    CACHE_MAP[cacheKey] = cache!!
                }
            }
        }
        return cache!!
    }


    class DiskCache internal constructor(
        private val cacheKey: String, private val cacheDir: File,
        private val maxSize: Long, private val maxCount: Int
    ) : CacheConstants {
        private var mDiskCacheManager: DiskCacheManager? = null

        private val diskCacheManager: DiskCacheManager?
            get() {
                if (cacheDir.exists()) {
                    if (mDiskCacheManager == null) {
                        mDiskCacheManager = DiskCacheManager(cacheDir, maxSize, maxCount)
                    }
                } else {
                    if (cacheDir.mkdirs()) {
                        mDiskCacheManager = DiskCacheManager(cacheDir, maxSize, maxCount)
                    } else {
                        Log.e("DiskCache", "can't make dirs in " + cacheDir.absolutePath)
                    }
                }
                return mDiskCacheManager
            }

        override fun toString(): String {
            return cacheKey + "@" + Integer.toHexString(hashCode())
        }

        /**
         * Put bytes in cache.
         *
         * @param key      The key of cache.
         * @param value    The value of cache.
         * @param saveTime The save time of cache, in seconds.
         */
        @JvmOverloads
        fun put(key: String, value: ByteArray, saveTime: Int = -1) {
            realPutBytes(TYPE_BYTE + key, value, saveTime)
        }

        private fun realPutBytes(key: String, value: ByteArray?, saveTime: Int) {
            var valueTemp = value ?: return
            val diskCacheManager = diskCacheManager ?: return
            if (saveTime >= 0) valueTemp = DiskCacheHelper.newByteArrayWithTime(saveTime, valueTemp)
            val file = diskCacheManager.getFileBeforePut(key)
            FileIOUtils.writeFileFromBytesByChannel(file, valueTemp, true)
            diskCacheManager.updateModify(file)
            diskCacheManager.put(file)
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
            return realGetBytes(TYPE_BYTE + key, defaultValue)
        }

        private fun realGetBytes(key: String, defaultValue: ByteArray? = null): ByteArray? {
            val diskCacheManager = diskCacheManager ?: return defaultValue
            val file = diskCacheManager.getFileIfExists(key) ?: return defaultValue
            val data = FileIOUtils.readFile2BytesByChannel(file)
            if (DiskCacheHelper.isDue(data)) {
                diskCacheManager.removeByKey(key)
                return defaultValue
            }
            diskCacheManager.updateModify(file)
            return DiskCacheHelper.getDataWithoutDueTime(data)
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
            realPutBytes(TYPE_STRING + key, UtilsBridge.string2Bytes(value), saveTime)
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
            val bytes = realGetBytes(TYPE_STRING + key) ?: return defaultValue
            return UtilsBridge.bytes2String(bytes)
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
            realPutBytes(TYPE_JSON_OBJECT + key, UtilsBridge.jsonObject2Bytes(value), saveTime)
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
            val bytes = realGetBytes(TYPE_JSON_OBJECT + key) ?: return defaultValue
            return UtilsBridge.bytes2JSONObject(bytes)
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
            realPutBytes(TYPE_JSON_ARRAY + key, UtilsBridge.jsonArray2Bytes(value), saveTime)
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
            val bytes = realGetBytes(TYPE_JSON_ARRAY + key) ?: return defaultValue
            return UtilsBridge.bytes2JSONArray(bytes)
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
            realPutBytes(
                TYPE_BITMAP + key,
                if (value == null) null else UtilsBridge.bitmap2Bytes(value),
                saveTime
            )
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
            val bytes = realGetBytes(TYPE_BITMAP + key) ?: return defaultValue
            return UtilsBridge.bytes2Bitmap(bytes)
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
            realPutBytes(
                TYPE_DRAWABLE + key,
                if (value == null) null else UtilsBridge.drawable2Bytes(value),
                saveTime
            )
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
            val bytes = realGetBytes(TYPE_DRAWABLE + key) ?: return defaultValue
            return UtilsBridge.bytes2Drawable(bytes)
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
            realPutBytes(TYPE_PARCELABLE + key, UtilsBridge.parcelable2Bytes(value), saveTime)
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
            val bytes = realGetBytes(TYPE_PARCELABLE + key) ?: return defaultValue
            return UtilsBridge.bytes2Parcelable(bytes, creator)
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
            realPutBytes(TYPE_SERIALIZABLE + key, UtilsBridge.serializable2Bytes(value), saveTime)
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
            val bytes = realGetBytes(TYPE_SERIALIZABLE + key) ?: return defaultValue
            return UtilsBridge.bytes2Object(bytes) as T?
        }

        /**
         * Return the size of cache in disk.
         *
         * @return the size of cache in disk
         */
        val cacheDiskSize: Long
            get() {
                val diskCacheManager = diskCacheManager ?: return 0
                return diskCacheManager.getCacheSize()
            }

        /**
         * Return the count of cache in disk.
         *
         * @return the count of cache in disk
         */
        val cacheDiskCount: Int
            get() {
                val diskCacheManager = diskCacheManager ?: return 0
                return diskCacheManager.getCacheCount()
            }

        /**
         * Remove the cache by key.
         *
         * @param key The key of cache.
         * @return `true`: success<br></br>`false`: fail
         */
        fun remove(key: String): Boolean {
            val diskCacheManager = diskCacheManager ?: return true
            return (diskCacheManager.removeByKey(TYPE_BYTE + key)
                    && diskCacheManager.removeByKey(TYPE_STRING + key)
                    && diskCacheManager.removeByKey(TYPE_JSON_OBJECT + key)
                    && diskCacheManager.removeByKey(TYPE_JSON_ARRAY + key)
                    && diskCacheManager.removeByKey(TYPE_BITMAP + key)
                    && diskCacheManager.removeByKey(TYPE_DRAWABLE + key)
                    && diskCacheManager.removeByKey(TYPE_PARCELABLE + key)
                    && diskCacheManager.removeByKey(TYPE_SERIALIZABLE + key))
        }

        /**
         * Clear all of the cache.
         *
         * @return `true`: success<br></br>`false`: fail
         */
        fun clear(): Boolean {
            val diskCacheManager = diskCacheManager ?: return true
            return diskCacheManager.clear()
        }

    }

    internal class DiskCacheManager constructor(
        private val cacheDir: File,
        private val sizeLimit: Long,
        private val countLimit: Int
    ) {
        private val cacheSize: AtomicLong = AtomicLong()
        private val cacheCount: AtomicInteger = AtomicInteger()
        private val lastUsageDates = Collections.synchronizedMap(HashMap<File, Long>())
        private val mThread: Thread = Thread {
            var size = 0
            var count = 0
            val cachedFiles = cacheDir.listFiles { dir, name -> name.startsWith(CACHE_PREFIX) }
            if (cachedFiles != null) {
                for (cachedFile in cachedFiles) {
                    size += cachedFile.length().toInt()
                    count += 1
                    lastUsageDates[cachedFile] = cachedFile.lastModified()
                }
                cacheSize.getAndAdd(size.toLong())
                cacheCount.getAndAdd(count)
            }
        }

        internal fun getCacheSize(): Long {
            wait2InitOk()
            return cacheSize.get()
        }

        internal fun getCacheCount(): Int {
            wait2InitOk()
            return cacheCount.get()
        }

        internal fun getFileBeforePut(key: String): File {
            wait2InitOk()
            val file = File(cacheDir, getCacheNameByKey(key))
            if (file.exists()) {
                cacheCount.addAndGet(-1)
                cacheSize.addAndGet(-file.length())
            }
            return file
        }

        private fun wait2InitOk() {
            try {
                mThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        internal fun getFileIfExists(key: String): File? {
            val file = File(cacheDir, getCacheNameByKey(key))
            return if (!file.exists()) null else file
        }

        private fun getCacheNameByKey(key: String): String {
            return CACHE_PREFIX + key.substring(0, 3) + key.substring(3).hashCode()
        }

        internal fun put(file: File) {
            cacheCount.addAndGet(1)
            cacheSize.addAndGet(file.length())
            while (cacheCount.get() > countLimit || cacheSize.get() > sizeLimit) {
                cacheSize.addAndGet(-removeOldest())
                cacheCount.addAndGet(-1)
            }
        }

        internal fun updateModify(file: File) {
            val millis = System.currentTimeMillis()
            file.setLastModified(millis)
            lastUsageDates[file] = millis
        }

        internal fun removeByKey(key: String): Boolean {
            val file = getFileIfExists(key) ?: return true
            if (!file.delete()) return false
            cacheSize.addAndGet(-file.length())
            cacheCount.addAndGet(-1)
            lastUsageDates.remove(file)
            return true
        }

        internal fun clear(): Boolean {
            val files = cacheDir.listFiles { dir, name -> name.startsWith(CACHE_PREFIX) }
            if (files == null || files.isEmpty()) return true
            var flag = true
            for (file in files) {
                if (!file.delete()) {
                    flag = false
                    continue
                }
                cacheSize.addAndGet(-file.length())
                cacheCount.addAndGet(-1)
                lastUsageDates.remove(file)
            }
            if (flag) {
                lastUsageDates.clear()
                cacheSize.set(0)
                cacheCount.set(0)
            }
            return flag
        }

        /**
         * Remove the oldest files.
         *
         * @return the size of oldest files, in bytes
         */
        private fun removeOldest(): Long {
            if (lastUsageDates.isEmpty()) return 0
            var oldestUsage = Long.MAX_VALUE
            var oldestFile: File? = null
            val entries: Set<Map.Entry<File, Long>> = lastUsageDates.entries
            synchronized(lastUsageDates) {
                for ((key, lastValueUsage) in entries) {
                    if (lastValueUsage < oldestUsage) {
                        oldestUsage = lastValueUsage
                        oldestFile = key
                    }
                }
            }
            if (oldestFile == null) return 0
            val fileSize = oldestFile!!.length()
            if (oldestFile!!.delete()) {
                lastUsageDates.remove(oldestFile)
                return fileSize
            }
            return 0
        }

        init {
            mThread.start()
        }
    }

    internal object DiskCacheHelper {

        const val TIME_INFO_LEN = 14

        internal fun newByteArrayWithTime(second: Int, data: ByteArray): ByteArray {
            val time = createDueTime(second).toByteArray()
            val content = ByteArray(time.size + data.size)
            System.arraycopy(time, 0, content, 0, time.size)
            System.arraycopy(data, 0, content, time.size, data.size)
            return content
        }

        /**
         * Return the string of due time.
         *
         * @param seconds The seconds.
         * @return the string of due time
         */
        private fun createDueTime(seconds: Int): String {
            return String.format(
                Locale.getDefault(), "_$%010d$" + "_",
                System.currentTimeMillis() / 1000 + seconds
            )
        }

        internal fun isDue(data: ByteArray?): Boolean {
            val millis = getDueTime(data)
            return millis != -1L && System.currentTimeMillis() > millis
        }

        private fun getDueTime(data: ByteArray?): Long {
            if (hasTimeInfo(data)) {
                val millis = String(copyOfRange(data!!, 2, 12))
                return try {
                    millis.toLong() * 1000
                } catch (e: NumberFormatException) {
                    -1
                }
            }
            return -1
        }

        internal fun getDataWithoutDueTime(data: ByteArray?): ByteArray? {
            return if (hasTimeInfo(data)) {
                copyOfRange(data!!, TIME_INFO_LEN, data.size)
            } else data
        }

        private fun copyOfRange(original: ByteArray, from: Int, to: Int): ByteArray {
            val newLength = to - from
            require(newLength >= 0) { "$from > $to" }
            val copy = ByteArray(newLength)
            System.arraycopy(original, from, copy, 0, Math.min(original.size - from, newLength))
            return copy
        }

        private fun hasTimeInfo(data: ByteArray?): Boolean {
            return data != null && data.size >= TIME_INFO_LEN && data[0] == '_'.toByte()
                    && data[1] == '$'.toByte() && data[12] == '$'.toByte() && data[13] == '_'.toByte()
        }
    }
}