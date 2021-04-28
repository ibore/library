package me.ibore.utils.card

import android.annotation.SuppressLint
import me.ibore.utils.LogUtils
import me.ibore.utils.StringUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 居民身份证工具类
 */
object IDCardUtils {

    // 加权因子
    private val POWER = intArrayOf(
        7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2
    )

    // 身份证最少位数
    const val CHINA_ID_MIN_LENGTH = 15

    // 身份证最大位数
    const val CHINA_ID_MAX_LENGTH = 18

    // 省份编码
    private val sCityCodeMaps: MutableMap<String, String?> = HashMap()

    // 台湾身份首字母对应数字
    private val sTWFirstCodeMaps: MutableMap<String, Int> = HashMap()

    // 香港身份首字母对应数字
    private val sHKFirstCodeMaps: MutableMap<String, Int> = HashMap()

    /**
     * 身份证校验规则, 验证 15 位身份编码是否合法
     * @param idCard 待验证身份证号码
     * @return `true` yes, `false` no
     */
    @SuppressLint("SimpleDateFormat")
    fun validate15(idCard: String): Boolean {
        // 属于数字, 并且长度为 15 位数
        if (isNumber(idCard) && idCard.length == CHINA_ID_MIN_LENGTH) {
            // 获取省份编码
            val provinceCode = idCard.substring(0, 2)
            if (sCityCodeMaps[provinceCode] == null) return false
            // 获取出生日期
            val birthCode = idCard.substring(6, 12)
            var birthDate: Date? = null
            try {
                birthDate = SimpleDateFormat("yy").parse(birthCode.substring(0, 2))
            } catch (e: ParseException) {
                LogUtils.e(e, "validateIdCard15")
            }
            val calendar = Calendar.getInstance()
            if (birthDate != null) calendar.time = birthDate
            // 判断是否有效日期
            return validateDateSmallerThenNow(
                calendar[Calendar.YEAR],
                Integer.valueOf(birthCode.substring(2, 4)),
                Integer.valueOf(birthCode.substring(4, 6))
            )
        }
        return false
    }

    /**
     * 身份证校验规则, 验证 18 位身份编码是否合法
     * @param idCard 待验证身份证号码
     * @return `true` yes, `false` no
     */
    fun validate18(idCard: String?): Boolean {
        if (idCard != null && idCard.length == CHINA_ID_MAX_LENGTH) {
            // 前 17 位
            val code17 = idCard.substring(0, 17)
            // 第 18 位
            val code18 = idCard.substring(17, CHINA_ID_MAX_LENGTH)
            // 判断前 17 位是否数字
            if (isNumber(code17)) {
                try {
                    val cardArys = convertCharToInt(code17.toCharArray())
                    val sum17 = getPowerSum(cardArys)
                    // 获取校验位
                    val str = getCheckCode18(sum17)
                    // 判断最后一位是否一样
                    if (str.isNotEmpty() && str.equals(code18, ignoreCase = true)) {
                        return true
                    }
                } catch (e: Exception) {
                    LogUtils.d(e)
                }
            }
        }
        return false
    }

    /**
     * 将 15 位身份证号码转换为 18 位
     * @param idCard 15 位身份编码
     * @return 18 位身份编码
     */
    @SuppressLint("SimpleDateFormat")
    fun convert15CardTo18(idCard: String): String? {
        // 属于数字, 并且长度为 15 位数
        if (isNumber(idCard) && idCard.length == CHINA_ID_MIN_LENGTH) {
            val idCard18: String
            var birthDate: Date? = null
            // 获取出生日期
            val birthday = idCard.substring(6, 12)
            try {
                birthDate = SimpleDateFormat("yyMMdd").parse(birthday)
            } catch (e: ParseException) {
                LogUtils.d(e)
            }
            val calendar = Calendar.getInstance()
            if (birthDate != null) calendar.time = birthDate
            try {
                // 获取出生年 ( 完全表现形式, 如: 2010)
                val year = calendar[Calendar.YEAR].toString()
                // 保存省市区信息 + 年 + 月日 + 后续信息 ( 顺序位、性别等 )
                idCard18 = idCard.substring(0, 6) + year + idCard.substring(8)
                // 转换字符数组
                val cardArys = convertCharToInt(idCard18.toCharArray())
                val sum17 = getPowerSum(cardArys)
                // 获取校验位
                val str = getCheckCode18(sum17)
                // 判断长度, 拼接校验位
                return if (str.isNotEmpty()) idCard18 + str else null
            } catch (e: Exception) {
                LogUtils.d(e)
            }
        }
        return null
    }

