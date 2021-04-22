package me.ibore.rxbus

internal class BusMessage(var event: Any, var tag: String) {

    fun isSameType(eventType: Class<*>?, tag: String?): Boolean {
        return (Utils.equals(this.eventType, eventType) && Utils.equals(this.tag, tag))
    }

    val eventType: Class<*>? = Utils.getClassFromObject(event)

    override fun toString(): String {
        return "event: $event, tag: $tag"
    }

}