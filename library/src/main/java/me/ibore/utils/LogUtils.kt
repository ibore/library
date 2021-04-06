package me.ibore.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

object LogUtils {
    // JSON 格式内容缩进
    private const val JSON_INDENT = 4

    /**
     * 是否打印日志 线上 (release) = false, 开发 (debug) = true
     */
    private var sPrintLog = false

    // 默认 DEFAULT_TAG
    private val DEFAULT_TAG = LogUtils::class.java.simpleName

    // ===========
    // = 通知输出 =
    // ===========
    // 默认日志输出接口
    private var sPrint: Print = object : Print {
        override fun printLog(logType: Int, tag: String?, message: String?) {
            // 防止 null 处理
            if (message == null) return
            when (logType) {
                Log.VERBOSE -> Log.v(tag, message)
                Log.DEBUG -> Log.d(tag, message)
                Log.INFO -> Log.i(tag, message)
                Log.WARN -> Log.w(tag, message)
                Log.ERROR -> Log.e(tag, message)
                Log.ASSERT -> Log.wtf(tag, message)
                else -> Log.wtf(tag, message)
            }
        }
    }

    fun setPrintLog(printLog :Boolean) {
        this.sPrintLog = printLog
    }

    /**
     * 设置日志输出接口
     * @param print 日志输出接口
     */
    fun setPrint(print: Print) {
        sPrint = print
    }


    /**
     * 最终打印日志方法 ( 全部调用此方法 )
     * @param logType 日志类型
     * @param tag     打印 Tag
     * @param message 日志信息
     */
    private fun printLog(logType: Int, tag: String, message: String) {
        sPrint.printLog(logType, tag, message)
    }

    /**
     * 处理信息
     * @param message 日志信息
     * @param args    占位符替换
     * @return 处理 ( 格式化 ) 后准备打印的日志信息
     */
    private fun createMessage(message: String?, vararg args: Any?): String {
        return try {
            if (message != null) {
                if (args.isEmpty()) message else String.format(message, *args)
            } else {
                // 打印内容为 null
                "message is null"
            }
        } catch (e: Exception) {
            // 出现异常
            e.toString()
        }
    }

    /**
     * 拼接错误信息
     * @param throwable 异常
     * @param message   需要打印的消息
     * @param args      动态参数
     * @return 处理 ( 格式化 ) 后准备打印的日志信息
     */
    private fun splitErrorMessage(
        throwable: Throwable?,
        message: String?,
        vararg args: Any?
    ): String {
        return try {
            if (throwable != null) {
                if (message != null) {
                    createMessage(message, *args) + " : " + throwable.toString()
                } else {
                    throwable.toString()
                }
            } else {
                createMessage(message, *args)
            }
        } catch (e: Exception) {
            e.toString()
        }
    }

    // ===============================
    // = 对外公开方法 ( 使用默认 TAG ) =
    // ===============================
    fun d(message: String?, vararg args: Any?) {
        dTag(DEFAULT_TAG, message, *args)
    }

    fun e(throwable: Throwable?) {
        eTag(DEFAULT_TAG, throwable, null)
    }

    fun e(message: String?, vararg args: Any?) {
        e(null as Throwable?, message, *args)
    }

    fun e(throwable: Throwable?, message: String?, vararg args: Any?) {
        eTag(DEFAULT_TAG, throwable, message, *args)
    }

    fun w(message: String?, vararg args: Any?) {
        wTag(DEFAULT_TAG, message, *args)
    }

    fun i(message: String?, vararg args: Any?) {
        iTag(DEFAULT_TAG, message, *args)
    }

    fun v(message: String?, vararg args: Any?) {
        vTag(DEFAULT_TAG, message, *args)
    }

    fun wtf(message: String?, vararg args: Any?) {
        wtfTag(DEFAULT_TAG, message, *args)
    }

    fun json(json: String?) {
        jsonTag(DEFAULT_TAG, json)
    }

    fun xml(xml: String?) {
        xmlTag(DEFAULT_TAG, xml)
    }

