package me.ibore.utils

import java.lang.reflect.Type

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/01/30
 * desc  : utils about clone
</pre> *
 */
object CloneUtils {
    /**
     * Deep clone.
     *
     * @param data The data.
     * @param type The type.
     * @param <T>  The value type.
     * @return The object of cloned.
    </T> */
    fun <T> deepClone(data: T, type: Type): T {
        return GsonUtils.fromJson(GsonUtils.toJson(data), type)
    }
}