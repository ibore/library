package me.ibore.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StatFs
import android.text.TextUtils
import java.io.*
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/05/03
 * desc  : utils about file
</pre> *
 */
object FileUtils {

    private val LINE_SEP: String = System.getProperty("line.separator")!!

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    @JvmStatic
    fun getFileByPath(filePath: String): File? {
        return if (filePath.isBlank()) null else File(filePath)
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isFileExists(file: File?): Boolean {
        return if (file?.exists()?:return false) true
        else isFileExists(file.absolutePath)
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isFileExists(filePath: String): Boolean {
        val file = getFileByPath(filePath) ?: return false
        return if (file.exists()) true
        else isFileExistsApi29(filePath)
    }

    @JvmStatic
    private fun isFileExistsApi29(filePath: String?): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val uri = Uri.parse(filePath)
                val cr = Utils.contentResolver
                val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false
                try {
                    afd.close()
                } catch (ignore: IOException) {
                }
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        }
        return false
    }

    /**
     * Rename the file.
     *
     * @param filePath The path of file.
     * @param newName  The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun rename(filePath: String, newName: String): Boolean {
        return rename(getFileByPath(filePath) ?: return false, newName)
    }

    /**
     * Rename the file.
     *
     * @param file    The file.
     * @param newName The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun rename(file: File, newName: String): Boolean {
        // file doesn't exist then return false
        if (!file.exists()) return false
        // the new name is space then return false
        if (newName.isBlank()) return false
        // the new name equals old name then return true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // the new name of file exists then return false
        return (!newFile.exists()
                && file.renameTo(newFile))
    }

    /**
     * Return whether it is a directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isDir(dirPath: String): Boolean {
        return isDir(getFileByPath(dirPath) ?: return false)
    }

    /**
     * Return whether it is a directory.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isDir(file: File): Boolean {
        return file.exists() && file.isDirectory
    }

    /**
     * Return whether it is a file.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isFile(filePath: String): Boolean {
        return isFile(getFileByPath(filePath) ?: return false)
    }

    /**
     * Return whether it is a file.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isFile(file: File): Boolean {
        return file.exists() && file.isFile
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param dirPath The path of directory.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsDir(dirPath: String): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsDir(file: File?): Boolean {
        return if (file?.exists() ?: return false) file.isDirectory else file.mkdirs()
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath The path of file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsFile(filePath: String): Boolean {
        return createOrExistsFile(getFileByPath(filePath) ?: return false)
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsFile(file: File): Boolean {
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath) ?: return false)
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun createFileByDeleteOldFile(file: File): Boolean {
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Copy the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun copy(srcPath: String, destPath: String, listener: OnReplaceListener? = null): Boolean {
        return copy(
            getFileByPath(srcPath) ?: return false,
            getFileByPath(destPath) ?: return false, listener
        )
    }

    /**
     * Copy the directory or file.
     *
     * @param src      The source.
     * @param dest     The destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun copy(src: File, dest: File, listener: OnReplaceListener? = null): Boolean {
        return if (src.isDirectory) {
            copyDir(src, dest, listener)
        } else copyFile(src, dest, listener)
    }

    /**
     * Copy the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    private fun copyDir(srcDir: File, destDir: File, listener: OnReplaceListener? = null): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * Copy the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    private fun copyFile(
        srcFile: File, destFile: File, listener: OnReplaceListener? = null
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * Move the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun move(srcPath: String, destPath: String, listener: OnReplaceListener? = null): Boolean {
        return move(
            getFileByPath(srcPath) ?: return false,
            getFileByPath(destPath) ?: return false, listener
        )
    }

    /**
     * Move the directory or file.
     *
     * @param src      The source.
     * @param dest     The destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun move(src: File, dest: File, listener: OnReplaceListener? = null): Boolean {
        return if (src.isDirectory) {
            moveDir(src, dest, listener)
        } else moveFile(src, dest, listener)
    }

    /**
     * Move the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun moveDir(srcDir: File, destDir: File, listener: OnReplaceListener? = null): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * Move the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun moveFile(srcFile: File, destFile: File, listener: OnReplaceListener? = null): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    @JvmStatic
    @JvmOverloads
    private fun copyOrMoveDir(
        srcDir: File, destDir: File,
        listener: OnReplaceListener? = null, isMove: Boolean
    ): Boolean {
        // destDir's path locate in srcDir's path then return false
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
                } else if (file.isDirectory) {
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
                }
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    @JvmStatic
    @JvmOverloads
    private fun copyOrMoveFile(
        srcFile: File,
        destFile: File,
        listener: OnReplaceListener? = null,
        isMove: Boolean
    ): Boolean {
        // srcFile equals destFile then return false
        if (srcFile == destFile) return false
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) {
            if (listener == null || listener.onReplace(
                    srcFile,
                    destFile
                )
            ) { // require delete the old file
                if (!destFile.delete()) { // unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        return if (!createOrExistsDir(destFile.parentFile)) false else try {
            (UtilsBridge.writeFileFromIS(destFile.absolutePath, FileInputStream(srcFile))
                    && !(isMove && !deleteFile(srcFile)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Delete the directory.
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun delete(filePath: String): Boolean {
        return delete(getFileByPath(filePath) ?: return false)
    }

    /**
     * Delete the directory.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun delete(file: File): Boolean {
        return if (file.isDirectory) deleteDir(file)
        else deleteFile(file)
    }

    /**
     * Delete the directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    private fun deleteDir(dir: File): Boolean {
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * Delete the file.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    private fun deleteFile(file: File): Boolean {
        return !file.exists() || file.isFile && file.delete()
    }

    /**
     * Delete the all in directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun deleteAllInDir(dirPath: String): Boolean {
        return deleteAllInDir(getFileByPath(dirPath) ?: return false)
    }

    /**
     * Delete the all in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun deleteAllInDir(dir: File): Boolean {
        return deleteFilesInDirWithFilter(dir)
    }

    /**
     * Delete all files in directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun deleteFilesInDir(dirPath: String): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath)?:return false)
    }

    /**
     * Delete all files in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dir: File): Boolean {
        return deleteFilesInDirWithFilter(dir) { pathname -> pathname.isFile }
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun deleteFilesInDirWithFilter(dirPath: String, filter: FileFilter?= null): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath)?:return false, filter)
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun deleteFilesInDirWithFilter(dir: File, filter: FileFilter?=null): Boolean {
        if (filter == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    @JvmStatic
    @JvmOverloads
    fun listFilesInDir(
        dirPath: String, filter: FileFilter = FileFilter { true },
        isRecursive: Boolean = false, comparator: Comparator<File>? = null
    ): List<File> {
        return listFilesInDir(
            getFileByPath(dirPath) ?: return emptyList(),
            filter, isRecursive, comparator
        )
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    @JvmStatic
    @JvmOverloads
    fun listFilesInDir(
        dir: File, filter: FileFilter = FileFilter { true },
        isRecursive: Boolean = false, comparator: Comparator<File>? = null
    ): List<File> {
        val files = listFilesInDirInner(dir, filter, isRecursive)
        if (comparator != null) {
            Collections.sort(files, comparator)
        }
        return files
    }

    private fun listFilesInDirInner(
        dir: File, filter: FileFilter, isRecursive: Boolean
    ): List<File> {
        val list: MutableList<File> = ArrayList()
        if (!isDir(dir)) return list
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {
                    list.addAll(listFilesInDirInner(file, filter, true))
                }
            }
        }
        return list
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param filePath The path of file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(filePath: String): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param file The file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }

    /**
     * Return the charset of file simply.
     *
     * @param filePath The path of file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(filePath: String): String {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * Return the charset of file simply.
     *
     * @param file The file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(file: File?): String {
        if (file == null) return ""
        if (isUtf8(file)) return "UTF-8"
        var p = 0
        var inputStream: InputStream? = null
        try {
            inputStream = BufferedInputStream(FileInputStream(file))
            p = (inputStream.read() shl 8) + inputStream.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return when (p) {
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * Return whether the charset of file is utf8.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(filePath: String): Boolean {
        return isUtf8(getFileByPath(filePath) ?: return false)
    }

    /**
     * Return whether the charset of file is utf8.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(file: File): Boolean {
        var inputStream: InputStream? = null
        try {
            val bytes = ByteArray(24)
            inputStream = BufferedInputStream(FileInputStream(file))
            val read = inputStream.read(bytes)
            return if (read != -1) {
                val readArr = ByteArray(read)
                System.arraycopy(bytes, 0, readArr, 0, read)
                isUtf8(readArr) == 100
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * UTF-8编码方式
     * ----------------------------------------------
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    private fun isUtf8(raw: ByteArray): Int {
        var i: Int
        val len: Int
        var utf8 = 0
        var ascii = 0
        if (raw.size > 3) {
            if (raw[0] == 0xEF.toByte() && raw[1] == 0xBB.toByte() && raw[2] == 0xBF.toByte()) {
                return 100
            }
        }
        len = raw.size
        var child = 0
        i = 0
        while (i < len) {

            // UTF-8 byte shouldn't be FF and FE
            if (raw[i].toInt() and 0xFF.toInt() == 0xFF || raw[i].toInt() and 0xFE == 0xFE) {
                return 0
            }
            if (child == 0) {
                // ASCII format is 0x0*******
                if (raw[i].toInt() and 0x7F == raw[i].toInt() && raw[i].toInt() != 0) {
                    ascii++
                } else if (raw[i].toInt() and 0xC0 == 0xC0) {
                    // 0x11****** maybe is UTF-8
                    for (bit in 0..7) {
                        child =
                            if ((0x80 shr bit).toInt() and raw[i].toInt() == (0x80 shr bit).toByte()
                                    .toInt()
                            ) {
                                bit
                            } else {
                                break
                            }
                    }
                    utf8++
                }
                i++
            } else {
                child = if (raw.size - i > child) child else raw.size - i
                var currentNotUtf8 = false
                for (children in 0 until child) {
                    // format must is 0x10******
                    if (raw[i + children].toInt() and 0x80 != 0x80.toByte().toInt()) {
                        if (raw[i + children].toInt() and 0x7F == raw[i + children].toInt() && raw[i].toInt() != 0) {
                            // ASCII format is 0x0*******
                            ascii++
                        }
                        currentNotUtf8 = true
                    }
                }
                if (currentNotUtf8) {
                    utf8--
                    i++
                } else {
                    utf8 += child
                    i += child
                }
                child = 0
            }
        }
        // UTF-8 contains ASCII
        return if (ascii == len) {
            100
        } else (100 * ((utf8 + ascii).toFloat() / len.toFloat())).toInt()
    }

    /**
     * Return the number of lines of file.
     *
     * @param filePath The path of file.
     * @return the number of lines of file
     */
    fun getFileLines(filePath: String): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * Return the number of lines of file.
     *
     * @param file The file.
     * @return the number of lines of file
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (LINE_SEP.endsWith("\n")) {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\n'.toByte()) ++count
                    }
                }
            } else {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\r'.toByte()) ++count
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return count
    }

    /**
     * Return the size.
     *
     * @param filePath The path of file.
     * @return the size
     */
    fun getSize(filePath: String): String {
        return getSize(getFileByPath(filePath) ?: return "")
    }

    /**
     * Return the size.
     *
     * @param file The directory.
     * @return the size
     */
    fun getSize(file: File): String {
        return if (file.isDirectory) getDirSize(file)
        else getFileSize(file)
    }

    /**
     * Return the size of directory.
     *
     * @param dir The directory.
     * @return the size of directory
     */
    private fun getDirSize(dir: File): String {
        val len = getDirLength(dir)
        return if (len == -1L) "" else UtilsBridge.byte2FitMemorySize(len)
    }

    /**
     * Return the size of file.
     *
     * @param file The file.
     * @return the length of file
     */
    private fun getFileSize(file: File): String {
        val len = getFileLength(file)
        return if (len == -1L) "" else UtilsBridge.byte2FitMemorySize(len)
    }

    /**
     * Return the length.
     *
     * @param filePath The path of file.
     * @return the length
     */
    fun getLength(filePath: String): Long {
        return getLength(getFileByPath(filePath) ?: return -1)
    }

    /**
     * Return the length.
     *
     * @param file The file.
     * @return the length
     */
    fun getLength(file: File): Long {
        return if (file.isDirectory) {
            getDirLength(file)
        } else getFileLength(file)
    }

    /**
     * Return the length of directory.
     *
     * @param dir The directory.
     * @return the length of directory
     */
    private fun getDirLength(dir: File): Long {
        if (!isDir(dir)) return 0
        var len: Long = 0
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                len += if (file.isDirectory) {
                    getDirLength(file)
                } else {
                    file.length()
                }
            }
        }
        return len
    }

    /**
     * Return the length of file.
     *
     * @param filePath The path of file.
     * @return the length of file
     */
    fun getFileLength(filePath: String): Long {
        val isURL = filePath.matches(Regex("[a-zA-z]+://[^\\s]*"))
        if (isURL) {
            try {
                val conn = URL(filePath).openConnection() as HttpsURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                return if (conn.responseCode == 200) {
                    conn.contentLength.toLong()
                } else -1
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return getFileLength(getFileByPath(filePath) ?: return -1L)
    }

    /**
     * Return the length of file.
     *
     * @param file The file.
     * @return the length of file
     */
    private fun getFileLength(file: File): Long {
        return if (!isFile(file)) -1L else file.length()
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(filePath: String): String {
        if (filePath.isBlank()) return ""
        return getFileMD5ToString(File(filePath))
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(file: File): String {
        return ConvertUtils.bytes2HexString(getFileMD5(file))
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5(filePath: String): ByteArray {
        return getFileMD5(getFileByPath(filePath) ?: return ByteArray(0))
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */

    @JvmStatic
    fun getFileMD5(file: File): ByteArray {
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
            ByteArray(0)
        } finally {
            CloseUtils.closeIOQuietly(dis)
        }
    }

    /**
     * Return the file's path of directory.
     *
     * @param file The file.
     * @return the file's path of directory
     */
    @JvmStatic
    fun getDirName(file: File): String {
        return getDirName(file.absolutePath)
    }

    /**
     * Return the file's path of directory.
     *
     * @param filePath The path of file.
     * @return the file's path of directory
     */
    @JvmStatic
    fun getDirName(filePath: String): String {
        if (filePath.isBlank()) return ""
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * Return the name of file.
     *
     * @param file The file.
     * @return the name of file
     */
    @JvmStatic
    fun getFileName(file: File): String {
        return getFileName(file.absolutePath)
    }

    /**
     * Return the name of file.
     *
     * @param filePath The path of file.
     * @return the name of file
     */
    @JvmStatic
    fun getFileName(filePath: String): String {
        if (filePath.isBlank()) return ""
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * Return the name of file without extension.
     *
     * @param file The file.
     * @return the name of file without extension
     */
    @JvmStatic
    fun getFileNameNoExtension(file: File): String {
        return getFileNameNoExtension(file.path)
    }

    /**
     * Return the name of file without extension.
     *
     * @param filePath The path of file.
     * @return the name of file without extension
     */
    @JvmStatic
    fun getFileNameNoExtension(filePath: String): String {
        if (filePath.isBlank()) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * Return the extension of file.
     *
     * @param file The file.
     * @return the extension of file
     */
    @JvmStatic
    fun getFileExtension(file: File): String {
        return getFileExtension(file.path)
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    @JvmStatic
    fun getFileExtension(filePath: String): String {
        if (filePath.isBlank()) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    /**
     * Notify system to scan the file.
     *
     * @param filePath The path of file.
     */
    @JvmStatic
    fun notifySystemToScan(filePath: String) {
        notifySystemToScan(getFileByPath(filePath))
    }

    /**
     * Notify system to scan the file.
     *
     * @param file The file.
     */
    @JvmStatic
    fun notifySystemToScan(file: File?) {
        if (file == null || !file.exists()) return
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.parse("file://" + file.absolutePath)
        Utils.app.sendBroadcast(intent)
    }

    /**
     * Return the total size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the total size of file system
     */
    @JvmStatic
    fun getFsTotalSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long = statFs.blockSizeLong
        val totalSize: Long = statFs.blockCountLong
        return blockSize * totalSize
    }

    /**
     * Return the available size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the available size of file system
     */
    @JvmStatic
    fun getFsAvailableSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long = statFs.blockSizeLong
        val availableSize: Long = statFs.availableBlocksLong
        return blockSize * availableSize
    }

    interface OnReplaceListener {
        fun onReplace(srcFile: File, destFile: File): Boolean
    }

}
