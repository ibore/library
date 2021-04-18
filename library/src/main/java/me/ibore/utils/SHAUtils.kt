package me.ibore.utils

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

/**
 * detail: SHA 加密工具类
 * @author Ttt
 */
object SHAUtils {


    /**
     * 加密内容 SHA1
     * @param data 待加密数据
     * @return SHA1 加密后的字符串
     */
    fun sha1(data: String): String {
        return shaHex(data, "SHA-1")
    }

    /**
     * 加密内容 SHA224
     * @param data 待加密数据
     * @return SHA224 加密后的字符串
     */
    fun sha224(data: String): String {
        return shaHex(data, "SHA-224")
    }

    /**
     * 加密内容 SHA256
     * @param data 待加密数据
     * @return SHA256 加密后的字符串
     */
    fun sha256(data: String): String {
        return shaHex(data, "SHA-256")
    }

    /**
     * 加密内容 SHA384
     * @param data 待加密数据
     * @return SHA384 加密后的字符串
     */
    fun sha384(data: String): String {
        return shaHex(data, "SHA-384")
    }

    /**
     * 加密内容 SHA512
     * @param data 待加密数据
     * @return SHA512 加密后的字符串
     */
    fun sha512(data: String): String {
        return shaHex(data, "SHA-512")
    }
    // =
    /**
     * 获取文件 SHA1 值
     * @param filePath 文件路径
     * @return 文件 SHA1 字符串信息
     */
    fun getFileSHA1(filePath: String): String {
         if (filePath.isBlank()) return ""
        return getFileSHA(File(filePath), "SHA-1")
    }

    /**
     * 获取文件 SHA1 值
     * @param file 文件
     * @return 文件 SHA1 字符串信息
     */
    fun getFileSHA1(file: File): String {
        return getFileSHA(file, "SHA-1")
    }

    /**
     * 获取文件 SHA256 值
     * @param filePath 文件路径
     * @return 文件 SHA256 字符串信息
     */
    fun getFileSHA256(filePath: String): String {
        if (filePath.isBlank()) return ""
        return getFileSHA(File(filePath), "SHA-256")
    }

    /**
     * 获取文件 SHA256 值
     * @param file 文件
     * @return 文件 SHA256 字符串信息
     */
    fun getFileSHA256(file: File): String {
        return getFileSHA(file, "SHA-256")
    }
    // =
    /**
     * 加密内容 SHA 模板
     * @param data      待加密数据
     * @param algorithm 算法
     * @return SHA 算法加密后的字符串
     */
    fun shaHex(data: String, algorithm: String): String {
        try {
            val bytes = data.toByteArray()
            // 获取 SHA-1 摘要算法的 MessageDigest 对象
            val digest = MessageDigest.getInstance(algorithm)
            // 使用指定的字节更新摘要
            digest.update(bytes)
            // 获取密文
            return ConvertUtils.bytes2HexString(digest.digest(), true)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return ""
    }

    /**
     * 获取文件 SHA 值
     * @param file      文件
     * @param algorithm 算法
     * @return 文件指定 SHA 字符串信息
     */
    fun getFileSHA(file: File, algorithm: String): String {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            val digest = MessageDigest.getInstance(algorithm)
            var numRead: Int
            while (inputStream.read(buffer).also { numRead = it } > 0) {
                digest.update(buffer, 0, numRead)
            }
            return ConvertUtils.bytes2HexString(digest.digest(), true)
        } catch (e: Exception) {
            LogUtils.d(e)
        } finally {
            CloseUtils.closeIOQuietly(inputStream)
        }
        return ""
    }
}