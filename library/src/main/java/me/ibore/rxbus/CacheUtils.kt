package me.ibore.rxbus

import io.reactivex.disposables.Disposable
import me.ibore.rxbus.Utils.getClassFromObject
import me.ibore.rxbus.Utils.logW
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2017/12/25
 * desc  :
</pre> *
 */
internal class CacheUtils private constructor() {

    private val stickyEventsMap: MutableMap<Class<*>?, MutableList<BusMessage>> = ConcurrentHashMap<Class<*>?, MutableList<BusMessage>>()
    private val disposablesMap: MutableMap<Any, MutableList<Disposable>> = ConcurrentHashMap()

    fun addStickyEvent(event: Any, tag: String) {
        val eventType = getClassFromObject(event)
        synchronized(stickyEventsMap) {
            var stickyEvents: MutableList<BusMessage>? = stickyEventsMap[eventType]
            if (stickyEvents == null) {
                stickyEvents = ArrayList<BusMessage>()
                stickyEvents.add(BusMessage(event, tag))
                stickyEventsMap.put(eventType, stickyEvents)
            } else {
                for (i in stickyEvents.indices.reversed()) {
                    val tmp: BusMessage = stickyEvents[i]
                    if (tmp.isSameType(eventType, tag)) {
                        logW("The sticky event already added.")
                        return
                    }
                }
                stickyEvents.add(BusMessage(event, tag))
            }
        }
    }

    fun removeStickyEvent(event: Any?, tag: String?) {
        val eventType = getClassFromObject(event)
        synchronized(stickyEventsMap) {
            val stickyEvents: MutableList<BusMessage> = stickyEventsMap[eventType] ?: return
            for (i in stickyEvents.indices.reversed()) {
                val stickyEvent: BusMessage = stickyEvents[i]
                if (stickyEvent.isSameType(eventType, tag)) {
                    stickyEvents.removeAt(i)
                    break
                }
            }
            if (stickyEvents.size == 0) stickyEventsMap.remove(eventType)
        }
    }

    fun findStickyEvent(eventType: Class<*>?, tag: String?): BusMessage? {
        synchronized(stickyEventsMap) {
            val stickyEvents: MutableList<BusMessage> = stickyEventsMap[eventType] ?: return null
            val size = stickyEvents.size
            var res: BusMessage? = null
            for (i in size - 1 downTo 0) {
                val stickyEvent: BusMessage = stickyEvents[i]
                if (stickyEvent.isSameType(eventType, tag)) {
                    res = stickyEvents[i]
                    break
                }
            }
            return res
        }
    }

    fun addDisposable(subscriber: Any, disposable: Disposable) {
        synchronized(disposablesMap) {
            var list = disposablesMap[subscriber]
            if (list == null) {
                list = ArrayList()
                list.add(disposable)
                disposablesMap.put(subscriber, list)
            } else {
                list.add(disposable)
            }
        }
    }

    fun removeDisposables(subscriber: Any?) {
        synchronized(disposablesMap) {
            val disposables = disposablesMap[subscriber] ?: return
            for (disposable in disposables) {
                if (!disposable.isDisposed) {
                    disposable.dispose()
                }
            }
            disposables.clear()
            disposablesMap.remove(subscriber)
        }
    }

    private object Holder {
        val CACHE_UTILS = CacheUtils()
    }

    companion object {
        val INSTANCE: CacheUtils = Holder.CACHE_UTILS
    }
}