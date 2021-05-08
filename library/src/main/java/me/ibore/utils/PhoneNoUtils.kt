package me.ibore.utils

object PhoneNoUtils {

    /**
     * 简单手机号码校验 校验手机号码的长度和 1 开头 ( 是否 11 位 )
     */
    const val CHINA_PHONE_PATTERN_SIMPLE = "^(?:\\+86)?1\\d{10}$"

    /**
     * 中国手机号正则
     * 移动: 134 135 136 137 138 139 147 148 150 151 152 157 158 159 165 172 178 182 183 184 187 188 198
     * 联通: 130 131 132 145 146 155 156 166 171 175 176 185 186
     * 电信: 133 149 153 173 174 177 180 181 189 191 199
     * 卫星通信: 1349
     * 虚拟运营商: 170
     */
    const val CHINA_PHONE_PATTERN: String =
        "^13[\\d]{9}$|^14[5,6,7,8,9]{1}\\d{8}$|^15[^4]{1}\\d{8}$|^16[5,6]{1}\\d{8}$|^17[0,1,2,3,4,5,6,7,8]{1}\\d{8}$|^18[\\d]{9}$|^19[1,8,9]{1}\\d{8}$"

    // 中国电信号码正则
    val CHINA_TELECOM_PATTERN: String by lazy {
        // 电信: 133 149 153 173 174 177 180 181 189 191 199
        val builder = StringBuilder()
        builder.append("^13[3]{1}\\d{8}$") // 13 开头
        builder.append("|") // 或
        builder.append("^14[9]{1}\\d{8}$") // 14 开头
        builder.append("|")
        builder.append("^15[3]{1}\\d{8}$") // 15 开头
        builder.append("|")
        builder.append("^17[3,4,7]{1}\\d{8}$") // 17 开头
        builder.append("|")
        builder.append("^18[0,1,9]{1}\\d{8}$") // 18 开头
        builder.append("|")
        builder.append("^19[1,9]{1}\\d{8}$") // 19 开头
        builder.toString()
    }

    // 中国联通号码正则
    val CHINA_UNICON_PATTERN: String by lazy {
        // 联通: 130 131 132 145 146 155 156 166 171 175 176 185 186
        val builder = StringBuilder()
        builder.append("^13[0,1,2]{1}\\d{8}$") // 13 开头
        builder.append("|") // 或
        builder.append("^14[5,6]{1}\\d{8}$") // 14 开头
        builder.append("|")
        builder.append("^15[5,6]{1}\\d{8}$") // 15 开头
        builder.append("|")
        builder.append("^16[6]{1}\\d{8}$") // 16 开头
        builder.append("|")
        builder.append("^17[1,5,6]{1}\\d{8}$") // 17 开头
        builder.append("|")
        builder.append("^18[5,6]{1}\\d{8}$") // 18 开头
        builder.toString()
    }

    // 中国移动号码正则
    val CHINA_MOBILE_PATTERN: String by lazy {
        // 移动: 134 135 136 137 138 139 147 148 150 151 152 157 158 159 165 172 178 182 183 184 187 188 198
        val builder = StringBuilder()
        builder.append("^13[4,5,6,7,8,9]{1}\\d{8}$") // 13 开头
        builder.append("|") // 或
        builder.append("^14[7,8]{1}\\d{8}$") // 14 开头
        builder.append("|")
        builder.append("^15[0,1,2,7,8,9]{1}\\d{8}$") // 15 开头
        builder.append("|")
        builder.append("^16[5]{1}\\d{8}$") // 16 开头
        builder.append("|")
        builder.append("^17[2,8]{1}\\d{8}$") // 17 开头
        builder.append("|")
        builder.append("^18[2,3,4,7,8]{1}\\d{8}$") // 18 开头
        builder.append("|")
        builder.append("^19[8]{1}\\d{8}$") // 19 开头
        builder.toString()
    }

    /**
     * 香港手机号码正则 香港手机号码 8 位数, 5|6|8|9 开头 + 7 位任意数
     */
    const val HK_PHONE_PATTERN = "^(5|6|8|9)\\d{7}$"

    /**
     * 座机电话格式验证
     */
    const val PHONE_CALL_PATTERN = "^(?:\\(\\d{3,4}\\)|\\d{3,4}-)?\\d{7,8}(?:-\\d{1,4})?$"

    /**
     * 中国手机号格式验证, 在输入可以调用该方法, 点击发送验证码, 使用 isPhone
     * @param phone 待校验的手机号
     * @return `true` yes, `false` no
     */
    fun isPhoneCNSimple(phone: String): Boolean {
        return ValidatorUtils.match(CHINA_PHONE_PATTERN_SIMPLE, phone)
    }

    /**
     * 是否中国手机号
     * @param phone 待校验的手机号
     * @return `true` yes, `false` no
     */
    fun isPhoneCN(phone: String): Boolean {
        return ValidatorUtils.match(CHINA_PHONE_PATTERN, phone)
    }

    /**
     * 是否中国电信手机号码
     * @param phone 待校验的手机号
     * @return `true` yes, `false` no
     */
    fun isPhoneCTCC(phone: String): Boolean {
        return ValidatorUtils.match(CHINA_TELECOM_PATTERN, phone)
    }

    /**
     * 是否中国联通手机号码
     * @param phone 待校验的手机号
     * @return `true` yes, `false` no
     */
    fun isPhoneCUCC(phone: String): Boolean {
        return ValidatorUtils.match(CHINA_UNICON_PATTERN, phone)
    }

    /**
     * 是否中国移动手机号码
     * @param phone 待校验的手机号
     * @return `true` yes, `false` no
     */
    fun isPhoneCMCC(phone: String): Boolean {
        return ValidatorUtils.match(CHINA_MOBILE_PATTERN, phone)
    }

    /**
     * 判断是否香港手机号
     * @param phone 待校验的手机号
     * @return `true` yes, `false` no
     */
    fun isPhoneHkMobile(phone: String): Boolean {
        return ValidatorUtils.match(HK_PHONE_PATTERN, phone)
    }

    /**
     * 验证电话号码的格式
     * @param phone 待校验的号码
     * @return `true` yes, `false` no
     */
    fun isPhoneCallNum(phone: String): Boolean {
        return ValidatorUtils.match(PHONE_CALL_PATTERN, phone)
    }

}