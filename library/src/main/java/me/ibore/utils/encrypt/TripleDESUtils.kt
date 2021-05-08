package me.ibore.utils.encrypt

import android.annotation.SuppressLint
import me.ibore.utils.LogUtils.d
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * detail: 3DES 对称加密工具类
 * @author Ttt
 * <pre>
 * Triple DES、DESede 进行了三重 DES 加密的算法 ( 对称加密算法 )
</pre> *
 */
object TripleDESUtils {
    /**
     * 生成密钥
     * @return 密钥 byte[]
     */
    fun initKey(): ByteArray? {
        try {
            val keyGen = KeyGenerator.getInstance("DESede")
            keyGen.init(168) // 112 168
            val secretKey = keyGen.generateKey()
            return secretKey.encoded
        } catch (e: Exception) {
            d(e)
        }
        return null
    }

    /**
     * 3DES 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的 byte[]
     */
    @SuppressLint("GetInstance")
    fun encrypt(data: ByteArray, key: ByteArray): ByteArray? {
        try {
            val secretKey: SecretKey = SecretKeySpec(key, "DESede")
            val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            d(e)
        }
        return null
    }

    /**
     * 3DES 解密
     * @param data 待加密数据
     * @param key  密钥
     * @return 解密后的 byte[]
     */
    @SuppressLint("GetInstance")
    fun decrypt(data: ByteArray, key: ByteArray): ByteArray? {
        try {
            val secretKey: SecretKey = SecretKeySpec(key, "DESede")
            val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            d(e)
        }
        return null
    }
}