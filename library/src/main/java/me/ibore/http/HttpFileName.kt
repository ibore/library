package me.ibore.http

import okhttp3.Response
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

object HttpFileName {

    /**
     * 根据Response获取文件名
     */
    fun getFileName(response: Response): String {
        return getFileName(response.header("Content-Disposition"), response.request.url.toString())
    }

    /**
     * 根据响应头或者url获取文件名
     */
    fun getFileName(dispositionHeader: String?, url: String): String {
        var fileName: String? = null
        if (!dispositionHeader.isNullOrBlank()) {
            fileName = getHeaderFileName(dispositionHeader)
        }
        if (fileName.isNullOrBlank()) fileName = getUrlFileName(url)
        if (fileName.isNullOrBlank()) fileName = "temp_" + System.currentTimeMillis()
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return fileName!!
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    fun getHeaderFileName(dispositionHeader: String): String? {
        //文件名可能包含双引号，需要去除
        val splits = dispositionHeader.replace("\"".toRegex(), "").split(";")
        var split: String? = null
        for (s in splits) {
            if (null == split && (s.contains("filename=") || s.contains("filename*="))) {
                split = s
            }
        }
        if (null == split) {
            return null
        } else if (split.contains("filename=")) {
            return split.replace("filename=", "")
        } else if (split.contains("filename*=")) {
            split = split.replace("filename*=", "")
            val encode = "UTF-8''"
            if (split.startsWith(encode)) {
                return split.substring(encode.length, split.length)
            }
        }
        return null
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    fun getUrlFileName(url: String): String? {
        var filename: String? = null
        val strings = url.split("/")
        for (string in strings) {
            if (string.contains("?")) {
                val endIndex = string.indexOf("?")
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex)
                    return filename
                }
            }
        }
        if (strings.isNotEmpty()) {
            filename = strings[strings.size - 1]
        }
        return filename
    }

}