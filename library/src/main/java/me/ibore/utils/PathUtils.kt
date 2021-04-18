package me.ibore.utils

import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.File

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/04/15
 * desc  : utils about path
</pre> *
 */
object PathUtils {

    private val SEP = File.separatorChar

    /**
     * Join the path.
     *
     * @param parent The parent of path.
     * @param child  The child path.
     * @return the path
     */
    fun join(parent: String?, child: String): String? {
        var parentTemp = parent
        if (TextUtils.isEmpty(child)) return parentTemp
        if (parentTemp == null) {
            parentTemp = ""
        }
        val len = parentTemp.length
        val legalSegment = getLegalSegment(child)
        return when {
            len == 0 -> {
                SEP.toString() + legalSegment
            }
            parentTemp[len - 1] == SEP -> {
                parentTemp + legalSegment
            }
            else -> {
                parentTemp + SEP + legalSegment
            }
        }
    }

    private fun getLegalSegment(segment: String): String {
        var st = -1
        var end = -1
        val charArray = segment.toCharArray()
        for (i in charArray.indices) {
            val c = charArray[i]
            if (c != SEP) {
                if (st == -1) {
                    st = i
                }
                end = i
            }
        }
        if (st in 0..end) {
            return segment.substring(st, end + 1)
        }
        throw IllegalArgumentException("segment of <$segment> is illegal")
    }

    /**
     * Return the path of /system.
     *
     * @return the path of /system
     */
    val rootPath: String
        get() = getAbsolutePath(Environment.getRootDirectory())

    /**
     * Return the path of /data.
     *
     * @return the path of /data
     */
    val dataPath: String
        get() = getAbsolutePath(Environment.getDataDirectory())

    /**
     * Return the path of /cache.
     *
     * @return the path of /cache
     */
    val downloadCachePath: String
        get() = getAbsolutePath(Environment.getDownloadCacheDirectory())

