package me.ibore.utils

import android.os.Build

/**
 * detail: 版本工具类
 * @author Ttt
 */
object VersionUtils {

    /**
     * 获取 SDK 版本
     * @return SDK 版本
     */
    @JvmStatic
    val sDKVersion: Int
        get() = Build.VERSION.SDK_INT

    /**
     * 是否在 5.0.1 版本及以上
     * @return 是否在 5.0.1 版本及以上
     */
    @JvmStatic
    val isLollipop: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    /**
     * 是否在 6.0 版本及以上
     * @return 是否在 6.0 版本及以上
     */
    @JvmStatic
    val isM: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * 是否在 7.0 版本及以上
     * @return 是否在 7.0 版本及以上
     */
    @JvmStatic
    val isN: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    /**
     * 是否在 7.1.1 版本及以上
     * @return 是否在 7.1.1 版本及以上
     */
    @JvmStatic
    val isN_MR1: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

    /**
     * 是否在 8.0 版本及以上
     * @return 是否在 8.0 版本及以上
     */
    @JvmStatic
    val isO: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /**
     * 是否在 8.1 版本及以上
     * @return 是否在 8.1 版本及以上
     */
    @JvmStatic
    val isO_MR1: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

    /**
     * 是否在 9.0 版本及以上
     * @return 是否在 9.0 版本及以上
     */
    @JvmStatic
    val isP: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    /**
     * 是否在 10.0 版本及以上
     * @return 是否在 10.0 版本及以上
     */
    @JvmStatic
    val isQ: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * 是否在 11.0 版本及以上
     * @return 是否在 11.0 版本及以上
     */
    @JvmStatic
    val isR: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    /**
     * 转换 SDK 版本 convertSDKVersion(14) = Android 4.0.0-2
     * @param sdkVersion SDK 版本
     * @return SDK 版本
     */
    @JvmOverloads
    fun convertSDKVersion(sdkVersion: Int = Build.VERSION.SDK_INT): String {
        when (sdkVersion) {
            21 -> return "Android 5.0"
            22 -> return "Android 5.1"
            23 -> return "Android 6.0"
            24 -> return "Android 7.0"
            25 -> return "Android 7.1.1"
            26 -> return "Android 8.0"
            27 -> return "Android 8.1"
            28 -> return "Android 9.0"
            29 -> return "Android 10.0"
            30 -> return "Android 11.0"
        }
        return "unknown"
    }
}