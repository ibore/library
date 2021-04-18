package me.ibore.exception

class HttpException : XException {

    val code: Int

    constructor(code: Int) : super("$code ${HTTP_STATUS[code] ?: "Unknown"}") {
        this.code = code
    }

    constructor(code: Int, message: String) : super("$code ${if (message.isBlank()) HTTP_STATUS[code] ?: "Unknown" else message}") {
        this.code = code
    }

    companion object {

        val HTTP_STATUS = HashMap<Int, String?>()

        init {
            HTTP_STATUS[100] = "Continue"
            HTTP_STATUS[101] = "Switching Protocols"
            HTTP_STATUS[102] = "Processing"
            HTTP_STATUS[200] = "OK"
            HTTP_STATUS[201] = "Created"
            HTTP_STATUS[202] = "Accepted"
            HTTP_STATUS[203] = "Non-Authoritative Information"
            HTTP_STATUS[204] = "No Content"
            HTTP_STATUS[205] = "Reset Content"
            HTTP_STATUS[206] = "Partial Content"
            HTTP_STATUS[300] = "Multiple Choices"
            HTTP_STATUS[301] = "Moved Permanently"
            HTTP_STATUS[302] = "Found"
            HTTP_STATUS[303] = "See Other"
            HTTP_STATUS[304] = "Not Modified"
            HTTP_STATUS[305] = "Use Proxy"
            HTTP_STATUS[306] = "Switch Proxy"
            HTTP_STATUS[307] = "Temporary Redirect"
            HTTP_STATUS[400] = "Bad Request"
            HTTP_STATUS[401] = "Unauthorized"
            HTTP_STATUS[402] = "Payment Required"
            HTTP_STATUS[403] = "Forbidden"
            HTTP_STATUS[404] = "Not Found"
            HTTP_STATUS[405] = "Method Not Allowed"
            HTTP_STATUS[406] = "Not Acceptable"
            HTTP_STATUS[407] = "Proxy Authentication Required"
            HTTP_STATUS[408] = "Request Time-out"
            HTTP_STATUS[409] = "Conflict"
            HTTP_STATUS[410] = "Gone"
            HTTP_STATUS[411] = "Length Required"
            HTTP_STATUS[412] = "Precondition Failed"
            HTTP_STATUS[413] = "Request Entity Too Large"
            HTTP_STATUS[414] = "Request-URI Too Large"
            HTTP_STATUS[415] = "Unsupported Media Type"
            HTTP_STATUS[416] = "Requested range not satisfiable"
            HTTP_STATUS[417] = "Expectation Failed"
            HTTP_STATUS[421] = "too many connections"
            HTTP_STATUS[422] = "Unprocessable Entity"
            HTTP_STATUS[423] = "Locked"
            HTTP_STATUS[424] = "Failed Dependency"
            HTTP_STATUS[500] = "Internal Server Error"
            HTTP_STATUS[501] = "Not Implemented"
            HTTP_STATUS[502] = "Bad Gateway"
            HTTP_STATUS[503] = "Service Unavailable"
            HTTP_STATUS[504] = "Gateway Time-out"
            HTTP_STATUS[505] = "HTTP Version not supported"
            HTTP_STATUS[600] = "Unparseable Response Headers"
        }
    }

}
