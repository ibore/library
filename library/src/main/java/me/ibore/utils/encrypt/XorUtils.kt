package me.ibore.utils.encrypt

/**
 * detail: 异或 ( 加密 ) 工具类
 * @author Ttt
 * <pre>
 * 位运算可以实现很多高级、高效的运算
 * 可用于 IM 二进制数据包加密
 * 1. 能够实现加密
 * 2. 采用异或加密算法不会改变二进制数据的长度这对二进制数据包封包起到不小的好处
 * 也可用于记事本等场景
 *
 *
 * 参考链接
 * @see [](http://www.cnblogs.com/whoislcj/p/5944917.html)
</pre> *
 */
object XorUtils {
    /**
     * 加解密 ( 固定 Key 方式 ) 这种方式 加解密 方法共用
     * <pre>
     * 加密: byte[] bytes = encryptAsFix("123".getBytes());
     * 解密: String str = new String(encryptAsFix(bytes));
    </pre> *
     * @param data 待加解密数据
     * @return 加解密后的数据 byte[]
     */
    fun encryptAsFix(data: ByteArray?): ByteArray? {
        if (data == null) return null
        val len = data.size
        if (len == 0) return null
        val key = 0x12
        for (i in 0 until len) {
            data[i] = (data[i].toInt() xor key).toByte()
        }
        return data
    }
    // =
    /**
     * 加密 ( 非固定 Key 方式 )
     * @param data 待加密数据
     * @return 加密后的数据 byte[]
     */
    fun encrypt(data: ByteArray?): ByteArray? {
        if (data == null) return null
        val len = data.size
        if (len == 0) return null
        var key = 0x12
        for (i in 0 until len) {
            data[i] = (data[i].toInt() xor key).toByte()
            key = data[i].toInt()
        }
        return data
    }

    /**
     * 解密 ( 非固定 Key 方式 )
     * @param data 待解密数据
     * @return 解密后的数据 byte[]
     */
    fun decrypt(data: ByteArray?): ByteArray? {
        if (data == null) return null
        val len = data.size
        if (len == 0) return null
        val key = 0x12
        for (i in len - 1 downTo 1) {
            data[i] = (data[i].toInt() xor data[i - 1].toInt()).toByte()
        }
        data[0] = (data[0].toInt() xor key).toByte()
        return data
    }
}