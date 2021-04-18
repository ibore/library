package me.ibore.utils

import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * <pre>
 * author: MilkZS
 * time  : 2019/01/09
 * desc  : https 工具类
</pre> *
 */
object HttpsUtils {

    private const val CONNECT_TIMEOUT_TIME = 15000
    private const val READ_TIMEOUT_TIME = 19000

    /**
     * POST + JSON
     *
     * @param data send data
     * @param url  target url
     * @return data receive from server
     * @author MilkZS
     */
    fun postJson(data: String, url: String): String? {
        return doHttpAction(data, json = true, post = true, url = url)
    }

    /**
     * POST + FORM
     *
     * @param data send data
     * @param url  target url
     * @return data receive from serv
     * @author MilkZS
     */
    fun postForm(data: String, url: String): String? {
        return doHttpAction(data, json = false, post = true, url = url)
    }

    /**
     * GET + JSON
     *
     * @param data send data
     * @param url  target url
     * @return data receive from server
     * @author MilkZS
     */
    fun getJson(data: String, url: String): String? {
        return doHttpAction(data, true, false, url)
    }

    /**
     * GET + FORM
     *
     * @param data send data
     * @param url  target url
     * @return data receive from server
     * @author MilkZS
     */
    fun getForm(data: String, url: String): String? {
        return doHttpAction(data, json = false, post = false, url = url)
    }

    private fun doHttpAction(data: String, json: Boolean, post: Boolean, url: String): String? {
        var connection: HttpURLConnection? = null
        var os: DataOutputStream? = null
        var `is`: InputStream? = null
        try {
            val sUrl = URL(url)
            connection = sUrl.openConnection() as HttpURLConnection
            connection.connectTimeout = CONNECT_TIMEOUT_TIME
            connection.readTimeout = READ_TIMEOUT_TIME
            if (post) {
                connection.requestMethod = "POST"
            } else {
                connection.requestMethod = "GET"
            }
            //允许输入输出
            connection.doInput = true
            connection.doOutput = true
            // 是否使用缓冲
            connection.useCaches = false
            // 本次连接是否处理重定向，设置成true，系统自动处理重定向；
            // 设置成false，则需要自己从http reply中分析新的url自己重新连接。
            connection.instanceFollowRedirects = true
            // 设置请求头里的属性
            if (json) {
                connection.setRequestProperty("Content-Type", "application/json")
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.setRequestProperty("Content-Length", data.length.toString() + "")
            }
            connection.connect()
            os = DataOutputStream(connection.outputStream)
            os.write(data.toByteArray(), 0, data.toByteArray().size)
            os.flush()
            os.close()
            `is` = connection.inputStream
            val scan = Scanner(`is`)
            scan.useDelimiter("\\A")
            if (scan.hasNext()) return scan.next()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
            if (os != null) {
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}