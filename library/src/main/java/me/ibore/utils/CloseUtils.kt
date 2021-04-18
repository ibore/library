package me.ibore.utils

import java.io.Closeable
import java.io.IOException

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/10/09
 * desc  : utils about close
</pre> *
 */
object CloseUtils {
    /**
     * Close the io stream.
     *
     * @param closeables The closeables.
     */
    @JvmStatic
    fun closeIO(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Close the io stream quietly.
     *
     * @param closeables The closeables.
     */
    @JvmStatic
    fun closeIOQuietly(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }
}