package me.ibore.rxbus

internal class BusMessage(var mEvent: Any, var mTag: String) {

    fun isSameType(eventType: Class<*>?, tag: String?): Boolean {
        return (Utils.equals(eventType, eventType) && Utils.equals(mTag, tag))
    }

    val eventType: Class<*>? = Utils.getClassFromObject(mEvent)

    override fun toString(): String {
        return "event: $mEvent, tag: $mTag"
    }

}