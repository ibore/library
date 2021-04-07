//package me.ibore.utils
//
//import android.text.TextUtils
//import androidx.annotation.IntDef
//import dev.utils.DevFinal
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.*
//
///**
// * detail: 日志记录分析工具类
// * @author Ttt
// */
//object LogRecordUtils {
//    // 日志 TAG
//    private val TAG = LogRecordUtils::class.java.simpleName
//    /**
//     * 获取日志文件名
//     * @return 日志文件名
//     */
//    /**
//     * 设置日志文件夹名
//     * @param logFolderName 日志文件夹名
//     */
//    // 日志文件夹名字 ( 目录名 )
//    var logFolderName = "LogRecord"
//    /**
//     * 获取日志存储路径
//     * @return 日志存储路径
//     */
//    /**
//     * 设置日志存储路径
//     * @param logStoragePath 日志存储路径
//     */
//    // 日志存储路径
//    var logStoragePath: String? = null
//    /**
//     * 判断是否处理日志记录
//     * @return `true` yes, `false` no
//     */
//    /**
//     * 设置是否处理日志记录
//     * @param handler 是否处理日志
//     */
//    // 是否处理保存
//    var isHandler = true
//    /**
//     * 判断是否追加空格
//     * @return `true` yes, `false` no
//     */
//    /**
//     * 设置是否追加空格
//     * @param appendSpace 是否追加空格
//     */
//    // 判断是否加空格
//    var isAppendSpace = true
//
//    // 文件记录回调
//    private var RECORD_CALLBACK: Callback? = null
//
//    // ===========
//    // = 配置信息 =
//    // ===========
//    // APP 版本名 ( 主要用于对用户显示版本信息 )
//    private var APP_VERSION_NAME = ""
//
//    // APP 版本号
//    private var APP_VERSION_CODE = ""
//
//    // 应用包名
//    private var PACKAGE_NAME = ""
//
//    // 设备信息
//    private var DEVICE_INFO_STR: String? = null
//
//    // 设备信息存储 Map
//    private val DEVICE_INFO_MAPS: Map<String, String> = HashMap()
//
//    /**
//     * 初始化操作 ( 内部已调用 )
//     */
//    fun init() {
//        // 如果为 null, 才设置
//        if (TextUtils.isEmpty(logStoragePath)) {
//            // 获取根路径
//            logStoragePath = PathUtils.getAppExternal().getAppCachePath()
//        }
//
//        // 如果版本信息为 null, 才进行处理
//        if (TextUtils.isEmpty(APP_VERSION_CODE) || TextUtils.isEmpty(APP_VERSION_NAME)) {
//            // 获取 APP 版本信息
//            val versions: Array<String> = ManifestUtils.getAppVersion()
//            // 防止为 null
//            if (versions != null && versions.size == 2) {
//                // 保存 APP 版本信息
//                APP_VERSION_NAME = versions[0]
//                APP_VERSION_CODE = versions[1]
//            }
//        }
//
//        // 获取包名
//        if (TextUtils.isEmpty(PACKAGE_NAME)) {
//            PACKAGE_NAME = AppUtils.getPackageName()
//        }
//
//        // 判断是否存在设备信息
//        if (DEVICE_INFO_MAPS.size == 0) {
//            // 获取设备信息
//            DeviceUtils.getDeviceInfo(DEVICE_INFO_MAPS)
//            // 转换字符串
//            handlerDeviceInfo("")
//        }
//    }
//
//    /**
//     * 设置文件记录回调
//     * @param callback [Callback]
//     */
//    fun setCallback(callback: Callback?) {
//        RECORD_CALLBACK = callback
//    }
//    // ===========
//    // = 记录方法 =
//    // ===========
//    /**
//     * 日志记录
//     * @param fileInfo [FileInfo]
//     * @param logs     日志内容数组
//     * @return 日志内容
//     */
//    fun record(
//        fileInfo: FileInfo?,
//        vararg logs: String?
//    ): String {
//        // 如果不处理, 则直接跳过
//        if (!isHandler) {
//            return "do not process records"
//        }
//        if (fileInfo != null) {
//            if (!fileInfo.isHandler) {
//                return "file not recorded"
//            }
//            return if (logs != null && logs.size != 0) {
//                saveLogRecord(fileInfo, *logs)
//            } else "no data record"
//            // 无数据记录
//        }
//        // 信息为 null
//        return "fileInfo is null"
//    }
//    // =================
//    // = 判断、获取方法 =
//    // =================
//    // ===========
//    // = 内部方法 =
//    // ===========
//    /**
//     * 最终保存方法
//     * @param fileInfo [FileInfo]
//     * @param logs     日志内容数组
//     * @return 拼接后的日志内容
//     */
//    private fun saveLogRecord(
//        fileInfo: FileInfo?,
//        vararg logs: String
//    ): String {
//        // 如果不处理, 则直接跳过
//        if (!isHandler) return "do not process records"
//        // 文件信息为 null, 则不处理
//        if (fileInfo == null) return "fileInfo is null"
//        // 如果文件地址为 null, 则不处理
//        if (TextUtils.isEmpty(fileInfo.fileName)) return "fileName is null"
//        // 获取文件名
//        val fileName = fileInfo.fileName
//        // 获取文件提示
//        val fileHint = fileInfo.fileFunction
//        return try {
//            // 操作结果
//            val result: Boolean
//            // 获取处理的日志
//            val logContent = splitLog(*logs)
//            // 日志保存路径
//            val logPath = fileInfo.logPath
//            // 获取日志地址
//            val file = File(logPath, fileName)
//            // 判断是否存在
//            result = if (file.exists()) {
//                FileUtils.appendFile(file, StringUtils.getBytes(logContent))
//            } else {
//                // = 首次则保存设备、APP 信息 =
//                val builder = StringBuilder()
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("[设备信息]")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("===========================")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(handlerDeviceInfo("failed to get device information"))
//                builder.append(DevFinal.NEW_LINE_STR)
//                builder.append("===========================")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("[版本信息]")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("===========================")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("versionName: ").append(APP_VERSION_NAME)
//                builder.append(DevFinal.NEW_LINE_STR)
//                builder.append("versionCode: ").append(APP_VERSION_CODE)
//                builder.append(DevFinal.NEW_LINE_STR)
//                builder.append("package: ").append(PACKAGE_NAME)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("===========================")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("[文件信息]")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("===========================")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(fileHint)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("===========================")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("[日志内容]")
//                builder.append(DevFinal.NEW_LINE_STR_X2)
//                builder.append("===========================")
//                // 创建文件夹, 并且进行处理
//                FileUtils.saveFile(file, StringUtils.getBytes(builder.toString()))
//                // 追加内容
//                FileUtils.appendFile(file, StringUtils.getBytes(logContent))
//            }
//            // 触发回调
//            if (RECORD_CALLBACK != null) {
//                RECORD_CALLBACK!!.callback(result, fileInfo, logContent, logPath, fileName, *logs)
//            }
//            // 返回打印日志
//            logContent
//        } catch (e: Exception) {
//            LogPrintUtils.eTag(TAG, e, "saveLogRecord")
//            // 捕获异常
//            "catch exception"
//        }
//    }
//
//    /**
//     * 拼接日志
//     * @param logs 日志内容数组
//     * @return 拼接后的日志内容
//     */
//    private fun splitLog(vararg logs: String): String {
//        // 判断是否追加空格
//        val isSpace = isAppendSpace
//        // =
//        val builder = StringBuilder()
//        // 增加换行
//        builder.append(DevFinal.NEW_LINE_STR)
//        builder.append(DevFinal.NEW_LINE_STR)
//        // 获取保存时间
//        builder.append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
//        // 追加边距
//        builder.append(" => ")
//        // 是否添加空格 ( 第一位不添加空格 )
//        var isAdd = false
//        // 循环追加内容
//        var i = 0
//        val len = logs.size
//        while (i < len) {
//            if (isSpace && isAdd) { // 判断是否追加空格
//                builder.append(" ")
//            }
//            // 标记添加空格 ( 第一位不添加空格 )
//            isAdd = true
//            // 追加保存内容
//            builder.append(logs[i])
//            i++
//        }
//        return builder.toString()
//    }
//
//    // ===============
//    // = 日志保存时间 =
//    // ===============
//    // DEFAULT ( 默认天, 在根目录下 )
//    const val DEFAULT = 0
//
//    // 小时
//    const val HH = 1
//
//    // 分钟
//    const val MM = 2
//
//    // 秒
//    const val SS = 3
//    // ===============
//    // = 设备信息处理 =
//    // ===============
//    /**
//     * 处理设备信息
//     * @param errorInfo 错误提示信息, 如获取设备信息失败
//     * @return 拼接后的设备信息字符串
//     */
//    private fun handlerDeviceInfo(errorInfo: String): String? {
//        // 如果不为 null, 则直接返回之前的信息
//        if (!TextUtils.isEmpty(DEVICE_INFO_STR)) {
//            return DEVICE_INFO_STR
//        }
//        // 设备信息
//        val deviceInfo: String = DeviceUtils.handlerDeviceInfo(DEVICE_INFO_MAPS, null)
//            ?: return errorInfo
//        // 如果为 null
//        // 保存设备信息
//        DEVICE_INFO_STR = deviceInfo
//        // 返回设备信息
//        return DEVICE_INFO_STR
//    }
//
//    @IntDef(DEFAULT, HH, MM, SS)
//    @Retention(AnnotationRetention.SOURCE)
//    annotation class TIME
//
//    /**
//     * detail: 记录文件信息实体类
//     * @author Ttt
//     */
//    class FileInfo
//    /**
//     * 构造函数
//     * @param storagePath      存储路径
//     * @param folderName       文件夹名
//     * @param fileName         文件名
//     * @param fileFunction     文件记录的功能
//     * @param fileIntervalTime 文件记录间隔时间
//     * @param handler          是否处理日志记录
//     */ private constructor(
//        // 存储路径
//        private var storagePath: String?,
//        // 文件夹名
//        private var folderName: String?,
//        /**
//         * 获取日志文件名
//         * @return 日志文件名
//         */
//        // 文件名 如: xxx.txt
//        val fileName: String,
//        /**
//         * 获取日志文件记录功能
//         * @return 日志文件记录功能
//         */
//        // 文件记录的功能
//        val fileFunction: String,
//        /**
//         * 获取日志文件记录间隔时间
//         * @return 日志文件记录间隔时间
//         */
//        // 文件记录间隔时间 如: HH
//        @param:TIME val fileIntervalTime: Int,
//        /**
//         * 判断是否处理日志记录
//         * @return `true` yes, `false` no
//         */
//        // 是否处理日志记录
//        var isHandler: Boolean
//    ) {
//
//        // ===========
//        // = get/set =
//        // ===========
//        /**
//         * 获取存储路径
//         * @return 存储路径
//         */
//        fun getStoragePath(): String? {
//            if (TextUtils.isEmpty(storagePath)) {
//                storagePath = logStoragePath
//            }
//            return storagePath
//        }
//
//        /**
//         * 获取日志文件夹名
//         * @return 日志文件夹名
//         */
//        fun getFolderName(): String? {
//            if (TextUtils.isEmpty(folderName)) {
//                folderName = logFolderName
//            }
//            return folderName
//        }
//
//        /**
//         * 设置是否处理日志记录
//         * @param handler 是否处理日志记录
//         * @return [FileInfo]
//         */
//        fun setHandler(handler: Boolean): FileInfo {
//            this.isHandler = handler
//            return this
//        }
//        // ===============
//        // = 内部处理方法 =
//        // ===============// 返回拼接后的路径
//        /**
//         * 获取日志文件地址
//         * @return 日志文件地址
//         */
//        val logPath: String
//            get() =// 返回拼接后的路径
//                FileUtils.getFilePathCreateFolder(
//                    getStoragePath(),
//                    logFolderName + File.separator + DateUtils.getDateNow("yyyy_MM_dd")
//                ).toString() + intervalTimeFolder// 属于秒
//        // 秒格式
//        // /folder/HH/HH_number/MM/MM_number/SS_number/
//        // /LogSpace/HH/HH_15/MM/MM_55/SS_12/
//        // 放到未知目录下
//// /folder/HH/HH_number/MM/MM_number/
//        // /LogSpace/HH/HH_15/MM/MM_55/
//// 分钟格式
//        // 判断是否属于分钟
//// /folder/HH/HH_number/
//        // /LogSpace/HH/HH_15/
//// 小时格式
//        // 判断属于小时格式
//// 文件夹
//        // 获取间隔时间
//        /**
//         * 获取时间间隔所属的文件夹
//         * @return 时间间隔所属的文件夹
//         */
//        val intervalTimeFolder: String
//            get() {
//                // 文件夹
//                val folder = File.separator + getFolderName() + File.separator
//                // 获取间隔时间
//                val iTime = fileIntervalTime
//                when (iTime) {
//                    DEFAULT -> return folder
//                    HH, MM, SS -> {
//                        // 小时格式
//                        val hh_Format: String = DateUtils.getDateNow("HH")
//                        // 判断属于小时格式
//                        return if (iTime == HH) {
//                            // /folder/HH/HH_number/
//                            // /LogSpace/HH/HH_15/
//                            folder + "HH/HH_" + hh_Format + File.separator
//                        } else {
//                            // 分钟格式
//                            val mm_Format: String = DateUtils.getDateNow("mm")
//                            // 判断是否属于分钟
//                            if (iTime == MM) {
//                                // /folder/HH/HH_number/MM/MM_number/
//                                // /LogSpace/HH/HH_15/MM/MM_55/
//                                folder + "HH/HH_" + hh_Format + "/MM/MM_" + mm_Format + File.separator
//                            } else { // 属于秒
//                                // 秒格式
//                                val ss_Format: String = DateUtils.getDateNow("ss")
//                                // /folder/HH/HH_number/MM/MM_number/SS_number/
//                                // /LogSpace/HH/HH_15/MM/MM_55/SS_12/
//                                folder + "HH/HH_" + hh_Format + "/MM/MM_" + mm_Format + "/SS_" + ss_Format + File.separator
//                            }
//                        }
//                    }
//                }
//                // 放到未知目录下
//                return "/Unknown/"
//            }
//
//        companion object {
//            // =
//            /**
//             * 获取日志记录分析文件对象
//             * @param fileName     文件名
//             * @param fileFunction 日志文件记录功能
//             * @return [FileInfo]
//             */
//            operator fun get(
//                fileName: String,
//                fileFunction: String
//            ): FileInfo {
//                return FileInfo(null, null, fileName, fileFunction, DEFAULT, true)
//            }
//
//            /**
//             * 获取日志记录分析文件对象
//             * @param folderName   日志文件名
//             * @param fileName     文件名
//             * @param fileFunction 日志文件记录功能
//             * @return [FileInfo]
//             */
//            operator fun get(
//                folderName: String?,
//                fileName: String,
//                fileFunction: String
//            ): FileInfo {
//                return FileInfo(null, folderName, fileName, fileFunction, DEFAULT, true)
//            }
//
//            /**
//             * 获取日志记录分析文件对象
//             * @param storagePath  存储路径
//             * @param folderName   日志文件名
//             * @param fileName     文件名
//             * @param fileFunction 日志文件记录功能
//             * @return [FileInfo]
//             */
//            operator fun get(
//                storagePath: String?,
//                folderName: String?,
//                fileName: String,
//                fileFunction: String
//            ): FileInfo {
//                return FileInfo(storagePath, folderName, fileName, fileFunction, DEFAULT, true)
//            }
//            // =
//            /**
//             * 获取日志记录分析文件对象
//             * @param fileName         文件名
//             * @param fileFunction     日志文件记录功能
//             * @param fileIntervalTime 日志文件记录间隔时间
//             * @return [FileInfo]
//             */
//            operator fun get(
//                fileName: String,
//                fileFunction: String,
//                @TIME fileIntervalTime: Int
//            ): FileInfo {
//                return FileInfo(null, null, fileName, fileFunction, fileIntervalTime, true)
//            }
//
//            /**
//             * 获取日志记录分析文件对象
//             * @param folderName       日志文件名
//             * @param fileName         文件名
//             * @param fileFunction     日志文件记录功能
//             * @param fileIntervalTime 日志文件记录间隔时间
//             * @return [FileInfo]
//             */
//            operator fun get(
//                folderName: String?,
//                fileName: String,
//                fileFunction: String,
//                @TIME fileIntervalTime: Int
//            ): FileInfo {
//                return FileInfo(null, folderName, fileName, fileFunction, fileIntervalTime, true)
//            }
//
//            /**
//             * 获取日志记录分析文件对象
//             * @param storagePath      存储路径
//             * @param folderName       日志文件名
//             * @param fileName         文件名
//             * @param fileFunction     日志文件记录功能
//             * @param fileIntervalTime 日志文件记录间隔时间
//             * @return [FileInfo]
//             */
//            operator fun get(
//                storagePath: String?,
//                folderName: String?,
//                fileName: String,
//                fileFunction: String,
//                @TIME fileIntervalTime: Int
//            ): FileInfo {
//                return FileInfo(
//                    storagePath,
//                    folderName,
//                    fileName,
//                    fileFunction,
//                    fileIntervalTime,
//                    true
//                )
//            }
//
//            /**
//             * 获取日志记录分析文件对象
//             * @param storagePath      存储路径
//             * @param folderName       日志文件名
//             * @param fileName         文件名
//             * @param fileFunction     日志文件记录功能
//             * @param fileIntervalTime 日志文件记录间隔时间
//             * @param isHandler        是否处理日志记录
//             * @return [FileInfo]
//             */
//            operator fun get(
//                storagePath: String?,
//                folderName: String?,
//                fileName: String,
//                fileFunction: String,
//                @TIME fileIntervalTime: Int,
//                isHandler: Boolean
//            ): FileInfo {
//                return FileInfo(
//                    storagePath,
//                    folderName,
//                    fileName,
//                    fileFunction,
//                    fileIntervalTime,
//                    isHandler
//                )
//            }
//        }
//        // ===========
//        // = 构造函数 =
//        // ===========
//    }
//    // ===========
//    // = 接口回调 =
//    // ===========
//    /**
//     * detail: 文件记录回调
//     * @author Ttt
//     */
//    interface Callback {
//        /**
//         * 记录结果回调
//         * @param result     保存结果
//         * @param fileInfo   [FileInfo]
//         * @param logContent 日志信息
//         * @param filePath   保存路径
//         * @param fileName   文件名 ( 含后缀 )
//         * @param logs       原始日志内容数组
//         */
//        fun callback(
//            result: Boolean,
//            fileInfo: FileInfo?,
//            logContent: String?,
//            filePath: String?,
//            fileName: String?,
//            vararg logs: String?
//        )
//    }
//}