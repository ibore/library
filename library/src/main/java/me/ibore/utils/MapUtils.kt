package me.ibore.utils

import android.util.Pair
import java.util.*

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2019/08/12
 * desc  : utils about map
</pre> *
 */
object MapUtils {
    /**
     * Returns a new read-only map with the specified contents, given as a list of pairs
     * where the first value is the key and the second is the value.
     *
     * @param pairs a list of pairs
     * @return a new read-only map with the specified contents
     */
    @SafeVarargs
    fun <K, V> newUnmodifiableMap(vararg pairs: Pair<K, V>?): Map<K, V> {
        return Collections.unmodifiableMap(newHashMap(*pairs))
    }

    @SafeVarargs
    fun <K, V> newHashMap(vararg pairs: Pair<K, V>?): HashMap<K, V> {
        val map = HashMap<K, V>()
        if (pairs == null || pairs.size == 0) {
            return map
        }
        for (pair in pairs) {
            if (pair == null) continue
            map[pair.first] = pair.second
        }
        return map
    }

    @SafeVarargs
    fun <K, V> newLinkedHashMap(vararg pairs: Pair<K, V>?): LinkedHashMap<K, V> {
        val map = LinkedHashMap<K, V>()
        if (pairs == null || pairs.size == 0) {
            return map
        }
        for (pair in pairs) {
            if (pair == null) continue
            map[pair.first] = pair.second
        }
        return map
    }

    @SafeVarargs
    fun <K, V> newTreeMap(
        comparator: Comparator<K>?,
        vararg pairs: Pair<K, V>?
    ): TreeMap<K, V> {
        requireNotNull(comparator) { "comparator must not be null" }
        val map = TreeMap<K, V>(comparator)
        if (pairs.isEmpty()) {
            return map
        }
        for (pair in pairs) {
            if (pair == null) continue
            map[pair.first] = pair.second
        }
        return map
    }

    @SafeVarargs
    fun <K, V> newHashTable(vararg pairs: Pair<K, V>?): Hashtable<K, V> {
        val map = Hashtable<K, V>()
        if (pairs.isEmpty()) {
            return map
        }
        for (pair in pairs) {
            if (pair == null) continue
            map[pair.first] = pair.second
        }
        return map
    }

    /**
     * Null-safe check if the specified map is empty.
     *
     *
     * Null returns true.
     *
     * @param map the map to check, may be null
     * @return true if empty or null
     */
    fun isEmpty(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }

    /**
     * Null-safe check if the specified map is not empty.
     *
     *
     * Null returns false.
     *
     * @param map the map to check, may be null
     * @return true if non-null and non-empty
     */
    fun isNotEmpty(map: Map<*, *>?): Boolean {
        return !isEmpty(map)
    }

    /**
     * Gets the size of the map specified.
     *
     * @param map The map.
     * @return the size of the map specified
     */
    fun size(map: Map<*, *>?): Int {
        return map?.size ?: 0
    }

    /**
     * Executes the given closure on each element in the collection.
     *
     *
     * If the input collection or closure is null, there is no change made.
     *
     * @param map     the map to get the input from, may be null
     * @param closure the closure to perform, may be null
     */
    fun <K, V> forAllDo(map: Map<K, V>?, closure: Closure<K, V>?) {
        if (map == null || closure == null) return
        for ((key, value) in map) {
            closure.execute(key, value)
        }
    }

    /**
     * Transform the map by applying a Transformer to each element.
     *
     *
     * If the input map or transformer is null, there is no change made.
     *
     * @param map         the map to get the input from, may be null
     * @param transformer the transformer to perform, may be null
     */
    @Suppress("UNCHECKED_CAST")
    fun <K1, V1, K2, V2> transform(
        map: Map<K1, V1>?,
        transformer: Transformer<K1, V1, K2, V2>?
    ): Map<K2, V2>? {
        if (map == null || transformer == null) return null
        try {
            val transMap: MutableMap<K2, V2> = map.javaClass.newInstance() as MutableMap<K2, V2>
            forAllDo(map, object : Closure<K1, V1> {
                override fun execute(key: K1, value: V1) {
                    val pair = transformer.transform(key, value)
                    transMap[pair.first] = pair.second
                }
            })
            return transMap
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Return the string of map.
     *
     * @param map The map.
     * @return the string of map
     */
    fun toString(map: Map<*, *>?): String {
        return map?.toString() ?: "null"
    }

    interface Closure<K, V> {
        fun execute(key: K, value: V)
    }

    interface Transformer<K1, V1, K2, V2> {
        fun transform(k1: K1, v1: V1): Pair<K2, V2>
    }
}
