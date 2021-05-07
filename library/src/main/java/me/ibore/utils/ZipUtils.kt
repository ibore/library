package me.ibore.utils

import me.ibore.ktx.logD
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * utils about zip
 */
object ZipUtils {

    private const val BUFFER_LEN = 8192

    /**
     * Zip the files.
     *
     * @param srcFilePaths The paths of source files.
     * @param zipFilePath  The path of ZIP file.
     * @param comment      The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFiles(
        srcFilePaths: Collection<String>, zipFilePath: String, comment: String? = null
    ): Boolean {
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFilePath))
            for (srcFilePath in srcFilePaths) {
                val srcFile = FileUtils.getFileByPath(srcFilePath) ?: return false
                if (!zipFile(srcFile, "", zos, comment)) return false
            }
            true
        } finally {
            if (zos != null) {
                zos.finish()
                zos.close()
            }
        }
    }

    /**
     * Zip the files.
     *
     * @param srcFiles The source of files.
     * @param zipFile  The ZIP file.
     * @param comment  The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFiles(srcFiles: Collection<File>, zipFile: File, comment: String? = null): Boolean {
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFile))
            for (srcFile in srcFiles) {
                if (!zipFile(srcFile, "", zos, comment)) return false
            }
            true
        } finally {
            if (zos != null) {
                zos.finish()
                zos.close()
            }
        }
    }

    /**
     * Zip the file.
     *
     * @param srcFilePath The path of source file.
     * @param zipFilePath The path of ZIP file.
     * @param comment     The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFile(srcFilePath: String, zipFilePath: String, comment: String? = null): Boolean {
        val srcFile: File = FileUtils.getFileByPath(srcFilePath) ?: return false
        val zipFile: File = FileUtils.getFileByPath(zipFilePath) ?: return false
        return zipFile(srcFile, zipFile, comment)
    }

    /**
     * Zip the file.
     *
     * @param srcFile The source of file.
     * @param zipFile The ZIP file.
     * @param comment The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFile(srcFile: File, zipFile: File, comment: String? = null): Boolean {
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFile))
            zipFile(srcFile, "", zos, comment)
        } finally {
            zos?.close()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    private fun zipFile(
        srcFile: File, rootPath: String, zos: ZipOutputStream, comment: String? = null
    ): Boolean {
        var rootPathTemp = rootPath
        if (rootPathTemp.isNotBlank()) rootPathTemp = rootPathTemp + File.separator + srcFile.name
        if (srcFile.isDirectory) {
            val fileList = srcFile.listFiles()
            if (fileList == null || fileList.isEmpty()) {
                val entry = ZipEntry("$rootPathTemp/")
                entry.comment = comment
                zos.putNextEntry(entry)
                zos.closeEntry()
            } else {
                for (file in fileList) {
                    if (!zipFile(file, rootPathTemp, zos, comment)) return false
                }
            }
        } else {
            var inputStream: InputStream? = null
            try {
                inputStream = BufferedInputStream(FileInputStream(srcFile))
                val entry = ZipEntry(rootPathTemp)
                entry.comment = comment
                zos.putNextEntry(entry)
                val buffer = ByteArray(BUFFER_LEN)
                var len: Int
                while (inputStream.read(buffer, 0, BUFFER_LEN).also { len = it } != -1) {
                    zos.write(buffer, 0, len)
                }
                zos.closeEntry()
            } finally {
                inputStream?.close()
            }
        }
        return true
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFilePath The path of ZIP file.
     * @param destDirPath The path of destination directory.
     * @param keyword     The keyboard.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun unzipFile(zipFilePath: String, destDirPath: String, keyword: String? = null): List<File> {
        val zipFile: File = FileUtils.getFileByPath(zipFilePath) ?: return emptyList()
        val destDir: File = FileUtils.getFileByPath(destDirPath) ?: return emptyList()
        return unzipFile(zipFile, destDir, keyword)
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFile The ZIP file.
     * @param destDir The destination directory.
     * @param keyword The keyboard.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun unzipFile(zipFile: File, destDir: File, keyword: String? = null): List<File> {
        val files: MutableList<File> = ArrayList()
        val zip = ZipFile(zipFile)
        val entries: Enumeration<*> = zip.entries()
        zip.use {
            if (keyword.isNullOrEmpty()) {
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement() as ZipEntry
                    val entryName = entry.name.replace("\\", "/")
                    if (entryName.contains("../")) {
                        logD("entryName: $entryName is dangerous!")
                        continue
                    }
                    if (!unzipChildFile(destDir, files, it, entry, entryName)) return files
                }
            } else {
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement() as ZipEntry
                    val entryName = entry.name.replace("\\", "/")
                    if (entryName.contains("../")) {
                        logD("entryName: $entryName is dangerous!")
                        continue
                    }
                    if (entryName.contains(keyword)) {
                        if (!unzipChildFile(destDir, files, it, entry, entryName)) return files
                    }
                }
            }
        }
        return files
    }

    @JvmStatic
    @Throws(IOException::class)
    private fun unzipChildFile(
        destDir: File, files: MutableList<File>, zip: ZipFile, entry: ZipEntry, name: String
    ): Boolean {
        val file = File(destDir, name)
        files.add(file)
        if (entry.isDirectory) {
            return FileUtils.createOrExistsDir(file)
        } else {
            if (!FileUtils.createOrExistsFile(file)) return false
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                inputStream = BufferedInputStream(zip.getInputStream(entry))
                outputStream = BufferedOutputStream(FileOutputStream(file))
                val buffer = ByteArray(BUFFER_LEN)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    outputStream.write(buffer, 0, len)
                }
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
        return true
    }

    /**
     * Return the files' path in ZIP file.
     *
     * @param zipFilePath The path of ZIP file.
     * @return the files' path in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getFilesPath(zipFilePath: String): List<String> {
        return getFilesPath(FileUtils.getFileByPath(zipFilePath) ?: return emptyList())
    }

    /**
     * Return the files' path in ZIP file.
     *
     * @param zipFile The ZIP file.
     * @return the files' path in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getFilesPath(zipFile: File): List<String> {
        val paths: MutableList<String> = ArrayList()
        val zip = ZipFile(zipFile)
        val entries: Enumeration<*> = zip.entries()
        while (entries.hasMoreElements()) {
            val entryName = (entries.nextElement() as ZipEntry).name.replace("\\", "/")
            if (entryName.contains("../")) {
                logD("entryName: $entryName is dangerous!")
                paths.add(entryName)
            } else {
                paths.add(entryName)
            }
        }
        zip.close()
        return paths
    }

    /**
     * Return the files' comment in ZIP file.
     *
     * @param zipFilePath The path of ZIP file.
     * @return the files' comment in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getComments(zipFilePath: String): List<String> {
        return getComments(FileUtils.getFileByPath(zipFilePath) ?: return emptyList())
    }

    /**
     * Return the files' comment in ZIP file.
     *
     * @param zipFile The ZIP file.
     * @return the files' comment in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getComments(zipFile: File): List<String> {
        val comments: MutableList<String> = ArrayList()
        val zip = ZipFile(zipFile)
        val entries: Enumeration<*> = zip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            comments.add(entry.comment)
        }
        zip.close()
        return comments
    }
}