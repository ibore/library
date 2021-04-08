package me.ibore.base

open class XException : RuntimeException {

    constructor()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)

}
