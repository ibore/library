package me.ibore.exception


class ServerException(private val code: Int, override val message: String, val data: String) : XException(message) {

    override fun toString(): String {
        return "SERVER $code $message"
    }

}