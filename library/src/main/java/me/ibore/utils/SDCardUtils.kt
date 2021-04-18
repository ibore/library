package me.ibore.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.format.Formatter
import me.ibore.utils.UtilsBridge.getFsAvailableSize
import me.ibore.utils.UtilsBridge.getFsTotalSize
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/11
 * desc  : utils about sdcard
</pre> *
 */
object SDCardUtils {
    /**
     * Return whether sdcard is enabled by environment.
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    val isSDCardEnableByEnvironment: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    /**
     * Return the path of sdcard by environment.
     *
     * @return the path of sdcard by environment
     */
    val sDCardPathByEnvironment: String
        get() = if (isSDCardEnableByEnvironment) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""

    /**
     * Return the information of sdcard.
     *
     * @return the information of sdcard
     */
    val sDCardInfo: List<SDCardInfo>
        get() {
            val paths: MutableList<SDCardInfo> = ArrayList()
            val sm = Utils.app.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                ?: return paths
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val storageVolumes = sm.storageVolumes
                try {
                    val getPathMethod = StorageVolume::class.java.getMethod("getPath")
                    for (storageVolume in storageVolumes) {
                        val isRemovable = storageVolume.isRemovable
                        val state = storageVolume.state
                        val path = getPathMethod.invoke(storageVolume) as String
                        paths.add(SDCardInfo(path, state, isRemovable))
                    }
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
                    val getPathMethod = storageVolumeClazz.getMethod("getPath")
                    val isRemovableMethod = storageVolumeClazz.getMethod("isRemovable")
                    val getVolumeStateMethod =
                        StorageManager::class.java.getMethod("getVolumeState", String::class.java)
                    val getVolumeListMethod = StorageManager::class.java.getMethod("getVolumeList")
                    val result = getVolumeListMethod.invoke(sm)!!
                    val length = Array.getLength(result)
                    for (i in 0 until length) {
                        val storageVolumeElement = Array.get(result, i)
                        val path = getPathMethod.invoke(storageVolumeElement) as String
                        val isRemovable = isRemovableMethod.invoke(storageVolumeElement) as Boolean
                        val state = getVolumeStateMethod.invoke(sm, path) as String
                        paths.add(SDCardInfo(path, state, isRemovable))
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
            return paths
        }

    /**
     * Return the ptah of mounted sdcard.
     *
     * @return the ptah of mounted sdcard.
     */
    val mountedSDCardPath: List<String>
        get() {
            val path: MutableList<String> = ArrayList()
            val sdCardInfo = this.sDCardInfo
            if (sdCardInfo.isEmpty()) return path
            for (cardInfo in sdCardInfo) {
                val state = cardInfo.state ?: continue
                if ("mounted" == state.toLowerCase()) {
                    path.add(cardInfo.path)
                }
            }
            return path
        }

    /**
     * Return the total size of external storage
     *
     * @return the total size of external storage
     */
    val externalTotalSize: Long
        get() = getFsTotalSize(sDCardPathByEnvironment)

    /**
     * Return the available size of external storage.
     *
     * @return the available size of external storage
     */
    val externalAvailableSize: Long
        get() = getFsAvailableSize(sDCardPathByEnvironment)

    /**
     * Return the total size of internal storage
     *
     * @return the total size of internal storage
     */
    val internalTotalSize: Long
        get() = getFsTotalSize(Environment.getDataDirectory().absolutePath)

    /**
     * Return the available size of internal storage.
     *
     * @return the available size of internal storage
     */
    val internalAvailableSize: Long
        get() = getFsAvailableSize(Environment.getDataDirectory().absolutePath)

    class SDCardInfo internal constructor(
        val path: String,
        val state: String,
        val isRemovable: Boolean
    ) {
        val totalSize: Long = getFsTotalSize(path)
        val availableSize: Long = getFsAvailableSize(path)
        override fun toString(): String {
            return "SDCardInfo {" +
                    "path = " + path +
                    ", state = " + state +
                    ", isRemovable = " + isRemovable +
                    ", totalSize = " + Formatter.formatFileSize(Utils.app, totalSize) +
                    ", availableSize = " + Formatter.formatFileSize(Utils.app, availableSize) +
                    '}'
        }

    }
}