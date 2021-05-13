package me.ibore.utils

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.collection.SimpleArrayMap
import me.ibore.utils.UtilsBridge.FileHead
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.regex.Pattern
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

object LogUtils {

    const val V = Log.VERBOSE
    const val D = Log.DEBUG
    const val I = Log.INFO
    const val W = Log.WARN
    const val E = Log.ERROR
    const val A = Log.ASSERT
    private val T = charArrayOf('V', 'D', 'I', 'W', 'E', 'A')
    private const val FILE = 0x10
    private const val JSON = 0x20
    private const val XML = 0x30
    private val FILE_SEP = System.getProperty("file.separator")
    private val LINE_SEP = System.getProperty("line.separator")
    private const val TOP_CORNER = "┌"
    private const val MIDDLE_CORNER = "├"
    private const val LEFT_BORDER = "│ "
    private const val BOTTOM_CORNER = "└"
    private const val SIDE_DIVIDER = "────────────────────────────────────────────────────────"
    private const val MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private const val TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private const val MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
    private const val BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private const val MAX_LEN = 1100 // fit for Chinese character
    private const val NOTHING = "log nothing"
    private const val NULL = "null"
    private const val ARGS = "args"
    private const val PLACEHOLDER = " "
    val config = Config()
    private var simpleDateFormat: SimpleDateFormat? = null
    private val EXECUTOR = Executors.newSingleThreadExecutor()
    private val I_FORMATTER_MAP = SimpleArrayMap<Class<*>?, IFormatter<*>>()
    fun v(vararg contents: Any?) {
        log(V, config.globalTag, *contents)
    }

    @JvmStatic
    fun vTag(tag: String, vararg contents: Any?) {
        log(V, tag, *contents)
    }

    @JvmStatic
    fun d(vararg contents: Any?) {
        log(D, config.globalTag, *contents)
    }

    @JvmStatic
    fun dTag(tag: String, vararg contents: Any?) {
        log(D, tag, *contents)
    }

    @JvmStatic
    fun i(vararg contents: Any?) {
        log(I, config.globalTag, *contents)
    }

    @JvmStatic
    fun iTag(tag: String, vararg contents: Any?) {
        log(I, tag, *contents)
    }

    @JvmStatic
    fun w(vararg contents: Any?) {
        log(W, config.globalTag, *contents)
    }

    @JvmStatic
    fun wTag(tag: String, vararg contents: Any?) {
        log(W, tag, *contents)
    }

    @JvmStatic
    fun e(vararg contents: Any?) {
        log(E, config.globalTag, *contents)
    }

    @JvmStatic
    fun eTag(tag: String, vararg contents: Any?) {
        log(E, tag, *contents)
    }

    @JvmStatic
    fun a(vararg contents: Any?) {
        log(A, config.globalTag, *contents)
    }

    @JvmStatic
    fun aTag(tag: String, vararg contents: Any?) {
        log(A, tag, *contents)
    }

    @JvmStatic
    fun file(content: Any?) {
        log(FILE or D, config.globalTag, content)
    }

    @JvmStatic
    fun file(@TYPE type: Int, content: Any?) {
        log(FILE or type, config.globalTag, content)
    }

    @JvmStatic
    fun file(tag: String, content: Any?) {
        log(FILE or D, tag, content)
    }

    @JvmStatic
    fun file(@TYPE type: Int, tag: String, content: Any?) {
        log(FILE or type, tag, content)
    }

    @JvmStatic
    fun json(content: Any?) {
        log(JSON or D, config.globalTag, content)
    }

    @JvmStatic
    fun json(@TYPE type: Int, content: Any?) {
        log(JSON or type, config.globalTag, content)
    }

    @JvmStatic
    fun json(tag: String, content: Any?) {
        log(JSON or D, tag, content)
    }

    @JvmStatic
    fun json(@TYPE type: Int, tag: String, content: Any?) {
        log(JSON or type, tag, content)
    }

    @JvmStatic
    fun xml(content: String?) {
        log(XML or D, config.globalTag, content)
    }