    // ===============================
    // = 对外公开方法 ( 日志打印方法 ) =
    // ===============================
    fun dTag(tag: String, message: String?, vararg args: Any?) {
        if (sPrintLog) {
            printLog(Log.DEBUG, tag, createMessage(message, *args))
        }
    }

    fun eTag(tag: String, message: String?, vararg args: Any?) {
        if (sPrintLog) {
            printLog(Log.ERROR, tag, createMessage(message, *args))
        }
    }

    fun eTag(tag: String, throwable: Throwable?) {
        if (sPrintLog) {
            printLog(Log.ERROR, tag, splitErrorMessage(throwable, null))
        }
    }

    fun eTag(tag: String, throwable: Throwable?, message: String?, vararg args: Any?) {
        if (sPrintLog) {
            printLog(Log.ERROR, tag, splitErrorMessage(throwable, message, *args))
        }
    }

    fun wTag(tag: String, message: String?, vararg args: Any?) {
        if (sPrintLog) {
            printLog(Log.WARN, tag, createMessage(message, *args))
        }
    }

    fun iTag(tag: String, message: String?, vararg args: Any?) {
        if (sPrintLog) {
            printLog(Log.INFO, tag, createMessage(message, *args))
        }
    }

    fun vTag(tag: String, message: String?, vararg args: Any?) {
        if (sPrintLog) {
            printLog(Log.VERBOSE, tag, createMessage(message, *args))
        }
    }

    fun wtfTag(tag: String, message: String?, vararg args: Any?) {
        if (sPrintLog) {
            printLog(Log.ASSERT, tag, createMessage(message, *args))
        }
    }

    fun jsonTag(tag: String, json: String?) {
        if (sPrintLog) {
            // 判断传入 JSON 格式信息是否为 null
            if (json.isNullOrEmpty()) {
                printLog(Log.ERROR, tag, "Empty/Null json content")
                return
            }
            try {
                // 属于对象的 JSON 格式信息
                if (json.startsWith("{")) {
                    val jsonObject = JSONObject(json)
                    // 进行缩进
                    val message = jsonObject.toString(JSON_INDENT)
                    // 打印信息
                    printLog(Log.DEBUG, tag, message)
                } else if (json.startsWith("[")) {
                    // 属于数据的 JSON 格式信息
                    val jsonArray = JSONArray(json)
                    // 进行缩进
                    val message = jsonArray.toString(JSON_INDENT)
                    // 打印信息
                    printLog(Log.DEBUG, tag, message)
                } else {
                    // 打印信息
                    printLog(Log.DEBUG, tag, "json content format error")
                }
            } catch (e: Exception) {
                val errorInfo: String
                val throwable = e.cause
                errorInfo = throwable?.toString()
                    ?: try {
                        e.toString()
                    } catch (e1: Exception) {
                        e1.toString()
                    }
                printLog(Log.ERROR, tag, """$errorInfo$json""".trimIndent())
            }
        }
    }

    fun xmlTag(tag: String, xml: String?) {
        if (sPrintLog) {
            // 判断传入 XML 格式信息是否为 null
            if (xml.isNullOrEmpty()) {
                printLog(Log.ERROR, tag, "Empty/Null xml content")
                return
            }
            try {
                val xmlInput: Source = StreamSource(StringReader(xml))
                val xmlOutput = StreamResult(StringWriter())
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transformer.transform(xmlInput, xmlOutput)
                // 获取打印消息
                val message = xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
                // 打印信息
                printLog(Log.DEBUG, tag, message)
            } catch (e: Exception) {
                val errorInfo: String
                val throwable = e.cause
                errorInfo = throwable?.toString()
                    ?: try {
                        e.toString()
                    } catch (e1: Exception) {
                        e1.toString()
                    }
                printLog(Log.ERROR, tag, """$errorInfo$xml""".trimIndent())
            }
        }
    }


    /**
     * detail: 日志输出接口
     * @author Ttt
     */
    interface Print {
        /**
         * 日志打印
         * @param logType 日志类型
         * @param tag     打印 Tag
         * @param message 日志信息
         */
        fun printLog(logType: Int, tag: String?, message: String?)
    }
}