package me.ibore.utils

import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.Key
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * detail: 加解密通用工具类
 * @author Blankj
 * @author Ttt
 */
object EncryptUtils {

    /**
     * MD2 加密
     * @param data 待加密数据
     * @return MD2 加密后的数据
     */
    fun encryptMD2(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "MD2")
    }

    /**
     * MD2 加密
     * @param data 待加密数据
     * @return MD2 加密后的十六进制字符串
     */
    fun encryptMD2ToHexString(data: String?): String? {
        return if (data.isNullOrEmpty()) null else encryptMD2ToHexString(data.toByteArray())
    }

    /**
     * MD2 加密
     * @param data 待加密数据
     * @return MD2 加密后的十六进制字符串
     */
    fun encryptMD2ToHexString(data: ByteArray?): String {
        return ConvertUtils.bytes2HexString(encryptMD2(data))
    }
    // =
    /**
     * MD5 加密
     * @param data 待加密数据
     * @return MD5 加密后的数据
     */
    fun encryptMD5(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "MD5")
    }

    /**
     * MD5 加密
     * @param data 待加密数据
     * @return MD5 加密后的十六进制字符串
     */
    fun encryptMD5ToHexString(data: String?): String? {
        return if (data.isNullOrEmpty()) null else encryptMD5ToHexString(data.toByteArray())
    }

    /**
     * MD5 加密
     * @param data 待加密数据
     * @param salt salt
     * @return MD5 加密后的十六进制字符串
     */
    fun encryptMD5ToHexString(
        data: String?,
        salt: String?
    ): String? {
        if (data == null && salt == null) return null
        if (salt == null) return ConvertUtils.bytes2HexString(encryptMD5(data!!.toByteArray()))
        return if (data == null) ConvertUtils.bytes2HexString(encryptMD5(salt.toByteArray())) else ConvertUtils.bytes2HexString(
            encryptMD5((data + salt).toByteArray())
        )
    }
    // =
    /**
     * MD5 加密
     * @param data 待加密数据
     * @return MD5 加密后的十六进制字符串
     */
    fun encryptMD5ToHexString(data: ByteArray?): String {
        return ConvertUtils.bytes2HexString(encryptMD5(data))
    }

    /**
     * MD5 加密
     * @param data 待加密数据
     * @param salt salt
     * @return MD5 加密后的十六进制字符串
     */
    fun encryptMD5ToHexString(
        data: ByteArray?,
        salt: ByteArray?
    ): String? {
        if (data == null && salt == null) return null
        if (salt == null) return ConvertUtils.bytes2HexString(encryptMD5(data))
        if (data == null) return ConvertUtils.bytes2HexString(encryptMD5(salt))
        // 拼接数据
        val bytes: ByteArray? = ArrayUtils.add(data, salt)
        return ConvertUtils.bytes2HexString(encryptMD5(bytes))
    }
    // =
    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值
     */
    fun encryptMD5File(filePath: String?): ByteArray? {
        val file = if (filePath.isNullOrEmpty()) null else File(filePath)
        return encryptMD5File(file)
    }

    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值转十六进制字符串
     */
    fun encryptMD5FileToHexString(filePath: String?): String {
        val file = if (filePath.isNullOrEmpty()) null else File(filePath)
        return encryptMD5FileToHexString(file)
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值转十六进制字符串
     */
    fun encryptMD5FileToHexString(file: File?): String {
        return ConvertUtils.bytes2HexString(encryptMD5File(file))
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值 byte[]
     */
    fun encryptMD5File(file: File?): ByteArray? {
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
    // =
    /**
     * SHA1 加密
     * @param data 待加密数据
     * @return SHA1 加密后的数据
     */
    fun encryptSHA1(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-1")
    }

    /**
     * SHA1 加密
     * @param data 待加密数据
     * @return SHA1 加密后的数据转十六进制字符串
     */
    fun encryptSHA1ToHexString(data: String?): String? {
        return if (data.isNullOrEmpty()) null else encryptSHA1ToHexString(data.toByteArray())
    }

    /**
     * SHA1 加密
     * @param data 待加密数据
     * @return SHA1 加密后的数据转十六进制字符串
     */
    fun encryptSHA1ToHexString(data: ByteArray?): String {
        return ConvertUtils.bytes2HexString(encryptSHA1(data))
    }
    // =
    /**
     * SHA224 加密
     * @param data 待加密数据
     * @return SHA224 加密后的数据
     */
    fun encryptSHA224(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA224")
    }

    /**
     * SHA224 加密
     * @param data 待加密数据
     * @return SHA224 加密后的数据转十六进制字符串
     */
    fun encryptSHA224ToHexString(data: String?): String? {
        return if (data.isNullOrEmpty()) null else encryptSHA224ToHexString(data.toByteArray())
    }

    /**
     * SHA224 加密
     * @param data 待加密数据
     * @return SHA224 加密后的数据转十六进制字符串
     */
    fun encryptSHA224ToHexString(data: ByteArray?): String {
        return ConvertUtils.bytes2HexString(encryptSHA224(data))
    }
    // =
    /**
     * SHA256 加密
     * @param data 待加密数据
     * @return SHA256 加密后的数据
     */
    fun encryptSHA256(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-256")
    }

    /**
     * SHA256 加密
     * @param data 待加密数据
     * @return SHA256 加密后的数据转十六进制字符串
     */
    fun encryptSHA256ToHexString(data: String?): String? {
        return if (data.isNullOrEmpty()) null else encryptSHA256ToHexString(data.toByteArray())
    }

    /**
     * SHA256 加密
     * @param data 待加密数据
     * @return SHA256 加密后的数据转十六进制
     */
    fun encryptSHA256ToHexString(data: ByteArray?): String {
        return ConvertUtils.bytes2HexString(encryptSHA256(data))
    }
    // =
    /**
     * SHA384 加密
     * @param data 待加密数据
     * @return SHA384 加密后的数据
     */
    fun encryptSHA384(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-384")
    }

    /**
     * SHA384 加密
     * @param data 待加密数据
     * @return SHA384 加密后的数据转十六进制
     */
    fun encryptSHA384ToHexString(data: String?): String? {
        return if (data.isNullOrEmpty()) null else encryptSHA384ToHexString(
            data.toByteArray()
        )
    }

    /**
     * SHA384 加密
     * @param data 待加密数据
     * @return SHA384 加密后的数据转十六进制
     */
    fun encryptSHA384ToHexString(data: ByteArray?): String {
        return ConvertUtils.bytes2HexString(encryptSHA384(data))
    }
    // =
    /**
     * SHA512 加密
     * @param data 待加密数据
     * @return SHA512 加密后的数据
     */
    fun encryptSHA512(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-512")
    }

    /**
     * SHA512 加密
     * @param data 待加密数据
     * @return SHA512 加密后的数据转十六进制
     */
    fun encryptSHA512ToHexString(data: String): String {
        return if (data.isEmpty()) "" else encryptSHA512ToHexString(data.toByteArray())
    }

    /**
     * SHA512 加密
     * @param data 待加密数据
     * @return SHA512 加密后的数据转十六进制
     */
    fun encryptSHA512ToHexString(data: ByteArray): String {
        return ConvertUtils.bytes2HexString(encryptSHA512(data))
    }

    /**
     * Hash 加密模版方法
     * @param data      待加密数据
     * @param algorithm 算法
     * @return 指定加密算法加密后的数据
     */
    fun hashTemplate(data: ByteArray, algorithm: String): ByteArray {
        return if (data.isEmpty()) null else try {
            val digest = MessageDigest.getInstance(algorithm)
            digest.update(data)
            digest.digest()
        } catch (e: Exception) {
            LogUtils.d(e)
            null
        }
    }
    // =
    /**
     * HmacMD5 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacMD5 加密后的数据
     */
    fun encryptHmacMD5(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        return hmacTemplate(data, key, "HmacMD5")
    }

    /**
     * HmacMD5 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacMD5 加密后的数据转十六进制
     */
    fun encryptHmacMD5ToHexString(
        data: String?,
        key: String?
    ): String? {
        return if (data.isNullOrEmpty() || key.isNullOrEmpty()) null else encryptHmacMD5ToHexString(
            data.toByteArray(),
            key.toByteArray()
        )
    }

    /**
     * HmacMD5 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacMD5 加密后的数据转十六进制
     */
    fun encryptHmacMD5ToHexString(
        data: ByteArray?,
        key: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptHmacMD5(data, key))
    }
    // =
    /**
     * HmacSHA1 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA1 加密后的数据
     */
    fun encryptHmacSHA1(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA1")
    }

    /**
     * HmacSHA1 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA1 加密后的数据转十六进制
     */
    fun encryptHmacSHA1ToHexString(
        data: String?,
        key: String?
    ): String? {
        return if (data.isNullOrEmpty() || key.isNullOrEmpty()) null else encryptHmacSHA1ToHexString(
            data.toByteArray(),
            key.toByteArray()
        )
    }

    /**
     * HmacSHA1 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA1 加密后的数据转十六进制
     */
    fun encryptHmacSHA1ToHexString(
        data: ByteArray?,
        key: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptHmacSHA1(data, key))
    }
    // =
    /**
     * HmacSHA224 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA224 加密后的数据
     */
    fun encryptHmacSHA224(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA224")
    }

    /**
     * HmacSHA224 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA224 加密后的数据转十六进制
     */
    fun encryptHmacSHA224ToHexString(
        data: String?,
        key: String?
    ): String? {
        return if (data.isNullOrEmpty() || key.isNullOrEmpty()) null else encryptHmacSHA224ToHexString(
            data.toByteArray(),
            key.toByteArray()
        )
    }

    /**
     * HmacSHA224 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA224 加密后的数据转十六进制
     */
    fun encryptHmacSHA224ToHexString(
        data: ByteArray?,
        key: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptHmacSHA224(data, key))
    }
    // =
    /**
     * HmacSHA256 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA256 加密后的数据
     */
    fun encryptHmacSHA256(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA256")
    }

    /**
     * HmacSHA256 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA256 加密后的数据转十六进制
     */
    fun encryptHmacSHA256ToHexString(
        data: String?,
        key: String?
    ): String? {
        return if (data.isNullOrEmpty() || key.isNullOrEmpty()) null else encryptHmacSHA256ToHexString(
            data.toByteArray(),
            key.toByteArray()
        )
    }

    /**
     * HmacSHA256 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA256 加密后的数据转十六进制
     */
    fun encryptHmacSHA256ToHexString(
        data: ByteArray?,
        key: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptHmacSHA256(data, key))
    }
    // =
    /**
     * HmacSHA384 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA384 加密后的数据
     */
    fun encryptHmacSHA384(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA384")
    }

    /**
     * HmacSHA384 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA384 加密后的数据转十六进制
     */
    fun encryptHmacSHA384ToHexString(
        data: String?,
        key: String?
    ): String? {
        return if (data.isNullOrEmpty() || key.isNullOrEmpty()) null else encryptHmacSHA384ToHexString(
            data.toByteArray(),
            key.toByteArray()
        )
    }

    /**
     * HmacSHA384 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA384 加密后的数据转十六进制
     */
    fun encryptHmacSHA384ToHexString(
        data: ByteArray?,
        key: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptHmacSHA384(data, key))
    }
    // =
    /**
     * HmacSHA512 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA512 加密后的数据
     */
    fun encryptHmacSHA512(
        data: ByteArray?,
        key: ByteArray?
    ): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA512")
    }

    /**
     * HmacSHA512 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA512 加密后的数据转十六进制
     */
    fun encryptHmacSHA512ToHexString(
        data: String?,
        key: String?
    ): String? {
        return if (data.isNullOrEmpty() || key.isNullOrEmpty()) null else encryptHmacSHA512ToHexString(
            data.toByteArray(),
            key.toByteArray()
        )
    }

    /**
     * HmacSHA512 加密
     * @param data 待加密数据
     * @param key  密钥
     * @return HmacSHA512 加密后的数据转十六进制
     */
    fun encryptHmacSHA512ToHexString(
        data: ByteArray?,
        key: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptHmacSHA512(data, key))
    }

    /**
     * Hmac 加密模版方法
     * @param data      待加密数据
     * @param key       密钥
     * @param algorithm 算法
     * @return 指定加密算法和密钥, 加密后的数据
     */
    fun hmacTemplate(
        data: ByteArray?,
        key: ByteArray?,
        algorithm: String?
    ): ByteArray? {
        return if (data == null || data.isEmpty() || key == null || key.size == 0) null else try {
            val secretKey = SecretKeySpec(key, algorithm)
            val mac = Mac.getInstance(algorithm)
            mac.init(secretKey)
            mac.doFinal(data)
        } catch (e: Exception) {
            LogUtils.d(e)
            null
        }
    }
    // =
    /**
     * DES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return DES 加密后的数据
     */
    fun encryptDES(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DES", transformation, iv, true)
    }

    /**
     * DES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return DES 加密后的数据转 Base64
     */
    fun encryptDESToBase64(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return base64Encode(encryptDES(data, key, transformation, iv))
    }

    /**
     * DES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return DES 加密后的数据转十六进制
     */
    fun encryptDESToHexString(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptDES(data, key, transformation, iv))
    }
    // =
    /**
     * DES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return DES 解密后的数据
     */
    fun decryptDES(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DES", transformation, iv, false)
    }

    /**
     * DES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return Base64 解码后, 在进行 DES 解密后的数据
     */
    fun decryptDESToBase64(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return decryptDES(base64Decode(data), key, transformation, iv)
    }

    /**
     * DES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return 十六进制转换后, 在进行 DES 解密后的数据
     */
    fun decryptDESToHexString(
        data: String?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return decryptDES(ConvertUtils.hexString2Bytes(data), key, transformation, iv)
    }
    // =
    /**
     * 3DES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return 3DES 加密后的数据
     */
    fun encrypt3DES(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DESede", transformation, iv, true)
    }

    /**
     * 3DES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return 3DES 加密后的数据转 Base64
     */
    fun encrypt3DESToBase64(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return base64Encode(encrypt3DES(data, key, transformation, iv))
    }

    /**
     * 3DES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return 3DES 加密后的数据转十六进制
     */
    fun encrypt3DESToHexString(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encrypt3DES(data, key, transformation, iv))
    }
    // =
    /**
     * 3DES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return 3DES 解密后的数据
     */
    fun decrypt3DES(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DESede", transformation, iv, false)
    }

    /**
     * 3DES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return Base64 解码后, 在进行 3DES 解密后的数据
     */
    fun decrypt3DESToBase64(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return decrypt3DES(base64Decode(data), key, transformation, iv)
    }

    /**
     * 3DES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return 十六进制转换后, 在进行 3DES 解密后的数据
     */
    fun decrypt3DESToHexString(
        data: String?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return decrypt3DES(ConvertUtils.hexString2Bytes(data), key, transformation, iv)
    }
    // =
    /**
     * AES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return AES 加密后的数据
     */
    fun encryptAES(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "AES", transformation, iv, true)
    }

    /**
     * AES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return AES 加密后的数据转 Base64
     */
    fun encryptAESToBase64(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return base64Encode(encryptAES(data, key, transformation, iv))
    }

    /**
     * AES 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return AES 加密后的数据转十六进制
     */
    fun encryptAESToHexString(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): String {
        return ConvertUtils.bytes2HexString(encryptAES(data, key, transformation, iv))
    }
    // =
    /**
     * AES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return AES 解密后的数据
     */
    fun decryptAES(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "AES", transformation, iv, false)
    }

    /**
     * AES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return Base64 解码后, 在进行 AES 解密后的数据
     */
    fun decryptAESToBase64(
        data: ByteArray?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return decryptAES(base64Decode(data), key, transformation, iv)
    }

    /**
     * AES 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @return 十六进制转换后, 在进行 AES 解密后的数据
     */
    fun decryptAESToHexString(
        data: String?,
        key: ByteArray?,
        transformation: String?,
        iv: ByteArray?
    ): ByteArray? {
        return decryptAES(ConvertUtils.hexString2Bytes(data), key, transformation, iv)
    }

    /**
     * 对称加密模版方法
     * @param data           待加解密数据
     * @param key            密钥
     * @param algorithm      算法
     * @param transformation [Cipher.getInstance] transformation
     * @param iv             算法参数 [AlgorithmParameterSpec]
     * @param isEncrypt      是否加密处理
     * @return 指定加密算法, 加解密后的数据
     */
    fun symmetricTemplate(
        data: ByteArray?,
        key: ByteArray?,
        algorithm: String,
        transformation: String?,
        iv: ByteArray?,
        isEncrypt: Boolean
    ): ByteArray? {
        return if (data == null || data.isEmpty() || key == null || key.isEmpty()) null else try {
            val secretKey: SecretKey = if ("DES" == algorithm) {
                val desKey = DESKeySpec(key)
                val keyFactory = SecretKeyFactory.getInstance(algorithm)
                keyFactory.generateSecret(desKey)
            } else {
                SecretKeySpec(key, algorithm)
            }
            val cipher = Cipher.getInstance(transformation)
            if (iv == null || iv.isEmpty()) {
                cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, secretKey)
            } else {
                val params: AlgorithmParameterSpec = IvParameterSpec(iv)
                cipher.init(
                    if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE,
                    secretKey,
                    params
                )
            }
            cipher.doFinal(data)
        } catch (e: Exception) {
            LogUtils.d(e)
            null
        }
    }
    // =
    /**
     * RSA 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param isPublicKey    `true` [X509EncodedKeySpec], `false` [PKCS8EncodedKeySpec]
     * @param transformation [Cipher.getInstance] transformation
     * @return RSA 加密后的数据
     */
    fun encryptRSA(
        data: ByteArray?,
        key: ByteArray?,
        isPublicKey: Boolean,
        transformation: String?
    ): ByteArray? {
        return rsaTemplate(data, key, isPublicKey, transformation, true)
    }

    /**
     * RSA 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param isPublicKey    `true` [X509EncodedKeySpec], `false` [PKCS8EncodedKeySpec]
     * @param transformation [Cipher.getInstance] transformation
     * @return RSA 加密后的数据转 Base64
     */
    fun encryptRSAToBase64(
        data: ByteArray?,
        key: ByteArray?,
        isPublicKey: Boolean,
        transformation: String?
    ): ByteArray? {
        return base64Encode(encryptRSA(data, key, isPublicKey, transformation))
    }

    /**
     * RSA 加密
     * @param data           待加密数据
     * @param key            密钥
     * @param isPublicKey    `true` [X509EncodedKeySpec], `false` [PKCS8EncodedKeySpec]
     * @param transformation [Cipher.getInstance] transformation
     * @return RSA 加密后的数据转十六进制
     */
    fun encryptRSAToHexString(
        data: ByteArray?,
        key: ByteArray?,
        isPublicKey: Boolean,
        transformation: String?
    ): String {
        return ConvertUtils.bytes2HexString(encryptRSA(data, key, isPublicKey, transformation))
    }
    // =
    /**
     * RSA 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param isPublicKey    `true` [X509EncodedKeySpec], `false` [PKCS8EncodedKeySpec]
     * @param transformation [Cipher.getInstance] transformation
     * @return RSA 解密后的数据
     */
    fun decryptRSA(
        data: ByteArray?,
        key: ByteArray?,
        isPublicKey: Boolean,
        transformation: String?
    ): ByteArray? {
        return rsaTemplate(data, key, isPublicKey, transformation, false)
    }

    /**
     * RSA 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param isPublicKey    `true` [X509EncodedKeySpec], `false` [PKCS8EncodedKeySpec]
     * @param transformation [Cipher.getInstance] transformation
     * @return Base64 解码后, 在进行 RSA 解密后的数据
     */
    fun decryptRSAToBase64(
        data: ByteArray?,
        key: ByteArray?,
        isPublicKey: Boolean,
        transformation: String?
    ): ByteArray? {
        return decryptRSA(base64Decode(data), key, isPublicKey, transformation)
    }

    /**
     * RSA 解密
     * @param data           待解密数据
     * @param key            密钥
     * @param isPublicKey    `true` [X509EncodedKeySpec], `false` [PKCS8EncodedKeySpec]
     * @param transformation [Cipher.getInstance] transformation
     * @return 十六进制转换后, 在进行 RSA 解密后的数据
     */
    fun decryptRSAToHexString(
        data: String?,
        key: ByteArray?,
        isPublicKey: Boolean,
        transformation: String?
    ): ByteArray? {
        return decryptRSA(ConvertUtils.hexString2Bytes(data), key, isPublicKey, transformation)
    }

    /**
     * RSA 加解密模版方法
     * @param data           待加解密数据
     * @param key            密钥
     * @param isPublicKey    `true` [X509EncodedKeySpec], `false` [PKCS8EncodedKeySpec]
     * @param transformation [Cipher.getInstance] transformation
     * @param isEncrypt      是否加密处理
     * @return 指定加密算法, 加解密后的数据
     */
    fun rsaTemplate(
        data: ByteArray?,
        key: ByteArray?,
        isPublicKey: Boolean,
        transformation: String?,
        isEncrypt: Boolean
    ): ByteArray? {
        if (data == null || key == null) return null
        try {
            val dataLength = data.size
            val keyLength = key.size
            if (dataLength == 0 || keyLength == 0) return null
            val rsaKey: Key? = if (isPublicKey) {
                val keySpec = X509EncodedKeySpec(key)
                KeyFactory.getInstance("RSA").generatePublic(keySpec)
            } else {
                val keySpec = PKCS8EncodedKeySpec(key)
                KeyFactory.getInstance("RSA").generatePrivate(keySpec)
            }
            if (rsaKey == null) return null
            val cipher = Cipher.getInstance(transformation)
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, rsaKey)
            val maxLen = if (isEncrypt) 117 else 128
            val count = dataLength / maxLen
            return if (count > 0) {
                var ret: ByteArray? = ByteArray(0)
                var buffer = ByteArray(maxLen)
                var index = 0
                for (i in 0 until count) {
                    System.arraycopy(data, index, buffer, 0, maxLen)
                    ret = ArrayUtils.add(ret, cipher.doFinal(buffer))
                    index += maxLen
                }
                if (index != dataLength) {
                    val restLen = dataLength - index
                    buffer = ByteArray(restLen)
                    System.arraycopy(data, index, buffer, 0, restLen)
                    ret = ArrayUtils.add(ret, cipher.doFinal(buffer))
                }
                ret
            } else {
                cipher.doFinal(data)
            }
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }
    // ===========
    // = 私有方法 =
    // ===========
    /**
     * Base64 编码
     * @param input 待编码数据
     * @return Base64 编码后的 byte[]
     */
    private fun base64Encode(input: ByteArray?): ByteArray? {
        return if (input == null) null else Base64.encode(input, Base64.NO_WRAP)
    }

    /**
     * Base64 解码
     * @param input 待解码数据
     * @return Base64 解码后的 byte[]
     */
    private fun base64Decode(input: ByteArray?): ByteArray? {
        return if (input == null) null else Base64.decode(input, Base64.NO_WRAP)
    }
}