    /**
     * 验证台湾身份证号码
     * @param idCard 身份证号码
     * @return `true` yes, `false` no
     */
    fun validateTW(idCard: String?): Boolean {
        // 台湾身份证 10 位
        if (idCard == null || idCard.length != 10) return false
        try {
            // 第一位英文 不同县市
            val start = idCard.substring(0, 1)
            val mid = idCard.substring(1, 9)
            val end = idCard.substring(9, 10)
            val iStart = sTWFirstCodeMaps[start]
            var sum = iStart!! / 10 + iStart % 10 * 9
            val chars = mid.toCharArray()
            var iflag = 8
            for (c in chars) {
                sum += Integer.valueOf(c.toString()) * iflag
                iflag--
            }
            return (if (sum % 10 == 0) 0 else 10 - sum % 10) == Integer.valueOf(end)
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return false
    }

    /**
     * 验证香港身份证号码 ( 部份特殊身份证无法检查 )
     * 身份证前 2 位为英文字符, 如果只出现一个英文字符则表示第一位是空格, 对应数字 58 前 2 位英文字符 A-Z 分别对应数字 10-35
     * 最后一位校验码为 0-9 的数字加上字符 "A", "A" 代表 10
     * 将身份证号码全部转换为数字, 分别对应乘 9-1 相加的总和, 整除 11 则证件号码有效
     * @param idCard 身份证号码
     * @return `true` yes, `false` no
     */
    fun validateHK(idCard: String): Boolean {
        if (StringUtils.isEmpty(idCard)) return false
        try {
            var card = idCard.replace("[\\(|\\)]".toRegex(), "")
            var sum: Int
            if (card.length == 9) {
                sum = (card.substring(0, 1).toUpperCase().toCharArray()[0]
                    .toInt() - 55) * 9 + (card.substring(1, 2).toUpperCase()
                    .toCharArray()[0].toInt() - 55) * 8
                card = card.substring(1, 9)
            } else {
                sum = 522 + (card.substring(0, 1).toUpperCase().toCharArray()[0].toInt() - 55) * 8
            }
            val mid = card.substring(1, 7)
            val end = card.substring(7, 8)
            val chars = mid.toCharArray()
            var iflag = 7
            for (c in chars) {
                sum += Integer.valueOf(c.toString()) * iflag
                iflag--
            }
            sum = if (end.equals("A", ignoreCase = true)) {
                sum + 10
            } else {
                sum + Integer.valueOf(end)
            }
            return sum % 11 == 0
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return false
    }

    /**
     * 判断 10 位数的身份证号, 是否合法
     * @param idCard 身份证号码
     * @return `true` yes, `false` no
     */
    fun validateIdCard10(idCard: String): Array<String?>? {
        if (idCard.isNullOrBlank()) return null
        val info = arrayOfNulls<String>(3)
        info[0] = "N" // 默认未知地区
        info[1] = "N" // 默认未知性别
        info[2] = "false" // 默认非法
        try {
            val card = idCard.replace("[\\(|\\)]".toRegex(), "")
            // 获取身份证长度
            val cardLength = card.length
            // 属于 8, 9, 10 长度范围内
            if (cardLength >= 8 || cardLength <= 10) {
                if (idCard.matches(Regex("^[a-zA-Z][0-9]{9}$"))) { // 台湾
                    info[0] = "台湾"
                    val char2 = idCard.substring(1, 2)
                    if (char2 == "1") {
                        info[1] = "M"
                    } else if (char2 == "2") {
                        info[1] = "F"
                    } else {
                        info[1] = "N"
                        info[2] = "false"
                        return info
                    }
                    info[2] = if (validateTW(idCard)) "true" else "false"
                } else if (idCard.matches(Regex("^[1|5|7][0-9]{6}\\(?[0-9A-Z]\\)?$"))) { // 澳门
                    info[0] = "澳门"
                    info[1] = "N"
                    // TODO
                } else if (idCard.matches(Regex("^[A-Z]{1,2}[0-9]{6}\\(?[0-9A]\\)?$"))) { // 香港
                    info[0] = "香港"
                    info[1] = "N"
                    info[2] = if (validateHK(idCard)) "true" else "false"
                }
            }
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return info
    }

    /**
     * 验证身份证是否合法
     * @param idCard 身份证号码
     * @return `true` yes, `false` no
     */
    fun validate(idCard: String?): Boolean {
        if (idCard.isNullOrBlank()) return false
        val card = idCard.trim { it <= ' ' }
        if (validate18(card)) return true
        if (validate15(card)) return true
        val cardArys = validateIdCard10(card)
        return cardArys != null && "true" == cardArys[2]
    }

    /**
     * 根据身份编号获取年龄
     * @param idCard 身份编号
     * @return 年龄
     */
    fun getAge(idCard: String?): Int {
        if (idCard.isNullOrBlank()) return 0
        try {
            var idCardStr: String? = idCard
            // 属于 15 位身份证, 则转换为 18 位
            if (idCardStr!!.length == CHINA_ID_MIN_LENGTH) {
                idCardStr = convert15CardTo18(idCard)
            }
            // 属于 18 位身份证才处理
            if (idCardStr!!.length == CHINA_ID_MAX_LENGTH) {
                val year = idCardStr.substring(6, 10)
                // 获取当前年份
                val currentYear = Calendar.getInstance()[Calendar.YEAR]
                // 当前年份 ( 出生年份 )
                return currentYear - Integer.valueOf(year)
            }
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return 0
    }

    /**
     * 根据身份编号获取生日
     * @param idCard 身份编号
     * @return 生日 (yyyyMMdd)
     */
    fun getBirth(idCard: String?): String? {
        if (idCard.isNullOrBlank()) return null
        try {
            var idCardStr: String? = idCard
            // 属于 15 位身份证, 则转换为 18 位
            if (idCardStr!!.length == CHINA_ID_MIN_LENGTH) {
                idCardStr = convert15CardTo18(idCard)
            }
            // 属于 18 位身份证才处理
            if (idCardStr!!.length == CHINA_ID_MAX_LENGTH) {
                return idCardStr.substring(6, 14)
            }
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * 根据身份编号获取生日
     * @param idCard 身份编号
     * @return 生日 (yyyyMMdd)
     */
    fun getBirthday(idCard: String): String? {
        // 获取生日
        val birth = getBirth(idCard)
        // 进行处理
        if (birth != null) {
            try {
                return birth.replace("(\\d{4})(\\d{2})(\\d{2})".toRegex(), "$1-$2-$3")
            } catch (e: Exception) {
                LogUtils.d(e)
            }
        }
        return null
    }

    /**
     * 根据身份编号获取生日 ( 年份 )
     * @param idCard 身份编号
     * @return 生日 (yyyy)
     */
    fun getYear(idCard: String): String? {
        // 获取生日
        val birth = getBirth(idCard)
        // 进行处理
        if (birth != null) {
            try {
                return birth.substring(0, 4)
            } catch (e: Exception) {
                LogUtils.d(e)
            }
        }
        return null
    }

    /**
     * 根据身份编号获取生日 ( 月份 )
     * @param idCard 身份编号
     * @return 生日 (MM)
     */
    fun getMonth(idCard: String): String? {
        // 获取生日
        val birth = getBirth(idCard)
        // 进行处理
        if (birth != null) {
            try {
                return birth.substring(4, 6)
            } catch (e: Exception) {
                LogUtils.d(e)
            }
        }
        return null
    }

    /**
     * 根据身份编号获取生日 ( 天数 )
     * @param idCard 身份编号
     * @return 生日 (dd)
     */
    fun getDay(idCard: String): String? {
        // 获取生日
        val birth = getBirth(idCard)
        // 进行处理
        if (birth != null) {
            try {
                return birth.substring(6, 8)
            } catch (e: Exception) {
                LogUtils.d(e)
            }
        }
        return null
    }

    /**
     * 根据身份编号获取性别
     * @param idCard 身份编号
     * @return 性别 男 (M)、女 (F)、未知 (N)
     */
    fun getGender(idCard: String?): String? {
        if (idCard.isNullOrBlank()) return null
        try {
            var idCardStr: String? = idCard
            // 属于 15 位身份证, 则转换为 18 位
            if (idCardStr!!.length == CHINA_ID_MIN_LENGTH) {
                idCardStr = convert15CardTo18(idCard)
            }
            // 属于 18 位身份证才处理
            if (idCardStr!!.length == CHINA_ID_MAX_LENGTH) {
                // 获取第 17 位性别信息
                val cardNumber = idCardStr.substring(16, 17)
                // 奇数为男, 偶数为女
                return if (cardNumber.toInt() % 2 == 0) "F" else "M"
            }
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        // 默认未知
        return "N"
    }

    /**
     * 根据身份编号获取户籍省份
     * @param idCard 身份编码
     * @return 省级编码
     */
    fun getProvince(idCard: String?): String? {
        if (idCard.isNullOrBlank()) return null
        try {
            // 身份证长度
            val idCardLength = idCard.length
            // 属于 15 位身份证、或 18 位身份证
            if (idCardLength == CHINA_ID_MIN_LENGTH || idCardLength == CHINA_ID_MAX_LENGTH) {
                return sCityCodeMaps[idCard.substring(0, 2)]
            }
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后, 再获取和值
     * @param data byte[] 数据
     * @return 身份证编码, 加权引子
     */
    fun getPowerSum(data: IntArray?): Int {
        if (data == null) return 0
        val len = data.size
        if (len == 0) return 0
        val powerLength = POWER.size
        var sum = 0
        if (powerLength == len) {
            for (i in 0 until len) {
                for (j in 0 until powerLength) {
                    if (i == j) {
                        sum += data[i] * POWER[j]
                    }
                }
            }
        }
        return sum
    }

    /**
     * 将 POWER 和值与 11 取模获取余数进行校验码判断
     * @param sum [IDCardUtils.getPowerSum]
     * @return 校验位
     */
    fun getCheckCode18(sum: Int): String {
        var code = ""
        when (sum % 11) {
            10 -> code = "2"
            9 -> code = "3"
            8 -> code = "4"
            7 -> code = "5"
            6 -> code = "6"
            5 -> code = "7"
            4 -> code = "8"
            3 -> code = "9"
            2 -> code = "x"
            1 -> code = "0"
            0 -> code = "1"
        }
        return code
    }
    // ===========
    // = 私有方法 =
    // ===========
    /**
     * 将字符数组转换成数字数组
     * @param data char[]
     * @return int[]
     */
    private fun convertCharToInt(data: CharArray?): IntArray? {
        if (data == null) return null
        val len = data.size
        if (len == 0) return null
        try {
            val arrays = IntArray(len)
            for (i in 0 until len) {
                arrays[i] = data[i].toString().toInt()
            }
            return arrays
        } catch (e: Exception) {
            LogUtils.d(e)
        }
        return null
    }

    /**
     * 验证小于当前日期 是否有效
     * @param yearData  待校验的日期 ( 年 )
     * @param monthData 待校验的日期 ( 月 1-12)
     * @param dayData   待校验的日期 ( 日 )
     * @return `true` yes, `false` no
     */
    private fun validateDateSmallerThenNow(
        yearData: Int,
        monthData: Int,
        dayData: Int
    ): Boolean {
        val year = Calendar.getInstance()[Calendar.YEAR]
        val datePerMonth: Int
        val MIN = 1930
        if (yearData < MIN || yearData >= year) {
            return false
        }
        if (monthData < 1 || monthData > 12) {
            return false
        }
        datePerMonth = when (monthData) {
            4, 6, 9, 11 -> 30
            2 -> {
                val dm =
                    (yearData % 4 == 0 && yearData % 100 != 0 || yearData % 400 == 0) && yearData > MIN && yearData < year
                if (dm) 29 else 28
            }
            else -> 31
        }
        return dayData in 1..datePerMonth
    }

    /**
     * 检验数字
     * @param str 待校验的字符串
     * @return `true` yes, `false` no
     */
    private fun isNumber(str: String): Boolean {
        return str.isNotEmpty() && str.matches(Regex("^[0-9]*$"))
    }

    init {
        sCityCodeMaps["11"] = "北京"
        sCityCodeMaps["12"] = "天津"
        sCityCodeMaps["13"] = "河北"
        sCityCodeMaps["14"] = "山西"
        sCityCodeMaps["15"] = "内蒙古"
        sCityCodeMaps["21"] = "辽宁"
        sCityCodeMaps["22"] = "吉林"
        sCityCodeMaps["23"] = "黑龙江"
        sCityCodeMaps["31"] = "上海"
        sCityCodeMaps["32"] = "江苏"
        sCityCodeMaps["33"] = "浙江"
        sCityCodeMaps["34"] = "安徽"
        sCityCodeMaps["35"] = "福建"
        sCityCodeMaps["36"] = "江西"
        sCityCodeMaps["37"] = "山东"
        sCityCodeMaps["41"] = "河南"
        sCityCodeMaps["42"] = "湖北"
        sCityCodeMaps["43"] = "湖南"
        sCityCodeMaps["44"] = "广东"
        sCityCodeMaps["45"] = "广西"
        sCityCodeMaps["46"] = "海南"
        sCityCodeMaps["50"] = "重庆"
        sCityCodeMaps["51"] = "四川"
        sCityCodeMaps["52"] = "贵州"
        sCityCodeMaps["53"] = "云南"
        sCityCodeMaps["54"] = "西藏"
        sCityCodeMaps["61"] = "陕西"
        sCityCodeMaps["62"] = "甘肃"
        sCityCodeMaps["63"] = "青海"
        sCityCodeMaps["64"] = "宁夏"
        sCityCodeMaps["65"] = "新疆"
        sCityCodeMaps["71"] = "台湾"
        sCityCodeMaps["81"] = "香港"
        sCityCodeMaps["82"] = "澳门"
        sCityCodeMaps["83"] = "台湾"
        sCityCodeMaps["91"] = "国外"
        sTWFirstCodeMaps["A"] = 10
        sTWFirstCodeMaps["B"] = 11
        sTWFirstCodeMaps["C"] = 12
        sTWFirstCodeMaps["D"] = 13
        sTWFirstCodeMaps["E"] = 14
        sTWFirstCodeMaps["F"] = 15
        sTWFirstCodeMaps["G"] = 16
        sTWFirstCodeMaps["H"] = 17
        sTWFirstCodeMaps["J"] = 18
        sTWFirstCodeMaps["K"] = 19
        sTWFirstCodeMaps["L"] = 20
        sTWFirstCodeMaps["M"] = 21
        sTWFirstCodeMaps["N"] = 22
        sTWFirstCodeMaps["P"] = 23
        sTWFirstCodeMaps["Q"] = 24
        sTWFirstCodeMaps["R"] = 25
        sTWFirstCodeMaps["S"] = 26
        sTWFirstCodeMaps["T"] = 27
        sTWFirstCodeMaps["U"] = 28
        sTWFirstCodeMaps["V"] = 29
        sTWFirstCodeMaps["X"] = 30
        sTWFirstCodeMaps["Y"] = 31
        sTWFirstCodeMaps["W"] = 32
        sTWFirstCodeMaps["Z"] = 33
        sTWFirstCodeMaps["I"] = 34
        sTWFirstCodeMaps["O"] = 35
        sHKFirstCodeMaps["A"] = 1
        sHKFirstCodeMaps["B"] = 2
        sHKFirstCodeMaps["C"] = 3
        sHKFirstCodeMaps["R"] = 18
        sHKFirstCodeMaps["U"] = 21
        sHKFirstCodeMaps["Z"] = 26
        sHKFirstCodeMaps["X"] = 24
        sHKFirstCodeMaps["W"] = 23
        sHKFirstCodeMaps["O"] = 15
        sHKFirstCodeMaps["N"] = 14
    }
}