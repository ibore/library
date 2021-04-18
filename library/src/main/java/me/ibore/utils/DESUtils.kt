package me.ibore.utils

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * detail: DES 对称加密工具类
 * @author Ttt
 * <pre>
 * Data Encryption Standard 数据加密标准 ( 对称加密算法 )
</pre> *
 */
object DESUtils {

    /**
     * 获取可逆算法 DES 的密钥
     * @param key 前八个字节将被用来生成密钥
     * @return 可逆算法 DES 的密钥
     */
    fun getDESKey(key: ByteArray?): Key? {
        if (key == null) return null
        try {
            val desKey = DESKeySpec(key)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            return keyFactory.generateSecret(desKey)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * DES 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的 byte[]
     */
    fun encrypt(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        if (data == null || key == null) return null
        try {
            val secretKey: SecretKey = SecretKeySpec(key, "DES")
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * DES 解密
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密后的 byte[]
     */
    fun decrypt(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        if (data == null || key == null) return null
        try {
            val secretKey: SecretKey = SecretKeySpec(key, "DES")
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }
}