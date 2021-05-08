package me.ibore.utils

import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

/**
 * MD5 加密工具类
 */
object MD5Utils {

    /**
     * 加密内容 (32 位小写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5(data: String): String {
        return md5(data.toByteArray())
    }

    /**
     * 加密内容 (32 位小写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5(data: ByteArray): String {
        // 获取 MD5 摘要算法的 MessageDigest 对象
        val digest = MessageDigest.getInstance("MD5")
        // 使用指定的字节更新摘要
        digest.update(data)
        // 获取密文
        return ConvertUtils.bytes2HexString(digest.digest(), true)
    }

    /**
     * 加密内容 (32 位大写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5Upper(data: String): String {
        return md5Upper(data.toByteArray())
    }

    /**
     * 加密内容 (32 位大写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5Upper(data: ByteArray): String {
        // 获取 MD5 摘要算法的 MessageDigest 对象
        val digest = MessageDigest.getInstance("MD5")
        // 使用指定的字节更新摘要
        digest.update(data)
        // 获取密文
        return ConvertUtils.bytes2HexString(digest.digest(), false)
    }
    // =
    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值
     */
    fun getFileMD5(filePath: String): ByteArray {
        return FileUtils.getFileMD5(filePath)
    }

    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值转十六进制字符串
     */
    fun getFileMD5ToHexString(filePath: String): String {
        return ConvertUtils.bytes2HexString(FileUtils.getFileMD5(filePath))
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值转十六进制字符串
     */
    fun getFileMD5ToHexString(file: File): String {
        return ConvertUtils.bytes2HexString(FileUtils.getFileMD5(file))
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值 byte[]
     */
    fun getFileMD5(file: File): ByteArray {
        return FileUtils.getFileMD5(file)
    }
}