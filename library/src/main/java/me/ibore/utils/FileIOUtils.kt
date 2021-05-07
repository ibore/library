package me.ibore.utils

import android.util.Log
import me.ibore.ktx.logD
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2017/06/22
 * desc  : utils about file io
</pre> *
 */
object FileIOUtils {

    private var sBufferSize = 524288

    /**
     * Write file from input stream.
     *
     * @param filePath The path of file.
     * @param is       The input stream.
     * @param append   True to append, false otherwise.
     * @param listener The progress update listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromIS(
        filePath: String, inputStream: InputStream, append: Boolean = false,
        listener: OnProgressUpdateListener? = null
    ): Boolean {
        return writeFileFromIS(
            FileUtils.getFileByPath(filePath) ?: return false, inputStream, append, listener
        )
    }

    /**
     * Write file from input stream.
     *
     * @param file     The file.
     * @param is       The input stream.
     * @param append   True to append, false otherwise.
     * @param listener The progress update listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromIS(
        file: File, inputStream: InputStream, append: Boolean = false,
        listener: OnProgressUpdateListener? = null
    ): Boolean {
        if (!FileUtils.createOrExistsFile(file)) {
            logD("create file <$file> failed.")
            return false
        }
        var os: OutputStream? = null
        return try {
            os = BufferedOutputStream(FileOutputStream(file, append), sBufferSize)
            if (listener == null) {
                val data = ByteArray(sBufferSize)
                var len: Int
                while (inputStream.read(data).also { len = it } != -1) {
                    os.write(data, 0, len)
                }
            } else {
                val totalSize = inputStream.available().toDouble()
                var curSize = 0
                listener.onProgressUpdate(0.0)
                val data = ByteArray(sBufferSize)
                var len: Int
                while (inputStream.read(data).also { len = it } != -1) {
                    os.write(data, 0, len)
                    curSize += len
                    listener.onProgressUpdate(curSize / totalSize)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            CloseUtils.closeIOQuietly(inputStream, os)
        }
    }

    /**
     * Write file from bytes by stream.
     *
     * @param filePath The path of file.
     * @param bytes    The bytes.
     * @param append   True to append, false otherwise.
     * @param listener The progress update listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromBytesByStream(
        filePath: String, bytes: ByteArray, append: Boolean = false,
        listener: OnProgressUpdateListener? = null
    ): Boolean {
        return writeFileFromBytesByStream(
            FileUtils.getFileByPath(filePath) ?: return false, bytes, append, listener
        )
    }

    /**
     * Write file from bytes by stream.
     *
     * @param file     The file.
     * @param bytes    The bytes.
     * @param append   True to append, false otherwise.
     * @param listener The progress update listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromBytesByStream(
        file: File, bytes: ByteArray, append: Boolean = false,
        listener: OnProgressUpdateListener? = null
    ): Boolean {
        return writeFileFromIS(file, ByteArrayInputStream(bytes), append, listener)
    }

    /**
     * Write file from bytes by channel.
     *
     * @param filePath The path of file.
     * @param bytes    The bytes.
     * @param append   True to append, false otherwise.
     * @param isForce  True to force write file, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromBytesByChannel(
        filePath: String, bytes: ByteArray, append: Boolean = false, isForce: Boolean = true
    ): Boolean {
        return writeFileFromBytesByChannel(
            FileUtils.getFileByPath(filePath) ?: return false, bytes, append, isForce
        )
    }

    /**
     * Write file from bytes by channel.
     *
     * @param file    The file.
     * @param bytes   The bytes.
     * @param append  True to append, false otherwise.
     * @param isForce True to force write file, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromBytesByChannel(
        file: File, bytes: ByteArray, append: Boolean = false, isForce: Boolean = true
    ): Boolean {
        if (!FileUtils.createOrExistsFile(file)) {
            logD("create file <$file> failed.")
            return false
        }
        var fc: FileChannel? = null
        return try {
            fc = FileOutputStream(file, append).channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return false
            }
            fc.position(fc.size())
            fc.write(ByteBuffer.wrap(bytes))
            if (isForce) fc.force(true)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                fc?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Write file from bytes by map.
     *
     * @param filePath The path of file.
     * @param bytes    The bytes.
     * @param append   True to append, false otherwise.
     * @param isForce  True to force write file, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromBytesByMap(
        filePath: String, bytes: ByteArray, append: Boolean = false, isForce: Boolean = true
    ): Boolean {
        return writeFileFromBytesByMap(
            FileUtils.getFileByPath(filePath) ?: return false, bytes, append, isForce
        )
    }

    /**
     * Write file from bytes by map.
     *
     * @param file    The file.
     * @param bytes   The bytes.
     * @param append  True to append, false otherwise.
     * @param isForce True to force write file, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromBytesByMap(
        file: File, bytes: ByteArray, append: Boolean = false, isForce: Boolean = true
    ): Boolean {
        if (!FileUtils.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var fc: FileChannel? = null
        return try {
            fc = FileOutputStream(file, append).channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return false
            }
            val mbb = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bytes.size.toLong())
            mbb.put(bytes)
            if (isForce) mbb.force()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                fc?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Write file from string.
     *
     * @param filePath The path of file.
     * @param content  The string of content.
     * @param append   True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromString(filePath: String, content: String, append: Boolean = false): Boolean {
        return writeFileFromString(
            FileUtils.getFileByPath(filePath) ?: return false, content, append
        )
    }

    /**
     * Write file from string.
     *
     * @param file    The file.
     * @param content The string of content.
     * @param append  True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromString(file: File, content: String, append: Boolean = false): Boolean {
        if (!FileUtils.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var bw: BufferedWriter? = null
        return try {
            bw = BufferedWriter(FileWriter(file, append))
            bw.write(content)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                bw?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Return the lines in file.
     *
     * @param filePath    The path of file.
     * @param st          The line's index of start.
     * @param end         The line's index of end.
     * @param charsetName The name of charset.
     * @return the lines in file
     */
    @JvmStatic
    @JvmOverloads
    fun readFile2List(
        filePath: String, st: Int = 0, end: Int = 0x7FFFFFFF, charsetName: String? = null
    ): List<String> {
        return readFile2List(
            FileUtils.getFileByPath(filePath) ?: return emptyList(), st, end, charsetName
        )
    }