    @JvmStatic
    fun xml(@TYPE type: Int, content: String?) {
        log(XML or type, config.globalTag, content)
    }

    @JvmStatic
    fun xml(tag: String, content: String?) {
        log(XML or D, tag, content)
    }

    @JvmStatic
    fun xml(@TYPE type: Int, tag: String, content: String?) {
        log(XML or type, tag, content)
    }

    @JvmStatic
    fun log(type: Int, tag: String, vararg contents: Any?) {
        if (!config.isLogSwitch) return
        val type_low = type and 0x0f
        val type_high = type and 0xf0
        if (config.isLog2ConsoleSwitch || config.isLog2FileSwitch || type_high == FILE) {
            if (type_low < config.mConsoleFilter && type_low < config.mFileFilter) return
            val tagHead = processTagAndHead(tag)
            val body = processBody(type_high, *contents)
            if (config.isLog2ConsoleSwitch && type_high != FILE && type_low >= config.mConsoleFilter) {
                print2Console(type_low, tagHead.tag, tagHead.consoleHead, body)
            }
            if ((config.isLog2FileSwitch || type_high == FILE) && type_low >= config.mFileFilter) {
                EXECUTOR.execute { print2File(type_low, tagHead.tag, tagHead.fileHead + body) }
            }
        }
    }

    val currentLogFilePath: String
        get() = getCurrentLogFilePath(Date())

    val logFiles: List<File>
        get() {
            val dir = config.dir
            val logDir = File(dir)
            if (!logDir.exists()) return ArrayList()
            val files = logDir.listFiles { _, name -> isMatchLogFileName(name) }
            val list: MutableList<File> = ArrayList()
            Collections.addAll(list, *files)
            return list
        }

    private fun processTagAndHead(tag: String): TagHead {
        var tagTemp = tag
        if (!config.mTagIsSpace && !config.isLogHeadSwitch) {
            tagTemp = config.globalTag
        } else {
            val stackTrace = Throwable().stackTrace
            val stackIndex = 3 + config.stackOffset
            if (stackIndex >= stackTrace.size) {
                val targetElement = stackTrace[3]
                val fileName = getFileName(targetElement)
                if (config.mTagIsSpace && tagTemp.isBlank()) {
                    val index = fileName.indexOf('.') // Use proguard may not find '.'.
                    tagTemp = if (index == -1) fileName else fileName.substring(0, index)
                }
                return TagHead(tagTemp, null, ": ")
            }
            var targetElement = stackTrace[stackIndex]
            val fileName = getFileName(targetElement)
            if (config.mTagIsSpace && tagTemp.isBlank()) {
                val index = fileName.indexOf('.') // Use proguard may not find '.'.
                tagTemp = if (index == -1) fileName else fileName.substring(0, index)
            }
            if (config.isLogHeadSwitch) {
                val tName = Thread.currentThread().name
                val head = Formatter()
                    .format(
                        "%s, %s.%s(%s:%d)",
                        tName,
                        targetElement.className,
                        targetElement.methodName,
                        fileName,
                        targetElement.lineNumber
                    )
                    .toString()
                val fileHead = " [$head]: "
                return if (config.stackDeep <= 1) {
                    TagHead(tagTemp, arrayOf(head), fileHead)
                } else {
                    val consoleHead = arrayOfNulls<String>(
                        config.stackDeep.coerceAtMost(stackTrace.size - stackIndex)
                    )
                    consoleHead[0] = head
                    val spaceLen = tName.length + 2
                    val space = Formatter().format("%" + spaceLen + "s", "").toString()
                    var i = 1
                    val len = consoleHead.size
                    while (i < len) {
                        targetElement = stackTrace[i + stackIndex]
                        consoleHead[i] = Formatter()
                            .format(
                                "%s%s.%s(%s:%d)",
                                space,
                                targetElement.className,
                                targetElement.methodName,
                                getFileName(targetElement),
                                targetElement.lineNumber
                            )
                            .toString()
                        ++i
                    }
                    TagHead(tagTemp, consoleHead, fileHead)
                }
            }
        }
        return TagHead(tagTemp, null, ": ")
    }

