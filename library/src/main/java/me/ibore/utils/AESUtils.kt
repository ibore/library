package me.ibore.utils

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * detail: AES 对称加密工具类
 *
 * @author Ttt
 * <pre>
 * Advanced Encryption Standard 高级数据加密标准 ( 对称加密算法 )
 * AES 算法可以有效抵制针对 DES 的攻击算法
</pre> *
 */
object AESUtils {
    /**
     * 生成密钥
     *
     * @return 密钥 byte[]
     */
    fun initKey(): ByteArray? {
        try {
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256) // 192 256
            val secretKey = keyGen.generateKey()
            return secretKey.encoded
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * AES 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的 byte[]
     */
    fun encrypt(data: ByteArray, key: ByteArray): ByteArray? {
        try {
            val secretKey: SecretKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * AES 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密后的 byte[]
     */
    fun decrypt(data: ByteArray, key: ByteArray): ByteArray? {
        try {
            val secretKey: SecretKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }
}