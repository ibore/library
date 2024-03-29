package me.ibore.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.IntRange
import me.ibore.ktx.logD
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/05/08
 * desc  : utils about thread
</pre> *
 */
object ThreadUtils {

    @JvmStatic
    private val mainHandler = Handler(Looper.getMainLooper())

    private val TYPE_PRIORITY_POOLS: MutableMap<Int, MutableMap<Int, ExecutorService?>> = HashMap()
    private val TASK_POOL_MAP: MutableMap<Task<*>, ExecutorService?> = ConcurrentHashMap()
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val TIMER = Timer()
    private const val TYPE_SINGLE: Byte = -1
    private const val TYPE_CACHED: Byte = -2
    private const val TYPE_IO: Byte = -4
    private const val TYPE_CPU: Byte = -8
    private var sDeliver: Executor? = null

    /**
     * Return whether the thread is the main thread.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    fun runOnUiThread(runnable: Runnable) {
        if (isMainThread) {
            runnable.run()
        } else {
            mainHandler.post(runnable)
        }
    }

    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        mainHandler.postDelayed(runnable, delayMillis)
    }

    /**
     * Return a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.
     *
     * @param size     The size of thread in the pool.
     * @param priority The priority of thread in the poll.
     * @return a fixed thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getFixedPool(
        @IntRange(from = 1) size: Int,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService {
        return getPoolByTypeAndPriority(size, priority)
    }

    /**
     * Return a thread pool that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed.
     *
     * @param priority The priority of thread in the poll.
     * @return a single thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getSinglePool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority)
    }

    /**
     * Return a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.
     *
     * @param priority The priority of thread in the poll.
     * @return a cached thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getCachedPool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority)
    }

    /**
     * Return a thread pool that creates (2 * CPU_COUNT + 1) threads
     * operating off a queue which size is 128.
     *
     * @param priority The priority of thread in the poll.
     * @return a IO thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getIoPool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_IO.toInt(), priority)
    }

    /**
     * Return a thread pool that creates (CPU_COUNT + 1) threads
     * operating off a queue which size is 128 and the maximum
     * number of threads equals (2 * CPU_COUNT + 1).
     *
     * @param priority The priority of thread in the poll.
     * @return a cpu thread pool for
     */
    @JvmStatic
    @JvmOverloads
    fun getCpuPool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority)
    }

    /**
     * Executes the given task in a fixed thread pool.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixed(
        @IntRange(from = 1) size: Int, task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPoolByTypeAndPriority(size, priority), task)
    }

    /**
     * Executes the given task in a fixed thread pool after the given delay.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixedWithDelay(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPoolByTypeAndPriority(size, priority), task, delay, unit)
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(size, priority), task, 0, period, unit)
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size         The size of thread in the fixed thread pool.
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(size, priority),
            task,
            initialDelay,
            period,
            unit
        )
    }

    /**
     * Executes the given task in a single thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingle(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority), task)
    }

    /**
     * Executes the given task in a single thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingleWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority), task, delay, unit)
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority),
            task,
            0,
            period,
            unit
        )
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority),
            task,
            initialDelay,
            period,
            unit
        )
    }

    /**
     * Executes the given task in a cached thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCached(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority), task)
    }

    /**
     * Executes the given task in a cached thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCachedWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority), task, delay, unit)
    }

    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority),
            task,
            0,
            period,
            unit
        )
    }

    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority),
            task,
            initialDelay,
            period,
            unit
        )
    }

    /**
     * Executes the given task in an IO thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIo(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPoolByTypeAndPriority(TYPE_IO.toInt(), priority), task)
    }

    /**
     * Executes the given task in an IO thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIoWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_IO.toInt(), priority), task, delay, unit)
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_IO.toInt(), priority),
            task,
            0,
            period,
            unit
        )
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_IO.toInt(), priority), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpu(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority), task)
    }

    /**
     * Executes the given task in a cpu thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpuWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority), task, delay, unit)
    }

    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority),
            task,
            0,
            period,
            unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a custom thread pool.
     *
     * @param pool The custom thread pool.
     * @param task The task to execute.
     * @param <T>  The type of the task's result.
    </T> */
    fun <T> executeByCustom(pool: ExecutorService?, task: Task<T>) {
        execute(pool, task)
    }

    /**
     * Executes the given task in a custom thread pool after the given delay.
     *
     * @param pool  The custom thread pool.
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param <T>   The type of the task's result.
    </T> */
    fun <T> executeByCustomWithDelay(
        pool: ExecutorService?,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(pool, task, delay, unit)
    }

    /**
     * Executes the given task in a custom thread pool at fix rate.
     *
     * @param pool   The custom thread pool.
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param <T>    The type of the task's result.
    </T> */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService?,
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, 0, period, unit)
    }

    /**
     * Executes the given task in a custom thread pool at fix rate.
     *
     * @param pool         The custom thread pool.
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService?,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, initialDelay, period, unit)
    }

    /**
     * Cancel the given task.
     *
     * @param task The task to cancel.
     */
    fun cancel(task: Task<*>?) {
        if (task == null) return
        task.cancel()
    }

    /**
     * Cancel the given tasks.
     *
     * @param tasks The tasks to cancel.
     */
    fun cancel(vararg tasks: Task<*>?) {
        if (tasks.isEmpty()) return
        for (task in tasks) {
            if (task == null) continue
            task.cancel()
        }
    }

    /**
     * Cancel the given tasks.
     *
     * @param tasks The tasks to cancel.
     */
    fun cancel(tasks: List<Task<*>?>?) {
        if (tasks == null || tasks.isEmpty()) return
        for (task in tasks) {
            if (task == null) continue
            task.cancel()
        }
    }

    /**
     * Cancel the tasks in pool.
     *
     * @param executorService The pool.
     */
    fun cancel(executorService: ExecutorService) {
        if (executorService is ThreadPoolExecutor4Util) {
            for ((key, value) in TASK_POOL_MAP) {
                if (value === executorService) {
                    cancel(key)
                }
            }
        } else {
            Log.e("ThreadUtils", "The executorService is not ThreadUtils's pool.")
        }
    }

    fun setDeliver(deliver: Executor?) {
        sDeliver = deliver
    }

    @JvmStatic
    fun <T> doAsync(task: ConsumerTask<T>): ConsumerTask<T> {
        getCachedPool().execute(task)
        return task
    }

    private fun <T> executeWithDelay(
        pool: ExecutorService?, task: Task<T>, delay: Long, unit: TimeUnit) {
        execute(pool, task, delay, 0, unit)
    }

    private fun <T> executeAtFixedRate(
        pool: ExecutorService?, task: Task<T>, delay: Long, period: Long, unit: TimeUnit
    ) {
        execute(pool, task, delay, period, unit)
    }

    @JvmStatic
    private fun getPoolByTypeAndPriority(
        type: Int, priority: Int = Thread.NORM_PRIORITY): ExecutorService {
        synchronized(TYPE_PRIORITY_POOLS) {
            var pool: ExecutorService?
            var priorityPools = TYPE_PRIORITY_POOLS[type]
            if (priorityPools == null) {
                priorityPools = ConcurrentHashMap()
                pool = ThreadPoolExecutor4Util.createPool(type, priority)
                priorityPools[priority] = pool
                TYPE_PRIORITY_POOLS[type] = priorityPools
            } else {
                pool = priorityPools[priority]
                if (pool == null) {
                    pool = ThreadPoolExecutor4Util.createPool(type, priority)
                    priorityPools[priority] = pool
                }
            }
            return pool
        }
    }


    @JvmStatic
    private fun <T> execute(
        pool: ExecutorService?, task: Task<T>,
        delay: Long = 0, period: Long = 0, unit: TimeUnit? = null
    ) {
        synchronized(TASK_POOL_MAP) {
            if (TASK_POOL_MAP[task] != null) {
                Log.e("ThreadUtils", "Task can only be executed once.")
                return
            }
            TASK_POOL_MAP.put(task, pool)
        }
        if (period == 0L) {
            if (delay == 0L) {
                pool!!.execute(task)
            } else {
                val timerTask: TimerTask = object : TimerTask() {
                    override fun run() {
                        pool!!.execute(task)
                    }
                }
                TIMER.schedule(timerTask, unit!!.toMillis(delay))
            }
        } else {
            task.setSchedule(true)
            val timerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    pool!!.execute(task)
                }
            }
            TIMER.scheduleAtFixedRate(timerTask, unit!!.toMillis(delay), unit.toMillis(period))
        }
    }

    private val globalDeliver: Executor
        get() {
            if (sDeliver == null) {
                sDeliver = Executor { command -> runOnUiThread(command) }
            }
            return sDeliver!!
        }

    internal class ThreadPoolExecutor4Util(
        corePoolSize: Int, maximumPoolSize: Int,
        keepAliveTime: Long, unit: TimeUnit?,
        private val workQueue: LinkedBlockingQueue4Util,
        threadFactory: ThreadFactory?
    ) : ThreadPoolExecutor(
        corePoolSize, maximumPoolSize,
        keepAliveTime, unit,
        workQueue,
        threadFactory
    ) {
        private val submittedCount = AtomicInteger()

        override fun afterExecute(r: Runnable, t: Throwable) {
            submittedCount.decrementAndGet()
            super.afterExecute(r, t)
        }

        override fun execute(command: Runnable) {
            if (this.isShutdown) return
            submittedCount.incrementAndGet()
            try {
                super.execute(command)
            } catch (ignore: RejectedExecutionException) {
                Log.e("ThreadUtils", "This will not happen!")
                workQueue.offer(command)
            } catch (t: Throwable) {
                submittedCount.decrementAndGet()
            }
        }

        companion object {
            fun createPool(type: Int, priority: Int): ExecutorService {
                return when (type) {
                    TYPE_SINGLE.toInt() -> ThreadPoolExecutor4Util(
                        1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        LinkedBlockingQueue4Util(),
                        UtilsThreadFactory("single", priority)
                    )
                    TYPE_CACHED.toInt() -> ThreadPoolExecutor4Util(
                        0, 128,
                        60L, TimeUnit.SECONDS,
                        LinkedBlockingQueue4Util(true),
                        UtilsThreadFactory("cached", priority)
                    )
                    TYPE_IO.toInt() -> ThreadPoolExecutor4Util(
                        2 * CPU_COUNT + 1,
                        2 * CPU_COUNT + 1,
                        30,
                        TimeUnit.SECONDS,
                        LinkedBlockingQueue4Util(),
                        UtilsThreadFactory("io", priority)
                    )
                    TYPE_CPU.toInt() -> ThreadPoolExecutor4Util(
                        CPU_COUNT + 1, 2 * CPU_COUNT + 1,
                        30, TimeUnit.SECONDS,
                        LinkedBlockingQueue4Util(true),
                        UtilsThreadFactory("cpu", priority)
                    )
                    else -> ThreadPoolExecutor4Util(
                        type, type,
                        0L, TimeUnit.MILLISECONDS,
                        LinkedBlockingQueue4Util(),
                        UtilsThreadFactory("fixed($type)", priority)
                    )
                }
            }
        }

        init {
            workQueue.mPool = this
        }
    }

    internal class LinkedBlockingQueue4Util : LinkedBlockingQueue<Runnable> {
        @Volatile
        var mPool: ThreadPoolExecutor4Util? = null
        var mCapacity = Int.MAX_VALUE

        internal constructor() : super() {}
        internal constructor(isAddSubThreadFirstThenAddQueue: Boolean) : super() {
            if (isAddSubThreadFirstThenAddQueue) {
                mCapacity = 0
            }
        }

        internal constructor(capacity: Int) : super() {
            mCapacity = capacity
        }

        override fun offer(runnable: Runnable): Boolean {
            return if (mCapacity <= size && mPool != null && mPool!!.poolSize < mPool!!.maximumPoolSize
            ) {
                // create a non-core thread
                false
            } else super.offer(runnable)
        }
    }

    internal class UtilsThreadFactory @JvmOverloads constructor(
        prefix: String,
        private val priority: Int,
        private val isDaemon: Boolean = false
    ) : AtomicLong(), ThreadFactory {

        companion object {
            private val POOL_NUMBER = AtomicInteger(1)
            private const val serialVersionUID = -9209200509960368598L
        }

        private val namePrefix = prefix + "-pool-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-"

        override fun newThread(r: Runnable): Thread {
            val t: Thread = object : Thread(r, namePrefix + andIncrement) {
                override fun run() {
                    try {
                        super.run()
                    } catch (t: Throwable) {
                        Log.e("ThreadUtils", "Request threw uncaught throwable", t)
                    }
                }
            }
            t.isDaemon = isDaemon
            t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e -> println(e) }
            t.priority = priority
            return t
        }

        override fun toByte(): Byte {
            return get().toByte()
        }

        override fun toChar(): Char {
            return get().toChar()
        }

        override fun toShort(): Short {
            return get().toShort()
        }

    }

    abstract class SimpleTask<T> : Task<T>() {
        override fun onCancel() {
            logD( "onCancel: " + Thread.currentThread())
        }

        override fun onFail(t: Throwable?) {
            Log.e("ThreadUtils", "onFail: ", t)
        }
    }

    abstract class ConsumerTask<Result>(private val consumer: Utils.Consumer<Result>?) : SimpleTask<Result>() {
        override fun onSuccess(result: Result) {
            consumer?.accept(result)
        }
    }

    abstract class Task<T> : Runnable {
        private val state = AtomicInteger(NEW)

        @Volatile
        private var isSchedule = false

        @Volatile
        private var runner: Thread? = null
        private var mTimer: Timer? = null
        private var mTimeoutMillis: Long = 0
        private var mTimeoutListener: OnTimeoutListener? = null
        private var deliver: Executor? = null

        @Throws(Throwable::class)
        abstract fun doInBackground(): T
        abstract fun onSuccess(result: T)
        abstract fun onCancel()
        abstract fun onFail(t: Throwable?)
        override fun run() {
            if (isSchedule) {
                if (runner == null) {
                    if (!state.compareAndSet(NEW, RUNNING)) return
                    runner = Thread.currentThread()
                    if (mTimeoutListener != null) {
                        Log.w("ThreadUtils", "Scheduled task doesn't support timeout.")
                    }
                } else {
                    if (state.get() != RUNNING) return
                }
            } else {
                if (!state.compareAndSet(NEW, RUNNING)) return
                runner = Thread.currentThread()
                if (mTimeoutListener != null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            if (!isDone && mTimeoutListener != null) {
                                timeout()
                                mTimeoutListener!!.onTimeout()
                            }
                        }
                    }, mTimeoutMillis)
                }
            }
            try {
                val result = doInBackground()
                if (isSchedule) {
                    if (state.get() != RUNNING) return
                    getDeliver()!!.execute { onSuccess(result) }
                } else {
                    if (!state.compareAndSet(RUNNING, COMPLETING)) return
                    getDeliver()!!.execute {
                        onSuccess(result)
                        onDone()
                    }
                }
            } catch (ignore: InterruptedException) {
                state.compareAndSet(CANCELLED, INTERRUPTED)
            } catch (throwable: Throwable) {
                if (!state.compareAndSet(RUNNING, EXCEPTIONAL)) return
                getDeliver()!!.execute {
                    onFail(throwable)
                    onDone()
                }
            }
        }

        @JvmOverloads
        fun cancel(mayInterruptIfRunning: Boolean = true) {
            synchronized(state) {
                if (state.get() > RUNNING) return
                state.set(CANCELLED)
            }
            if (mayInterruptIfRunning) {
                if (runner != null) {
                    runner!!.interrupt()
                }
            }
            getDeliver()!!.execute {
                onCancel()
                onDone()
            }
        }

        private fun timeout() {
            synchronized(state) {
                if (state.get() > RUNNING) return
                state.set(TIMEOUT)
            }
            if (runner != null) {
                runner!!.interrupt()
            }
            onDone()
        }

        val isCanceled: Boolean
            get() = state.get() >= CANCELLED
        val isDone: Boolean
            get() = state.get() > RUNNING

        fun setDeliver(deliver: Executor?): Task<T> {
            this.deliver = deliver
            return this
        }

        /**
         * Scheduled task doesn't support timeout.
         */
        fun setTimeout(timeoutMillis: Long, listener: OnTimeoutListener?): Task<T> {
            mTimeoutMillis = timeoutMillis
            mTimeoutListener = listener
            return this
        }

        fun setSchedule(isSchedule: Boolean) {
            this.isSchedule = isSchedule
        }

        private fun getDeliver(): Executor? {
            return if (deliver == null) {
                globalDeliver
            } else deliver
        }

        @CallSuper
        protected fun onDone() {
            TASK_POOL_MAP.remove(this)
            if (mTimer != null) {
                mTimer!!.cancel()
                mTimer = null
                mTimeoutListener = null
            }
        }

        interface OnTimeoutListener {
            fun onTimeout()
        }

        companion object {
            private const val NEW = 0
            private const val RUNNING = 1
            private const val EXCEPTIONAL = 2
            private const val COMPLETING = 3
            private const val CANCELLED = 4
            private const val INTERRUPTED = 5
            private const val TIMEOUT = 6
        }
    }

    class SyncValue<T> {
        private val mLatch = CountDownLatch(1)
        private val mFlag = AtomicBoolean()
        private var mValue: T? = null
        fun setValue(value: T) {
            if (mFlag.compareAndSet(false, true)) {
                mValue = value
                mLatch.countDown()
            }
        }

        val value: T?
            get() {
                if (!mFlag.get()) {
                    try {
                        mLatch.await()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                return mValue
            }
    }
}