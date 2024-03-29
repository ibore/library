package me.ibore.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * intent
 */
object IntentUtils {
    /**
     * Return whether the intent is available.
     *
     * @param intent The intent.
     * @return `true`: yes<br></br>`false`: no
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun isIntentAvailable(intent: Intent): Boolean {
        return Utils.app
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size > 0
    }

    /**
     * Return the intent of install app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param filePath The path of file.
     * @return the intent of install app
     */
    fun getInstallAppIntent(filePath: String): Intent? {
        return getInstallAppIntent(FileUtils.getFileByPath(filePath)?:return null)
    }

    /**
     * Return the intent of install app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param file The file.
     * @return the intent of install app
     */
    fun getInstallAppIntent(file: File): Intent? {
        if (!FileUtils.isFileExists(file)) return null
        val uri: Uri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(file)
        } else {
            val authority = Utils.packageName + ".provider"
            FileProvider.getUriForFile(Utils.app, authority, file)
        }
        return getInstallAppIntent(uri)
    }

    /**
     * Return the intent of install app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param uri The uri.
     * @return the intent of install app
     */
    fun getInstallAppIntent(uri: Uri): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val type = "application/vnd.android.package-archive"
        intent.setDataAndType(uri, type)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * Return the intent of uninstall app.
     *
     * Target APIs greater than 25 must hold
     * Must hold `<uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />`
     *
     * @param pkgName The name of the package.
     * @return the intent of uninstall app
     */
    fun getUninstallAppIntent(pkgName: String): Intent {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$pkgName")
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * Return the intent of launch app.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app
     */
    fun getLaunchAppIntent(pkgName: String): Intent? {
        val launcherActivity = ActivityUtils.getLauncherActivity(pkgName)
        if (launcherActivity.isBlank()) return null
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setClassName(pkgName?:"", launcherActivity)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app details settings
     */
    @JvmStatic
    @JvmOverloads
    fun getLaunchAppDetailsSettingsIntent(pkgName: String?, isNewTask: Boolean = false): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$pkgName")
        return getIntent(intent, isNewTask)
    }

    /**
     * Return the intent of share image.
     *
     * @param content   The content.
     * @param imagePath The path of image.
     * @return the intent of share image
     */
    fun getShareIntent(text: String = "", vararg imagePaths: String): Intent {
        val imageUris = ArrayList<Uri>()
        for (imagePath in imagePaths) {
            val imageUri = UriUtils.file2Uri(FileUtils.getFileByPath(imagePath)) ?: continue
            imageUris.add(imageUri)
        }
        return getShareIntent(text, imageUris)
    }

    fun getShareIntent(text: String = "", vararg imageFiles: File): Intent {
        val imageUris = ArrayList<Uri>()
        for (imageFile in imageFiles) {
            val imageUri = UriUtils.file2Uri(imageFile) ?: continue
            imageUris.add(imageUri)
        }
        return getShareIntent(text, imageUris)
    }

    fun getShareIntent(text: String = "", vararg imageUris: Uri): Intent {
        return getShareIntent(text, ArrayList(imageUris.toMutableList()))
    }

    fun getShareIntent(text: String = "", imageUris: ArrayList<Uri>): Intent {
        var intent = Intent(Intent.ACTION_SEND)
        if (text.isNotBlank()) intent.putExtra(Intent.EXTRA_TEXT, text)
        when {
            imageUris.isEmpty() -> {
                intent.type = "text/plain"
            }
            imageUris.size == 1 -> {
                intent.putExtra(Intent.EXTRA_STREAM, imageUris[0])
                intent.type = "image/*"
            }
            else -> {
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                intent.type = "image/*"
            }
        }
        intent = Intent.createChooser(intent, "")
        return getIntent(intent, true)
    }


    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @return the intent of component
     */
    fun getComponentIntent(pkgName: String?, className: String?): Intent {
        return getComponentIntent(pkgName, className, null, false)
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    fun getComponentIntent(
        pkgName: String?,
        className: String?,
        isNewTask: Boolean
    ): Intent {
        return getComponentIntent(pkgName, className, null, isNewTask)
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @return the intent of component
     */
    fun getComponentIntent(
        pkgName: String?,
        className: String?,
        bundle: Bundle?
    ): Intent {
        return getComponentIntent(pkgName, className, bundle, false)
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    fun getComponentIntent(
        pkgName: String?,
        className: String?,
        bundle: Bundle?,
        isNewTask: Boolean
    ): Intent {
        val intent = Intent()
        if (bundle != null) intent.putExtras(bundle)
        val cn = ComponentName(pkgName!!, className!!)
        intent.component = cn
        return getIntent(intent, isNewTask)
    }

    /**
     * Return the intent of shutdown.
     *
     * Requires root permission
     * or hold `android:sharedUserId="android.uid.system"`,
     * `<uses-permission android:name="android.permission.SHUTDOWN" />`
     * in manifest.
     *
     * @return the intent of shutdown
     */
    val shutdownIntent: Intent
        get() {
            val intent: Intent
            intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Intent.ACTION_SHUTDOWN)
            } else {
                Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN")
            }
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    /**
     * Return the intent of dial.
     *
     * @param phoneNumber The phone number.
     * @return the intent of dial
     */
    fun getDialIntent(phoneNumber: String?): Intent {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        return getIntent(intent, true)
    }

    /**
     * Return the intent of call.
     *
     * Must hold `<uses-permission android:name="android.permission.CALL_PHONE" />`
     *
     * @param phoneNumber The phone number.
     * @return the intent of call
     */
    @RequiresPermission(permission.CALL_PHONE)
    fun getCallIntent(phoneNumber: String?): Intent {
        val intent = Intent("android.intent.action.CALL", Uri.parse("tel:$phoneNumber"))
        return getIntent(intent, true)
    }

    /**
     * Return the intent of send SMS.
     *
     * @param phoneNumber The phone number.
     * @param content     The content of SMS.
     * @return the intent of send SMS
     */
    fun getSendSmsIntent(phoneNumber: String?, content: String?): Intent {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", content)
        return getIntent(intent, true)
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri The uri of output.
     * @return the intent of capture
     */
    fun getCaptureIntent(outUri: Uri?): Intent {
        return getCaptureIntent(outUri, false)
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri    The uri of output.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of capture
     */
    fun getCaptureIntent(outUri: Uri?, isNewTask: Boolean): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return getIntent(intent, isNewTask)
    }

    private fun getIntent(intent: Intent, isNewTask: Boolean): Intent {
        return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
    }

    /**
     * 获取选择照片的 Intent
     *
     * @return
     */
    fun getPickIntentWithGallery(): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        return intent.setType("image*//*")
    }

    /**
     * 获取从文件中选择照片的 Intent
     *
     * @return
     */
    fun getPickIntentWithDocuments(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        return intent.setType("image*//*")
    }

    fun buildImageGetIntent(
        saveTo: Uri?,
        outputX: Int,
        outputY: Int,
        returnData: Boolean
    ): Intent? {
        return buildImageGetIntent(saveTo, 1, 1, outputX, outputY, returnData)
    }

    fun buildImageGetIntent(
        saveTo: Uri?, aspectX: Int, aspectY: Int,
        outputX: Int, outputY: Int, returnData: Boolean
    ): Intent {
        val intent = Intent()
        if (Build.VERSION.SDK_INT < 19) {
            intent.action = Intent.ACTION_GET_CONTENT
        } else {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        }
        intent.type = "image*//*"
        intent.putExtra("output", saveTo)
        intent.putExtra("aspectX", aspectX)
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("outputX", outputX)
        intent.putExtra("outputY", outputY)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", returnData)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
        return intent
    }

    fun buildImageCropIntent(
        uriFrom: Uri?,
        uriTo: Uri?,
        outputX: Int,
        outputY: Int,
        returnData: Boolean
    ): Intent {
        return buildImageCropIntent(uriFrom, uriTo, 1, 1, outputX, outputY, returnData)
    }

    fun buildImageCropIntent(
        uriFrom: Uri?, uriTo: Uri?, aspectX: Int, aspectY: Int,
        outputX: Int, outputY: Int, returnData: Boolean
    ): Intent {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uriFrom, "image*//*")
        intent.putExtra("crop", "true")
        intent.putExtra("output", uriTo)
        intent.putExtra("aspectX", aspectX)
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("outputX", outputX)
        intent.putExtra("outputY", outputY)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", returnData)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
        return intent
    }

    fun buildImageCaptureIntent(uri: Uri?): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        return intent
    }
}