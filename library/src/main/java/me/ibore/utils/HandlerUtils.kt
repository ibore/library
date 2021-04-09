package me.ibore.utils

import android.os.Handler
import android.os.Looper
import java.util.*

/**
 * detail: Handler 工具类
 * @author Ttt
 */
object HandlerUtils {

    // 日志 TAG
    @JvmStatic
    private val TAG = HandlerUtils::class.java.simpleName

    // 主线程 Handler
    @JvmStatic
    private var sMainHandler: Handler? = null

    /**
     * 获取主线程 Handler
     * @return 主线程 Handler
     */
    @JvmStatic
    val mainHandler: Handler
        get() {
            if (sMainHandler == null) {
                sMainHandler = Handler(Looper.getMainLooper())
            }
            return sMainHandler!!
        }

    /**
     * 当前线程是否主线程
     * @return `true` yes, `false` no
     */
    @JvmStatic
    val isMainThread: Boolean
        get() = Looper.getMainLooper().thread === Thread.currentThread()

    /**
     * 在主线程 Handler 中执行延迟任务
     * @param runnable    可执行的任务
     * @param delayMillis 延迟时间
     */
    @JvmStatic
    @JvmOverloads
    fun postRunnable(runnable: Runnable?, delayMillis: Long = 0) {
        if (runnable != null) {
            mainHandler.postDelayed(runnable, delayMillis)
        }
    }

    /**
     * 在主线程 Handler 中执行延迟任务
     * @param runnable      可执行的任务
     * @param delayMillis   延迟时间
     * @param number        轮询次数
     * @param interval      轮询时间
     * @param onEndListener 结束通知
     */
    @JvmStatic
    @JvmOverloads
    fun postRunnable(
        runnable: Runnable?, delayMillis: Long, number: Int,
        interval: Long, onEndListener: OnEndListener? = null
    ) {
        if (runnable != null) {
            val loop: Runnable = object : Runnable {
                private var mNumber = 0
                override fun run() {
                    if (mNumber < number) {
                        mNumber++
                        try {
                            runnable.run()
                        } catch (e: Exception) {
                        }
                        // 判断是否超过次数
                        if (mNumber < number) {
                            mainHandler.postDelayed(this, interval)
                        }
                    }

                    // 判断是否超过次数
                    if (mNumber >= number) {
                        onEndListener?.onEnd(delayMillis, number, interval)
                    }
                }
            }
            mainHandler.postDelayed(loop, delayMillis)
        }
    }

    /**
     * 在主线程 Handler 中清除任务
     * <pre>
     * 也可使用 [Handler.removeCallbacksAndMessages] 实现
     * 注意: 会将所有的 Callbacks、Messages 全部清除掉
    </pre> *
     * @param runnable 需要清除的任务
     */
    @JvmStatic
    fun removeRunnable(runnable: Runnable?) {
        if (runnable != null) {
            mainHandler.removeCallbacks(runnable)
        }
    }

    // ================
    // = Runnable Map =
    // ================
    // 通过 Key 快捷控制 Runnable, 进行 postDelayed、removeCallbacks
    @JvmStatic
    private val sRunnableMaps: MutableMap<String, Runnable> = HashMap()

    /**
     * 获取 Key Runnable Map
     * @return Key Runnable Map
     */
    @JvmStatic
    val runnableMaps: Map<String, Runnable>
        get() = HashMap(sRunnableMaps)

    /**
     * 清空 Key Runnable Map
     */
    @JvmStatic
    fun clearRunnableMaps() {
        sRunnableMaps.clear()
    }

    /**
     * 判断 Map 是否存储 key Runnable
     * @param key key
     * @return `true` yes, `false` no
     */
    @JvmStatic
    fun containsKey(key: String): Boolean {
        return sRunnableMaps.containsKey(key)
    }

    /**
     * 通过 Key 存储 Runnable
     * @param key      key
     * @param runnable 线程任务
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun put(key: String?, runnable: Runnable?): Boolean {
        if (key != null && runnable != null) {
            try {
                sRunnableMaps[key] = runnable
                return true
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "put")
            }
        }
        return false
    }

    /**
     * 通过 Key 移除 Runnable
     * @param key key
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun remove(key: String?): Boolean {
        if (key != null) {
            try {
                sRunnableMaps.remove(key)
                return true
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "remove")
            }
        }
        return false
    }

    /**
     * 执行对应 Key Runnable
     * @param key         key
     * @param delayMillis 延迟时间
     */
    @JvmStatic
    fun postRunnable(key: String, delayMillis: Long) {
        val runnable = sRunnableMaps[key]
        if (runnable != null) {
            removeRunnable(runnable)
            postRunnable(runnable, delayMillis)
        }
    }

    /**
     * 清除对应 Key Runnable
     * @param key key
     */
    @JvmStatic
    fun removeRunnable(key: String) {
        val runnable = sRunnableMaps[key]
        if (runnable != null) removeRunnable(runnable)
    }
    // =
    /**
     * detail: 结束回调事件
     * @author Ttt
     */
    interface OnEndListener {
        /**
         * 结束通知
         * @param delayMillis 延迟时间
         * @param number      轮询次数
         * @param interval    轮询时间
         */
        fun onEnd(delayMillis: Long, number: Int, interval: Long)
    }
}