    private fun getFileName(targetElement: StackTraceElement): String {
        val fileName = targetElement.fileName
        if (fileName != null) return fileName
        // If name of file is null, should add
        // "-keepattributes SourceFile,LineNumberTable" in proguard file.
        var className = targetElement.className
        val classNameInfo = className.split("\\.").toTypedArray()
        if (classNameInfo.isNotEmpty()) {
            className = classNameInfo[classNameInfo.size - 1]
        }
        val index = className.indexOf('$')
        if (index != -1) {
            className = className.substring(0, index)
        }
        return "$className.java"
    }

    private fun processBody(type: Int, vararg contents: Any?): String {
        val body: String = if (contents.size == 1) {
            formatObject(type, contents[0])
        } else {
            val sb = StringBuilder()
            var i = 0
            val len = contents.size
            while (i < len) {
                val content = contents[i]
                sb.append(ARGS)
                    .append("[")
                    .append(i)
                    .append("]")
                    .append(" = ")
                    .append(formatObject(content))
                    .append(LINE_SEP)
                ++i
            }
            sb.toString()
        }
        return if (body.isEmpty()) NOTHING else body
    }

    private fun formatObject(type: Int, any: Any?): String {
        if (any == null) return NULL
        if (type == JSON) return LogFormatter.object2String(any, JSON)
        return if (type == XML) LogFormatter.object2String(
            any,
            XML
        ) else formatObject(any)
    }

    private fun formatObject(any: Any?): String {
        if (any == null) return NULL
        if (!I_FORMATTER_MAP.isEmpty) {
            val iFormatter: IFormatter<Any>? =
                I_FORMATTER_MAP[getClassFromObject(any)] as IFormatter<Any>?
            if (iFormatter != null) {
                return iFormatter.format(any)
            }
        }
        return LogFormatter.object2String(any)
    }

    private fun print2Console(
        type: Int,
        tag: String,
        head: Array<String?>?,
        msg: String
    ) {
        if (config.isSingleTagSwitch) {
            printSingleTagMsg(type, tag, processSingleTagMsg(type, tag, head, msg))
        } else {
            printBorder(type, tag, true)
            printHead(type, tag, head)
            printMsg(type, tag, msg)
            printBorder(type, tag, false)
        }
    }

    private fun printBorder(type: Int, tag: String, isTop: Boolean) {
        if (config.isLogBorderSwitch) {
            print2Console(type, tag, if (isTop) TOP_BORDER else BOTTOM_BORDER)
        }
    }

    private fun printHead(type: Int, tag: String, head: Array<String?>?) {
        if (head != null) {
            for (aHead in head) {
                print2Console(
                    type,
                    tag,
                    (if (config.isLogBorderSwitch) LEFT_BORDER + aHead else aHead)!!
                )
            }
            if (config.isLogBorderSwitch) print2Console(type, tag, MIDDLE_BORDER)
        }
    }

