package me.ibore.utils

import java.util.*
import java.util.regex.Pattern

/**
 * detail: 校验工具类
 * @author Ttt
 */
object ValidatorUtils {

    // 正则表达式: 空格
    const val REGEX_SPACE = "\\s"

    // 正则表达式: 验证数字
    const val REGEX_NUMBER = "^[0-9]*$"

    // 正则表达式: 验证数字或包含小数点
    const val REGEX_NUMBER_OR_DECIMAL = "^[0-9]*[.]?[0-9]*$"

    // 正则表达式: 验证是否包含数字
    const val REGEX_CONTAIN_NUMBER = ".*\\d+.*"

    // 正则表达式: 验证是否数字或者字母
    const val REGEX_NUMBER_OR_LETTER = "^[A-Za-z0-9]+$"

    // 正则表达式: 验证是否全是字母
    const val REGEX_LETTER = "^[A-Za-z]+$"

    // 正则表达式: 不能输入特殊字符 ^[\u4E00-\u9FA5A-Za-z0-9]+$ 或 ^[\u4E00-\u9FA5A-Za-z0-9]{2,20}$
    const val REGEX_SPECIAL = "^[\\u4E00-\\u9FA5A-Za-z0-9]+$"

    // 正则表达式: 验证微信号 ^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}+$
    const val REGEX_WEIXIN = "^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}+$"

    // 正则表达式: 验证真实姓名 ^[\u4e00-\u9fa5]+(·[\u4e00-\u9fa5]+)*$
    const val REGEX_REALNAME =
        "^[\\u4e00-\\u9fa5]+(•[\\u4e00-\\u9fa5]*)*$|^[\\u4e00-\\u9fa5]+(·[\\u4e00-\\u9fa5]*)*$"

    // 正则表达式: 验证昵称
    const val REGEX_NICKNAME = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$"

    // 正则表达式: 验证用户名 ( 不包含中文和特殊字符 ) 如果用户名使用手机号码或邮箱 则结合手机号验证和邮箱验证
    const val REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$"

    // 正则表达式: 验证密码 ( 不包含特殊字符 )
    const val REGEX_PASSWORD = "^[a-zA-Z0-9]{6,18}$"

    // 正则表达式: 验证邮箱
    const val REGEX_EMAIL =
        "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"

    // 正则表达式: 验证 URL
    const val REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?"

    // 正则表达式: 验证 IP 地址
    const val REGEX_IP_ADDRESS =
        "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})"

    /**
     * 通用匹配函数
     * @param regex 正则表达式
     * @param input 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun match(
        regex: String,
        input: String?
    ): Boolean {
        if (!input.isNullOrEmpty()) {
            try {
                return Pattern.matches(regex, input)
            } catch (e: Exception) {
                LogUtils.d(e)
            }
        }
        return false
    }
    // =
    /**
     * 检验数字
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isNumber(str: String?): Boolean {
        return match(REGEX_NUMBER, str)
    }

    /**
     * 检验数字或包含小数点
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isNumberDecimal(str: String?): Boolean {
        return match(REGEX_NUMBER_OR_DECIMAL, str)
    }

    /**
     * 判断字符串是不是全是字母
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isLetter(str: String?): Boolean {
        return match(REGEX_LETTER, str)
    }

    /**
     * 判断字符串是不是包含数字
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isContainNumber(str: String?): Boolean {
        return match(REGEX_CONTAIN_NUMBER, str)
    }

    /**
     * 判断字符串是不是只含字母和数字
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isNumberLetter(str: String?): Boolean {
        return match(REGEX_NUMBER_OR_LETTER, str)
    }

    /**
     * 检验特殊符号
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isSpecial(str: String?): Boolean {
        return match(REGEX_SPECIAL, str)
    }

    /**
     * 检验微信号
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isWeixin(str: String?): Boolean {
        return match(REGEX_WEIXIN, str)
    }

    /**
     * 检验真实姓名
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isRealName(str: String?): Boolean {
        return match(REGEX_REALNAME, str)
    }

    /**
     * 校验昵称
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isNickName(str: String?): Boolean {
        return match(REGEX_NICKNAME, str)
    }

    /**
     * 校验用户名
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isUserName(str: String?): Boolean {
        return match(REGEX_USERNAME, str)
    }

    /**
     * 校验密码
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isPassword(str: String?): Boolean {
        return match(REGEX_PASSWORD, str)
    }

    /**
     * 校验邮箱
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isEmail(str: String?): Boolean {
        return match(REGEX_EMAIL, str)
    }

    /**
     * 校验 URL
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isUrl(str: String): Boolean {
        return if (!StringUtils.isEmpty(str)) {
            match(REGEX_URL, str.toLowerCase(Locale.getDefault()))
        } else false
    }

    /**
     * 校验 IP 地址
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isIPAddress(str: String?): Boolean {
        return match(REGEX_IP_ADDRESS, str)
    }

    // 正则表达式: 验证汉字
    const val REGEX_CHINESE = "^[\u4e00-\u9fa5]+$"

    // 正则表达式: 验证汉字 ( 含双角符号 )
    const val REGEX_CHINESE_ALL = "^[\u0391-\uFFE5]+$"

    // 正则表达式: 验证汉字 ( 含双角符号 )
    const val REGEX_CHINESE_ALL2 = "[\u0391-\uFFE5]"

    /**
     * 校验汉字 ( 无符号, 纯汉字 )
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isChinese(str: String?): Boolean {
        return match(REGEX_CHINESE, str)
    }

    /**
     * 判断字符串是不是全是中文
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isChineseAll(str: String?): Boolean {
        return match(REGEX_CHINESE_ALL, str)
    }

    /**
     * 判断字符串中包含中文、包括中文字符标点等
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    fun isContainChinese(str: String?): Boolean {
        if (!StringUtils.isEmpty(str)) {
            try {
                var length = 0
                if (str != null && str.length.also { length = it } != 0) {
                    val dChar = str.toCharArray()
                    for (i in 0 until length) {
                        val flag = dChar[i].toString().matches(Regex(REGEX_CHINESE_ALL2))
                        if (flag) {
                            return true
                        }
                    }
                }
            } catch (e: Exception) {
                LogUtils.d(e)
            }
        }
        return false
    }

}