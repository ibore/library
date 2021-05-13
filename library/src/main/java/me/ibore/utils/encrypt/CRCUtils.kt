package me.ibore.utils.encrypt

import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.CRC32

/**
 * detail: CRC 工具类
 * @author Ttt
 * <pre>
 * Cyclic Redundancy Check 循环冗余校验
 * CRC 是一种根据网络数据包或电脑文件等数据产生简短固定位数校验码的一种散列函数
</pre> *
 */
object CRCUtils {

    /**
     * 获取 CRC32 值
     * @param data 字符串数据
     * @return CRC32 long 值
     */
    fun getCRC32(data: String?): Long {
        if (data == null) return -1L
        try {
            val crc32 = CRC32()
            crc32.update(data.toByteArray())
            return crc32.value
        } catch (e: Exception) {
            logD(e)
        }
        return -1L
    }

    /**
     * 获取 CRC32 值
     * @param data 字符串数据
     * @return CRC32 字符串
     */
    fun getCRC32ToHexString(data: String?): String? {
        if (data == null) return null
        try {
            val crc32 = CRC32()
            crc32.update(data.toByteArray())
            return java.lang.Long.toHexString(crc32.value)
        } catch (e: Exception) {
            logD(e)
        }
        return null
    }

    /**
     * 获取文件 CRC32 值
     * @param filePath 文件路径
     * @return 文件 CRC32 值
     */
    fun getFileCRC32(filePath: String?): String? {
        if (filePath == null) return null
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(filePath)
            val buffer = ByteArray(1024)
            val crc32 = CRC32()
            var numRead: Int
            while (inputStream.read(buffer).also { numRead = it } > 0) {
                crc32.update(buffer, 0, numRead)
            }
            return java.lang.Long.toHexString(crc32.value)
        } catch (e: Exception) {
            logD(e)
        } finally {
            CloseUtils.closeIOQuietly(inputStream)
        }
        return null
    }
}