    private fun printMsg(type: Int, tag: String, msg: String) {
        val len = msg.length
        val countOfSub = len / MAX_LEN
        if (countOfSub > 0) {
            var index = 0
            for (i in 0 until countOfSub) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN))
                index += MAX_LEN
            }
            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len))
            }
        } else {
            printSubMsg(type, tag, msg)
        }
    }

    private fun printSubMsg(type: Int, tag: String, msg: String) {
        if (!config.isLogBorderSwitch) {
            print2Console(type, tag, msg)
            return
        }
        val sb = StringBuilder()
        val lines = msg.split(LINE_SEP).toTypedArray()
        for (line in lines) {
            print2Console(type, tag, LEFT_BORDER + line)
        }
    }

    private fun processSingleTagMsg(
        type: Int,
        tag: String,
        head: Array<String?>?,
        msg: String
    ): String {
        val sb = StringBuilder()
        if (config.isLogBorderSwitch) {
            sb.append(PLACEHOLDER).append(LINE_SEP)
            sb.append(TOP_BORDER).append(LINE_SEP)
            if (head != null) {
                for (aHead in head) {
                    sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP)
                }
                sb.append(MIDDLE_BORDER).append(LINE_SEP)
            }
            for (line in msg.split(LINE_SEP).toTypedArray()) {
                sb.append(LEFT_BORDER).append(line).append(LINE_SEP)
            }
            sb.append(BOTTOM_BORDER)
        } else {
            if (head != null) {
                sb.append(PLACEHOLDER).append(LINE_SEP)
                for (aHead in head) {
                    sb.append(aHead).append(LINE_SEP)
                }
            }
            sb.append(msg)
        }
        return sb.toString()
    }

    private fun printSingleTagMsg(type: Int, tag: String, msg: String) {
        val len = msg.length
        val countOfSub =
            if (config.isLogBorderSwitch) (len - BOTTOM_BORDER.length) / MAX_LEN else len / MAX_LEN
        if (countOfSub > 0) {
            if (config.isLogBorderSwitch) {
                print2Console(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER)
                var index = MAX_LEN
                for (i in 1 until countOfSub) {
                    print2Console(
                        type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                                + LEFT_BORDER + msg.substring(index, index + MAX_LEN)
                                + LINE_SEP + BOTTOM_BORDER
                    )
                    index += MAX_LEN
                }
                if (index != len - BOTTOM_BORDER.length) {
                    print2Console(
                        type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                                + LEFT_BORDER + msg.substring(index, len)
                    )
                }
            } else {
                print2Console(type, tag, msg.substring(0, MAX_LEN))
                var index = MAX_LEN
                for (i in 1 until countOfSub) {
                    print2Console(
                        type, tag,
                        PLACEHOLDER + LINE_SEP + msg.substring(index, index + MAX_LEN)
                    )
                    index += MAX_LEN
                }
                if (index != len) {
                    print2Console(type, tag, PLACEHOLDER + LINE_SEP + msg.substring(index, len))
                }
            }
        } else {
            print2Console(type, tag, msg)
        }
    }

    private fun print2Console(type: Int, tag: String, msg: String) {
        Log.println(type, tag, msg)
        config.onConsoleOutputListener?.onConsoleOutput(type, tag, msg)
    }

    private fun print2File(type: Int, tag: String, msg: String) {
        val d = Date()
        val format = sdf!!.format(d)
        val date = format.substring(0, 10)
        val currentLogFilePath = getCurrentLogFilePath(d)
        if (!createOrExistsFile(currentLogFilePath, date)) {
            Log.e("LogUtils", "create $currentLogFilePath failed!")
            return
        }
        val time = format.substring(11)
        val content = time +
                T[type - V] +
                "/" +
                tag +
                msg +
                LINE_SEP
        input2File(currentLogFilePath, content)
    }

    private fun getCurrentLogFilePath(d: Date): String {
        val format = sdf!!.format(d)
        val date = format.substring(0, 10)
        return (config.dir + config.filePrefix + "_"
                + date + "_" +
                config.processName + config.fileExtension)
    }

    private val sdf: SimpleDateFormat?
        get() {
            if (simpleDateFormat == null) {
                simpleDateFormat =
                    SimpleDateFormat("yyyy_MM_dd HH:mm:ss.SSS ", Locale.getDefault())
            }
            return simpleDateFormat
        }

    private fun createOrExistsFile(filePath: String, date: String): Boolean {
        val file = File(filePath)
        if (file.exists()) return file.isFile
        return if (!FileUtils.createOrExistsDir(file.parentFile)) false else try {
            deleteDueLogs(filePath, date)
            val isCreate = file.createNewFile()
            if (isCreate) {
                printDeviceInfo(filePath, date)
            }
            isCreate
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun deleteDueLogs(filePath: String, date: String) {
        if (config.saveDays <= 0) return
        val file = File(filePath)
        val parentFile = file.parentFile
        val files = parentFile.listFiles { dir, name -> isMatchLogFileName(name) }
        if (files == null || files.isEmpty()) return
        val sdf = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
        try {
            val dueMillis = sdf.parse(date).time - config.saveDays * 86400000L
            for (aFile in files) {
                val name = aFile.name
                val l = name.length
                val logDay = findDate(name)
                if (sdf.parse(logDay).time <= dueMillis) {
                    EXECUTOR.execute {
                        val delete = aFile.delete()
                        if (!delete) {
                            Log.e("LogUtils", "delete $aFile failed!")
                        }
                    }
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun isMatchLogFileName(name: String): Boolean {
        return name.matches(Regex("^" + config.filePrefix + "_[0-9]{4}_[0-9]{2}_[0-9]{2}_.*$"))
    }

    private fun findDate(str: String): String {
        val pattern = Pattern.compile("[0-9]{4}_[0-9]{2}_[0-9]{2}")
        val matcher = pattern.matcher(str)
        return if (matcher.find()) {
            matcher.group()
        } else ""
    }

    private fun printDeviceInfo(filePath: String, date: String) {
        config.mFileHead.addFirst("Date of Log", date)
        input2File(filePath, config.mFileHead.toString())
    }

    private fun input2File(filePath: String, input: String) {
        if (config.mFileWriter == null) {
            FileIOUtils.writeFileFromString(filePath, input, true)
        } else {
            config.mFileWriter!!.write(filePath, input)
        }
        if (config.onFileOutputListener != null) {
            config.onFileOutputListener!!.onFileOutput(filePath, input)
        }
    }

    private fun <T> getTypeClassFromParadigm(formatter: IFormatter<T>): Class<*>? {
        val genericInterfaces = formatter.javaClass.genericInterfaces
        var type: Type?
        type = if (genericInterfaces.size == 1) {
            genericInterfaces[0]
        } else {
            formatter.javaClass.genericSuperclass
        }
        type = (type as ParameterizedType).actualTypeArguments[0]
        while (type is ParameterizedType) {
            type = type.rawType
        }
        var className = type.toString()
        if (className.startsWith("class ")) {
            className = className.substring(6)
        } else if (className.startsWith("interface ")) {
            className = className.substring(10)
        }
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getClassFromObject(obj: Any): Class<*> {
        val objClass: Class<*> = obj.javaClass
        if (objClass.isAnonymousClass || objClass.isSynthetic) {
            val genericInterfaces = objClass.genericInterfaces
            var className: String
            if (genericInterfaces.size == 1) { // interface
                var type: Type? = genericInterfaces[0]
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = type.toString()
            } else { // abstract class or lambda
                var type = objClass.genericSuperclass
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = type?.toString() ?: ""
            }
            if (className.startsWith("class ")) {
                className = className.substring(6)
            } else if (className.startsWith("interface ")) {
                className = className.substring(10)
            }
            try {
                return Class.forName(className)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return objClass
    }

    @IntDef(V, D, I, W, E, A)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TYPE
    class Config internal constructor() {
        // The default storage directory of log.
        private val defaultDir: String by lazy {
            return@lazy if (SDCardUtils.isSDCardEnableByEnvironment
                && Utils.app.getExternalFilesDir(null) != null
            ) Utils.app.getExternalFilesDir(null).toString() + FILE_SEP + "log" + FILE_SEP else {
                Utils.app.filesDir.toString() + FILE_SEP + "log" + FILE_SEP
            }
        }

        // The storage directory of log.
        private var mDir: String? = null

        // The file prefix of log.
        var filePrefix = "util"
            private set
        var fileExtension = ".txt" // The file extension of log.
            private set
        var isLogSwitch = true // The switch of log.
            private set
        var isLog2ConsoleSwitch = true // The logcat's switch of log.
            private set
        private var mGlobalTag = "" // The global tag of log.
        var mTagIsSpace = true // The global tag is space.
        var isLogHeadSwitch = true // The head's switch of log.
            private set
        var isLog2FileSwitch = false // The file's switch of log.
            private set
        var isLogBorderSwitch = true // The border's switch of log.
            private set
        var isSingleTagSwitch = true // The single tag of log.
            private set
        var mConsoleFilter = V // The console's filter of log.
        var mFileFilter = V // The file's filter of log.
        var stackDeep = 1 // The stack's deep of log.
            private set
        var stackOffset = 0 // The stack's offset of log.
            private set
        var saveDays = -1 // The save days of log.
            private set
        private val mProcessName: String = ProcessUtils.currentProcessName
        var mFileWriter: IFileWriter? = null
        var onConsoleOutputListener: OnConsoleOutputListener? = null
        var onFileOutputListener: OnFileOutputListener? = null
        internal val mFileHead = FileHead("Log")
        fun setLogSwitch(logSwitch: Boolean): Config {
            isLogSwitch = logSwitch
            return this
        }

        fun setConsoleSwitch(consoleSwitch: Boolean): Config {
            isLog2ConsoleSwitch = consoleSwitch
            return this
        }

        fun setGlobalTag(tag: String): Config {
            if (tag.isBlank()) {
                mGlobalTag = ""
                mTagIsSpace = true
            } else {
                mGlobalTag = tag
                mTagIsSpace = false
            }
            return this
        }

        fun setLogHeadSwitch(logHeadSwitch: Boolean): Config {
            isLogHeadSwitch = logHeadSwitch
            return this
        }

        fun setLog2FileSwitch(log2FileSwitch: Boolean): Config {
            isLog2FileSwitch = log2FileSwitch
            return this
        }

        fun setDir(dir: String): Config {
            mDir = if (dir.isBlank()) null
             else if (dir.endsWith(FILE_SEP)) dir else dir + FILE_SEP
            return this
        }

        fun setDir(dir: File?): Config {
            mDir = if (dir == null) null else dir.absolutePath + FILE_SEP
            return this
        }

        fun setFilePrefix(filePrefix: String): Config {
            this.filePrefix =  if (filePrefix.isBlank()) "util"
             else filePrefix
            return this
        }

        fun setFileExtension(fileExtension: String): Config {
            this.fileExtension = when {
                fileExtension.isBlank() -> ".txt"
                fileExtension.startsWith(".") -> fileExtension
                else -> ".$fileExtension"
            }
            return this
        }

        fun setBorderSwitch(borderSwitch: Boolean): Config {
            isLogBorderSwitch = borderSwitch
            return this
        }

        fun setSingleTagSwitch(singleTagSwitch: Boolean): Config {
            isSingleTagSwitch = singleTagSwitch
            return this
        }

        fun setConsoleFilter(@TYPE consoleFilter: Int): Config {
            mConsoleFilter = consoleFilter
            return this
        }

        fun setFileFilter(@TYPE fileFilter: Int): Config {
            mFileFilter = fileFilter
            return this
        }

        fun setStackDeep(@IntRange(from = 1) stackDeep: Int): Config {
            this.stackDeep = stackDeep
            return this
        }

        fun setStackOffset(@IntRange(from = 0) stackOffset: Int): Config {
            this.stackOffset = stackOffset
            return this
        }

        fun setSaveDays(@IntRange(from = 1) saveDays: Int): Config {
            this.saveDays = saveDays
            return this
        }

        fun <T> addFormatter(iFormatter: IFormatter<T>?): Config {
            if (iFormatter != null) {
                I_FORMATTER_MAP.put(getTypeClassFromParadigm(iFormatter), iFormatter)
            }
            return this
        }

        fun setFileWriter(fileWriter: IFileWriter?): Config {
            mFileWriter = fileWriter
            return this
        }

        fun setOnConsoleOutputListener(listener: OnConsoleOutputListener?): Config {
            onConsoleOutputListener = listener
            return this
        }

        fun setOnFileOutputListener(listener: OnFileOutputListener?): Config {
            onFileOutputListener = listener
            return this
        }

        fun addFileExtraHead(fileExtraHead: Map<String, String>?): Config {
            mFileHead.append(fileExtraHead)
            return this
        }

        fun addFileExtraHead(key: String, value: String): Config {
            mFileHead.append(key, value)
            return this
        }

        val processName: String
            get() = mProcessName.replace(":", "_")
        val dir: String
            get() = if (mDir == null) defaultDir else mDir!!
        val globalTag: String
            get() = if (mGlobalTag.isBlank()) "" else mGlobalTag
        val consoleFilter: Char
            get() = T[mConsoleFilter - V]
        val fileFilter: Char
            get() = T[mFileFilter - V]

        fun haveSetOnConsoleOutputListener(): Boolean {
            return onConsoleOutputListener != null
        }

        fun haveSetOnFileOutputListener(): Boolean {
            return onFileOutputListener != null
        }

        override fun toString(): String {
            return ("process: " + processName
                    + LINE_SEP + "logSwitch: " + isLogSwitch
                    + LINE_SEP + "consoleSwitch: " + isLog2ConsoleSwitch
                    + LINE_SEP + "tag: " + (if (globalTag == "") "null" else globalTag)
                    + LINE_SEP + "headSwitch: " + isLogHeadSwitch
                    + LINE_SEP + "fileSwitch: " + isLog2FileSwitch
                    + LINE_SEP + "dir: " + dir
                    + LINE_SEP + "filePrefix: " + filePrefix
                    + LINE_SEP + "borderSwitch: " + isLogBorderSwitch
                    + LINE_SEP + "singleTagSwitch: " + isSingleTagSwitch
                    + LINE_SEP + "consoleFilter: " + consoleFilter
                    + LINE_SEP + "fileFilter: " + fileFilter
                    + LINE_SEP + "stackDeep: " + stackDeep
                    + LINE_SEP + "stackOffset: " + stackOffset
                    + LINE_SEP + "saveDays: " + saveDays
                    + LINE_SEP + "formatter: " + I_FORMATTER_MAP
                    + LINE_SEP + "fileWriter: " + mFileWriter
                    + LINE_SEP + "onConsoleOutputListener: " + onConsoleOutputListener
                    + LINE_SEP + "onFileOutputListener: " + onFileOutputListener
                    + LINE_SEP + "fileExtraHeader: " + mFileHead.appended)
        }

    }

    abstract class IFormatter<T> {
        abstract fun format(t: T): String
    }

    interface IFileWriter {
        fun write(file: String?, content: String?)
    }

    interface OnConsoleOutputListener {
        fun onConsoleOutput(@TYPE type: Int, tag: String?, content: String?)
    }

    interface OnFileOutputListener {
        fun onFileOutput(filePath: String?, content: String?)
    }

    private data class TagHead(
        var tag: String,
        var consoleHead: Array<String?>?,
        var fileHead: String
    )

    private object LogFormatter {

        @JvmOverloads
        fun object2String(any: Any, type: Int = -1): String {
            if (any.javaClass.isArray) return array2String(any)
            if (any is Throwable) return ThrowableUtils.getFullStackTrace(any)
            if (any is Bundle) return bundle2String(any)
            if (any is Intent) return intent2String(any)
            if (type == JSON) {
                return object2Json(any)
            } else if (type == XML) {
                return formatXml(any.toString())
            }
            return any.toString()
        }

        private fun bundle2String(bundle: Bundle): String {
            val iterator: Iterator<String> = bundle.keySet().iterator()
            if (!iterator.hasNext()) {
                return "Bundle {}"
            }
            val sb = StringBuilder(128)
            sb.append("Bundle { ")
            while (true) {
                val key = iterator.next()
                val value = bundle[key]
                sb.append(key).append('=')
                if (value is Bundle) {
                    sb.append(
                        if (value === bundle) "(this Bundle)" else bundle2String(
                            value
                        )
                    )
                } else {
                    sb.append(formatObject(value))
                }
                if (!iterator.hasNext()) return sb.append(" }").toString()
                sb.append(',').append(' ')
            }
        }

        private fun intent2String(intent: Intent): String {
            val sb = StringBuilder(128)
            sb.append("Intent { ")
            var first = true
            val mAction = intent.action
            if (mAction != null) {
                sb.append("act=").append(mAction)
                first = false
            }
            val mCategories = intent.categories
            if (mCategories != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("cat=[")
                var firstCategory = true
                for (c in mCategories) {
                    if (!firstCategory) {
                        sb.append(',')
                    }
                    sb.append(c)
                    firstCategory = false
                }
                sb.append("]")
            }
            val mData = intent.data
            if (mData != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("dat=").append(mData)
            }
            val mType = intent.type
            if (mType != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("typ=").append(mType)
            }
            val mFlags = intent.flags
            if (mFlags != 0) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("flg=0x").append(Integer.toHexString(mFlags))
            }
            val mPackage = intent.getPackage()
            if (mPackage != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("pkg=").append(mPackage)
            }
            val mComponent = intent.component
            if (mComponent != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("cmp=").append(mComponent.flattenToShortString())
            }
            val mSourceBounds = intent.sourceBounds
            if (mSourceBounds != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("bnds=").append(mSourceBounds.toShortString())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val mClipData = intent.clipData
                if (mClipData != null) {
                    if (!first) {
                        sb.append(' ')
                    }
                    first = false
                    clipData2String(mClipData, sb)
                }
            }
            val mExtras = intent.extras
            if (mExtras != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("extras={")
                sb.append(bundle2String(mExtras))
                sb.append('}')
            }
            val mSelector = intent.selector
            if (mSelector != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("sel={")
                sb.append(if (mSelector === intent) "(this Intent)" else intent2String(mSelector))
                sb.append("}")
            }
            sb.append(" }")
            return sb.toString()
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private fun clipData2String(clipData: ClipData, sb: StringBuilder) {
            val item = clipData.getItemAt(0)
            if (item == null) {
                sb.append("ClipData.Item {}")
                return
            }
            sb.append("ClipData.Item { ")
            val mHtmlText = item.htmlText
            if (mHtmlText != null) {
                sb.append("H:")
                sb.append(mHtmlText)
                sb.append("}")
                return
            }
            val mText = item.text
            if (mText != null) {
                sb.append("T:")
                sb.append(mText)
                sb.append("}")
                return
            }
            val uri = item.uri
            if (uri != null) {
                sb.append("U:").append(uri)
                sb.append("}")
                return
            }
            val intent = item.intent
            if (intent != null) {
                sb.append("I:")
                sb.append(intent2String(intent))
                sb.append("}")
                return
            }
            sb.append("NULL")
            sb.append("}")
        }

        private fun object2Json(any: Any): String {
            return if (any is CharSequence) {
                JsonUtils.formatJson(any.toString())
            } else try {
                GsonUtils.toJson(any)
            } catch (t: Throwable) {
                any.toString()
            }
        }

        private fun formatJson(json: String): String {
            try {
                var i = 0
                val len = json.length
                while (i < len) {
                    val c = json[i]
                    if (c == '{') {
                        return JSONObject(json).toString(2)
                    } else if (c == '[') {
                        return JSONArray(json).toString(2)
                    } else if (!Character.isWhitespace(c)) {
                        return json
                    }
                    i++
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return json
        }

        private fun formatXml(xml: String): String {
            var xml = xml
            try {
                val xmlInput: Source = StreamSource(StringReader(xml))
                val xmlOutput = StreamResult(StringWriter())
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transformer.transform(xmlInput, xmlOutput)
                xml = xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">" + LINE_SEP)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return xml
        }

        private fun array2String(any: Any): String {
            when (any) {
                is Array<*> -> {
                    return any.contentDeepToString()
                }
                is BooleanArray -> {
                    return any.contentToString()
                }
                is ByteArray -> {
                    return any.contentToString()
                }
                is CharArray -> {
                    return any.contentToString()
                }
                is DoubleArray -> {
                    return any.contentToString()
                }
                is FloatArray -> {
                    return any.contentToString()
                }
                is IntArray -> {
                    return any.contentToString()
                }
                is LongArray -> {
                    return any.contentToString()
                }
                is ShortArray -> {
                    return any.contentToString()
                }
                else -> throw IllegalArgumentException("Array has incompatible type: " + any.javaClass)
            }
        }
    }
}