    /**
     * Return the lines in file.
     *
     * @param file        The file.
     * @param st          The line's index of start.
     * @param end         The line's index of end.
     * @param charsetName The name of charset.
     * @return the lines in file
     */
    @JvmStatic
    @JvmOverloads
    fun readFile2List(
        file: File, st: Int = 0, end: Int = 0x7FFFFFFF, charsetName: String? = null
    ): List<String> {
        if (!FileUtils.isFileExists(file)) return emptyList()
        if (st > end) return emptyList()
        var reader: BufferedReader? = null
        return try {
            var line: String
            var curLine = 1
            val list: MutableList<String> = ArrayList()
            reader = if (charsetName.isNullOrBlank()) {
                BufferedReader(InputStreamReader(FileInputStream(file)))
            } else {
                BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            }
            while (reader.readLine().also { line = it } != null) {
                if (curLine > end) break
                if (curLine in st..end) list.add(line)
                ++curLine
            }
            list
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Return the string in file.
     *
     * @param filePath    The path of file.
     * @param charsetName The name of charset.
     * @return the string in file
     */
    @JvmStatic
    @JvmOverloads
    fun readFile2String(filePath: String, charsetName: String? = null): String {
        return readFile2String(FileUtils.getFileByPath(filePath) ?: return "", charsetName)
    }

    /**
     * Return the string in file.
     *
     * @param file        The file.
     * @param charsetName The name of charset.
     * @return the string in file
     */
    @JvmStatic
    @JvmOverloads
    fun readFile2String(file: File, charsetName: String? = null): String {
        val bytes = readFile2BytesByStream(file)
        return if (charsetName.isNullOrBlank()) {
            String(bytes)
        } else {
            try {
                String(bytes, Charset.forName(charsetName))
            } catch (e: UnsupportedEncodingException) {
                logD(e)
                ""
            }
        }
    }

    /**
     * Return the bytes in file by stream.
     *
     * @param filePath The path of file.
     * @param listener The progress update listener.
     * @return the bytes in file
     */
    @JvmStatic
    @JvmOverloads
    fun readFile2BytesByStream(
        filePath: String, listener: OnProgressUpdateListener? = null
    ): ByteArray {
        return readFile2BytesByStream(
            FileUtils.getFileByPath(filePath)?:return ByteArray(0), listener)
    }

    /**
     * Return the bytes in file by stream.
     *
     * @param file     The file.
     * @param listener The progress update listener.
     * @return the bytes in file
     */
    @JvmStatic
    @JvmOverloads
    fun readFile2BytesByStream(
        file: File, listener: OnProgressUpdateListener? = null
    ): ByteArray {
        return if (!FileUtils.isFileExists(file)) ByteArray(0) else {
            var os: ByteArrayOutputStream? = null
            val inputStream: InputStream = BufferedInputStream(FileInputStream(file), sBufferSize)
            try {
                os = ByteArrayOutputStream()
                val b = ByteArray(sBufferSize)
                var len: Int
                if (listener == null) {
                    while (inputStream.read(b, 0, sBufferSize).also { len = it } != -1) {
                        os.write(b, 0, len)
                    }
                } else {
                    val totalSize = inputStream.available().toDouble()
                    var curSize = 0
                    listener.onProgressUpdate(0.0)
                    while (inputStream.read(b, 0, sBufferSize).also { len = it } != -1) {
                        os.write(b, 0, len)
                        curSize += len
                        listener.onProgressUpdate(curSize / totalSize)
                    }
                }
                os.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
                ByteArray(0)
            } finally {
                CloseUtils.closeIOQuietly(inputStream, os)
            }
        }
    }

    /**
     * Return the bytes in file by channel.
     *
     * @param filePath The path of file.
     * @return the bytes in file
     */
    fun readFile2BytesByChannel(filePath: String): ByteArray {
        return readFile2BytesByChannel(FileUtils.getFileByPath(filePath) ?: return ByteArray(0))
    }

    /**
     * Return the bytes in file by channel.
     *
     * @param file The file.
     * @return the bytes in file
     */
    fun readFile2BytesByChannel(file: File): ByteArray {
        if (!FileUtils.isFileExists(file)) return ByteArray(0)
        var fc: FileChannel? = null
        return try {
            fc = RandomAccessFile(file, "r").channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return ByteArray(0)
            }
            val byteBuffer = ByteBuffer.allocate(fc.size().toInt())
            while (true) {
                if (fc.read(byteBuffer) <= 0) break
            }
            byteBuffer.array()
        } catch (e: IOException) {
            logD(e)
            ByteArray(0)
        } finally {
            CloseUtils.closeIOQuietly(fc)
        }
    }

    /**
     * Return the bytes in file by map.
     *
     * @param filePath The path of file.
     * @return the bytes in file
     */
    fun readFile2BytesByMap(filePath: String): ByteArray {
        return readFile2BytesByMap(FileUtils.getFileByPath(filePath)?:return ByteArray(0))
    }

    /**
     * Return the bytes in file by map.
     *
     * @param file The file.
     * @return the bytes in file
     */
    fun readFile2BytesByMap(file: File): ByteArray {
        if (!FileUtils.isFileExists(file)) return ByteArray(0)
        var fc: FileChannel? = null
        return try {
            fc = RandomAccessFile(file, "r").channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return ByteArray(0)
            }
            val size = fc.size().toInt()
            val mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size.toLong()).load()
            val result = ByteArray(size)
            mbb[result, 0, size]
            result
        } catch (e: IOException) {
            logD(e)
            ByteArray(0)
        } finally {
            CloseUtils.closeIOQuietly(fc)
        }
    }

    /**
     * Set the buffer's size.
     *
     * Default size equals 8192 bytes.
     *
     * @param bufferSize The buffer's size.
     */
    fun setBufferSize(bufferSize: Int) {
        sBufferSize = bufferSize
    }

    interface OnProgressUpdateListener {
        fun onProgressUpdate(progress: Double)
    }
}