    /**
     * Return the path of /data/data/package.
     *
     * @return the path of /data/data/package
     */
    val internalAppDataPath: String
        get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Utils.app.applicationInfo.dataDir
        } else getAbsolutePath(Utils.app.dataDir)

    /**
     * Return the path of /data/data/package/code_cache.
     *
     * @return the path of /data/data/package/code_cache
     */
    val internalAppCodeCacheDir: String
        get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Utils.app.applicationInfo.dataDir + "/code_cache"
        } else getAbsolutePath(Utils.app.codeCacheDir)

    /**
     * Return the path of /data/data/package/cache.
     *
     * @return the path of /data/data/package/cache
     */
    val internalAppCachePath: String
        get() = getAbsolutePath(Utils.app.cacheDir)

    /**
     * Return the path of /data/data/package/databases.
     *
     * @return the path of /data/data/package/databases
     */
    val internalAppDbsPath: String
        get() = Utils.app.applicationInfo.dataDir + "/databases"

    /**
     * Return the path of /data/data/package/databases/name.
     *
     * @param name The name of database.
     * @return the path of /data/data/package/databases/name
     */
    fun getInternalAppDbPath(name: String?): String {
        return getAbsolutePath(Utils.app.getDatabasePath(name))
    }

    /**
     * Return the path of /data/data/package/files.
     *
     * @return the path of /data/data/package/files
     */
    val internalAppFilesPath: String
        get() = getAbsolutePath(Utils.app.filesDir)

    /**
     * Return the path of /data/data/package/shared_prefs.
     *
     * @return the path of /data/data/package/shared_prefs
     */
    val internalAppSpPath: String
        get() = Utils.app.applicationInfo.dataDir + "/shared_prefs"

    /**
     * Return the path of /data/data/package/no_backup.
     *
     * @return the path of /data/data/package/no_backup
     */
    val internalAppNoBackupFilesPath: String
        get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Utils.app.applicationInfo.dataDir + "/no_backup"
        } else getAbsolutePath(Utils.app.noBackupFilesDir)

    /**
     * Return the path of /storage/emulated/0.
     *
     * @return the path of /storage/emulated/0
     */
    val externalStoragePath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(Environment.getExternalStorageDirectory())

    /**
     * Return the path of /storage/emulated/0/Music.
     *
     * @return the path of /storage/emulated/0/Music
     */
    val externalMusicPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC
            )
        )

    /**
     * Return the path of /storage/emulated/0/Podcasts.
     *
     * @return the path of /storage/emulated/0/Podcasts
     */
    val externalPodcastsPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PODCASTS
            )
        )

    /**
     * Return the path of /storage/emulated/0/Ringtones.
     *
     * @return the path of /storage/emulated/0/Ringtones
     */
    val externalRingtonesPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES
            )
        )

    /**
     * Return the path of /storage/emulated/0/Alarms.
     *
     * @return the path of /storage/emulated/0/Alarms
     */
    val externalAlarmsPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_ALARMS
            )
        )

    /**
     * Return the path of /storage/emulated/0/Notifications.
     *
     * @return the path of /storage/emulated/0/Notifications
     */
    val externalNotificationsPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_NOTIFICATIONS
            )
        )

    /**
     * Return the path of /storage/emulated/0/Pictures.
     *
     * @return the path of /storage/emulated/0/Pictures
     */
    val externalPicturesPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
        )

    /**
     * Return the path of /storage/emulated/0/Movies.
     *
     * @return the path of /storage/emulated/0/Movies
     */
    val externalMoviesPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            )
        )

    /**
     * Return the path of /storage/emulated/0/Download.
     *
     * @return the path of /storage/emulated/0/Download
     */
    val externalDownloadsPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
        )

    /**
     * Return the path of /storage/emulated/0/DCIM.
     *
     * @return the path of /storage/emulated/0/DCIM
     */
    val externalDcimPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            )
        )

    /**
     * Return the path of /storage/emulated/0/Documents.
     *
     * @return the path of /storage/emulated/0/Documents
     */
    val externalDocumentsPath: String
        get() {
            if (!SDCardUtils.isSDCardEnableByEnvironment) return ""
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                getAbsolutePath(Environment.getExternalStorageDirectory()) + "/Documents"
            } else getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
        }

    /**
     * Return the path of /storage/emulated/0/Android/data/package.
     *
     * @return the path of /storage/emulated/0/Android/data/package
     */
    val externalAppDataPath: String
        get() {
            if (!SDCardUtils.isSDCardEnableByEnvironment) return ""
            val externalCacheDir = Utils.app.externalCacheDir ?: return ""
            return getAbsolutePath(externalCacheDir.parentFile)
        }

    /**
     * Return the path of /storage/emulated/0/Android/data/package/cache.
     *
     * @return the path of /storage/emulated/0/Android/data/package/cache
     */
    val externalAppCachePath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(Utils.app.externalCacheDir)

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files
     */
    val externalAppFilesPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(null)
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Music.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Music
     */
    val externalAppMusicPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_MUSIC
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Podcasts.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Podcasts
     */
    val externalAppPodcastsPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_PODCASTS
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Ringtones.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Ringtones
     */
    val externalAppRingtonesPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_RINGTONES
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Alarms.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Alarms
     */
    val externalAppAlarmsPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_ALARMS
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Notifications.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Notifications
     */
    val externalAppNotificationsPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_NOTIFICATIONS
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Pictures.
     *
     * @return path of /storage/emulated/0/Android/data/package/files/Pictures
     */
    val externalAppPicturesPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Movies.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Movies
     */
    val externalAppMoviesPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_MOVIES
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Download.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Download
     */
    val externalAppDownloadPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/DCIM.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/DCIM
     */
    val externalAppDcimPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(
            Utils.app.getExternalFilesDir(
                Environment.DIRECTORY_DCIM
            )
        )

    /**
     * Return the path of /storage/emulated/0/Android/data/package/files/Documents.
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Documents
     */
    val externalAppDocumentsPath: String
        get() {
            if (!SDCardUtils.isSDCardEnableByEnvironment) return ""
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                getAbsolutePath(Utils.app.getExternalFilesDir(null)) + "/Documents"
            } else getAbsolutePath(
                Utils.app.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            )
        }

    /**
     * Return the path of /storage/emulated/0/Android/obb/package.
     *
     * @return the path of /storage/emulated/0/Android/obb/package
     */
    val externalAppObbPath: String
        get() = if (!SDCardUtils.isSDCardEnableByEnvironment) "" else getAbsolutePath(Utils.app.obbDir)

    val rootPathExternalFirst: String
        get() {
            var rootPath = externalStoragePath
            if (TextUtils.isEmpty(rootPath)) {
                rootPath = this.rootPath
            }
            return rootPath
        }

    val appDataPathExternalFirst: String
        get() {
            var appDataPath = externalAppDataPath
            if (TextUtils.isEmpty(appDataPath)) {
                appDataPath = internalAppDataPath
            }
            return appDataPath
        }

    val filesPathExternalFirst: String
        get() {
            var filePath = externalAppFilesPath
            if (TextUtils.isEmpty(filePath)) {
                filePath = internalAppFilesPath
            }
            return filePath
        }

    val cachePathExternalFirst: String
        get() {
            var appPath = externalAppCachePath
            if (TextUtils.isEmpty(appPath)) {
                appPath = internalAppCachePath
            }
            return appPath
        }

    private fun getAbsolutePath(file: File?): String {
        return if (file == null) "" else file.absolutePath
    }
}