package me.ibore.utils

import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

/**
 * detail: MD5 加密工具类
 * @author Ttt
 * <pre>
 * Message Digest 消息摘要算法
</pre> *
 */
object MD5Utils {

    /**
     * 加密内容 (32 位小写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5(data: String?): String? {
        if (data == null) return null
        try {
            return md5(data.toByteArray())
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * 加密内容 (32 位小写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5(data: ByteArray?): String? {
        if (data == null) return null
        try {
            // 获取 MD5 摘要算法的 MessageDigest 对象
            val digest = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            digest.update(data)
            // 获取密文
            return ConvertUtils.bytes2HexString(digest.digest(), true)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * 加密内容 (32 位大写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5Upper(data: String?): String? {
        if (data == null) return null
        try {
            return md5Upper(data.toByteArray())
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * 加密内容 (32 位大写 MD5)
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    fun md5Upper(data: ByteArray?): String? {
        if (data == null) return null
        try {
            // 获取 MD5 摘要算法的 MessageDigest 对象
            val digest = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            digest.update(data)
            // 获取密文
            return ConvertUtils.bytes2HexString(digest.digest(), false)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }
    // =
    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值
     */
    fun getFileMD5(filePath: String?): ByteArray? {
        val file = if (filePath.isNullOrBlank()) null else File(filePath)
        return getFileMD5(file)
    }

    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值转十六进制字符串
     */
    fun getFileMD5ToHexString(filePath: String?): String {
        val file = if (filePath.isNullOrBlank()) null else File(filePath)
        return getFileMD5ToHexString(file)
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值转十六进制字符串
     */
    fun getFileMD5ToHexString(file: File?): String {
        return ConvertUtils.bytes2HexString(getFileMD5(file))
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值 byte[]
     */
    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        return try {
            val fis = FileInputStream(file)
            var digest = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, digest)
            val buffer = ByteArray(256 * 1024)
            while (true) {
                if (dis.read(buffer) <= 0) break
            }
            digest = dis.messageDigest
            digest.digest()
        } catch (e: Exception) {
            LogUtils.d(e)
            null
        } finally {
            CloseUtils.closeIOQuietly(dis)
        }
    }
}