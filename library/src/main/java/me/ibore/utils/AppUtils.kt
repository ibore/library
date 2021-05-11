package me.ibore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process
import me.ibore.ktx.logD
import me.ibore.utils.Utils.OnAppStatusChangedListener
import me.ibore.utils.encrypt.EncryptUtils
import java.io.File
import java.util.*
import kotlin.system.exitProcess

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : utils about app
</pre> *
 */
object AppUtils  {
    /**
     * Register the status of application changed listener.
     *
     * @param listener The status of application changed listener
     */
    @JvmStatic
    fun registerAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        UtilsActivityLifecycleImpl.INSTANCE.addOnAppStatusChangedListener(listener)
    }

    /**
     * Unregister the status of application changed listener.
     *
     * @param listener The status of application changed listener
     */
    @JvmStatic
    fun unregisterAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        UtilsActivityLifecycleImpl.INSTANCE.removeOnAppStatusChangedListener(listener)
    }

    /**
     * Install the app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param filePath The path of file.
     */
    @JvmStatic
    fun installApp(filePath: String) {
        val file = FileUtils.getFileByPath(filePath)?:return
        installApp(file)
    }

    /**
     * Install the app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param file The file.
     */
    @JvmStatic
    fun installApp(file: File) {
        val installAppIntent = IntentUtils.getInstallAppIntent(file) ?: return
        Utils.app.startActivity(installAppIntent)
    }

    /**
     * Install the app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param uri The uri.
     */
    @JvmStatic
    fun installApp(uri: Uri) {
        val installAppIntent = IntentUtils.getInstallAppIntent(uri) ?: return
        Utils.app.startActivity(installAppIntent)
    }

    /**
     * Uninstall the app.
     *
     * Target APIs greater than 25 must hold
     * Must hold `<uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />`
     *
     * @param packageName The name of the package.
     */
    @JvmStatic
    fun uninstallApp(packageName: String) {
        if (packageName.isBlank()) return
        Utils.app.startActivity(IntentUtils.getUninstallAppIntent(packageName))
    }

    /**
     * Return whether the app is installed.
     *
     * @param pkgName The name of the package.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isAppInstalled(pkgName: String): Boolean {
        if (pkgName.isBlank()) return false
        val pm = Utils.packageManager
        return try {
            pm.getApplicationInfo(pkgName, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Return whether the application with root permission.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isAppRoot: Boolean
        get() {
            val result = UtilsBridge.execCmd("echo root", true)
            return result.result == 0
        }

    /**
     * Return whether it is a debug application.
     *
     * @param packageName The name of the package.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    @JvmOverloads
    fun isAppDebug(packageName: String = Utils.packageName): Boolean {
        if (packageName.isBlank()) return false
        val ai = Utils.applicationInfo
        return ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    /**
     * Return whether it is a system application.
     *
     * @param packageName The name of the package.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    @JvmOverloads
    fun isAppSystem(packageName: String = Utils.packageName): Boolean {
        return if (packageName.isBlank()) false else try {
            val pm = Utils.packageManager
            val ai: ApplicationInfo = pm.getApplicationInfo(packageName, 0)
            ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Return whether application is foreground.
     *
     * Target APIs greater than 21 must hold
     * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
     *
     * @param pkgName The name of the package.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    @JvmOverloads
    fun isAppForeground(pkgName: String= Utils.packageName): Boolean {
        return !UtilsBridge.isSpace(pkgName) && pkgName == UtilsBridge.foregroundProcessName
    }

    /**
     * Return whether application is running.
     *
     * @param pkgName The name of the package.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    @JvmOverloads
    fun isAppRunning(pkgName: String = Utils.packageName): Boolean {
        if (UtilsBridge.isSpace(pkgName)) return false
        val ai = Utils.applicationInfo
        val uid = ai.uid
        val am = Utils.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        if (am != null) {
            val taskInfo = am.getRunningTasks(Int.MAX_VALUE)
            if (taskInfo != null && taskInfo.size > 0) {
                for (aInfo in taskInfo) {
                    if (aInfo.baseActivity != null) {
                        if (pkgName == aInfo.baseActivity!!.packageName) {
                            return true
                        }
                    }
                }
            }
            val serviceInfo = am.getRunningServices(Int.MAX_VALUE)
            if (serviceInfo != null && serviceInfo.size > 0) {
                for (aInfo in serviceInfo) {
                    if (uid == aInfo.uid) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * Launch the application.
     *
     * @param packageName The name of the package.
     */
    @JvmStatic
    @JvmOverloads
    fun launchApp(packageName: String = Utils.packageName) {
        if (packageName.isBlank()) return
        val launchAppIntent = IntentUtils.getLaunchAppIntent(packageName)
        if (launchAppIntent == null) {
            logD("Didn't exist launcher activity.")
            return
        }
        Utils.app.startActivity(launchAppIntent)
    }

    /**
     * Relaunch the application.
     *
     * @param isKillProcess True to kill the process, false otherwise.
     */
    @JvmStatic
    @JvmOverloads
    fun relaunchApp(isKillProcess: Boolean = false) {
        val intent = IntentUtils.getLaunchAppIntent(Utils.packageName)
        if (intent == null) {
            logD("Didn't exist launcher activity.")
            return
        }
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        Utils.app.startActivity(intent)
        if (!isKillProcess) return
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
    /**
     * Launch the application's details settings.
     *
     * @param pkgName The name of the package.
     */
    /**
     * Launch the application's details settings.
     */
    @JvmStatic
    @JvmOverloads
    fun launchAppDetailsSettings(pkgName: String = Utils.packageName) {
        if (pkgName.isBlank()) return
        val intent = IntentUtils.getLaunchAppDetailsSettingsIntent(pkgName, true)
        if (!IntentUtils.isIntentAvailable(intent)) return
        Utils.app.startActivity(intent)
    }

    /**
     * Launch the application's details settings.
     *
     * @param activity    The activity.
     * @param requestCode The requestCode.
     * @param pkgName     The name of the package.
     */
    @JvmOverloads
    fun launchAppDetailsSettings(
        activity: Activity, requestCode: Int,
        pkgName: String = Utils.packageName
    ) {
        if (pkgName.isBlank()) return
        val intent = IntentUtils.getLaunchAppDetailsSettingsIntent(pkgName, false)
        if (!IntentUtils.isIntentAvailable(intent)) return
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Exit the application.
     */
    fun exitApp() {
        ActivityUtils.finishAllActivities()
        exitProcess(0)
    }

    /**
     * Return the application's icon.
     *
     * @param packageName The name of the package.
     * @return the application's icon
     */
    @JvmStatic
    @JvmOverloads
    fun getAppIcon(packageName: String = Utils.packageName): Drawable? {
        return if (packageName.isBlank()) null else try {
            val pm = Utils.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.applicationInfo?.loadIcon(pm)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Return the application's icon resource identifier.
     *
     * @param packageName The name of the package.
     * @return the application's icon resource identifier
     */
    @JvmStatic
    @JvmOverloads
    fun getAppIconId(packageName: String = Utils.packageName): Int {
        return if (packageName.isBlank()) 0 else try {
            val pm = Utils.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.applicationInfo?.icon ?: 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Return the application's package name.
     *
     * @return the application's package name
     */
    val appPackageName: String
        get() = Utils.packageName

    /**
     * Return the application's name.
     *
     * @param packageName The name of the package.
     * @return the application's name
     */
    @JvmStatic
    @JvmOverloads
    fun getAppName(packageName: String = Utils.packageName): String {
        return if (packageName.isBlank()) "" else try {
            val pm = Utils.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.applicationInfo?.loadLabel(pm)?.toString() ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Return the application's path.
     *
     * @param packageName The name of the package.
     * @return the application's path
     */
    @JvmStatic
    @JvmOverloads
    fun getAppPath(packageName: String = Utils.packageName): String? {
        return if (packageName.isBlank()) "" else try {
            val pm = Utils.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.applicationInfo?.sourceDir
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    @JvmStatic
    @JvmOverloads
    fun getAppVersionName(packageName: String = Utils.packageName): String {
        return if (packageName.isBlank()) "" else try {
            val pm = Utils.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    @JvmStatic
    @JvmOverloads
    fun getAppVersionCode(packageName: String = Utils.packageName): Int {
        return if (packageName.isBlank()) -1 else try {
            val pm = Utils.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.versionCode ?: -1
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * Return the application's signature.
     *
     * @param packageName The name of the package.
     * @return the application's signature
     */
    @SuppressLint("PackageManagerGetSignatures")
    @JvmStatic
    @JvmOverloads
    fun getAppSignatures(packageName: String = Utils.packageName): Array<Signature>? {
        return if (packageName.isBlank()) null else try {
            val pm = Utils.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val pi =
                    pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                        ?: return null
                val signingInfo = pi.signingInfo
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
            } else {
                val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                pi?.signatures ?: return null
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Return the application's signature.
     *
     * @param file The file.
     * @return the application's signature
     */
    fun getAppSignatures(file: File): Array<Signature>? {
        val pm = Utils.packageManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val pi =
                pm.getPackageArchiveInfo(
                    file.absolutePath,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                    ?: return null
            val signingInfo = pi.signingInfo
            if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
        } else {
            val pi = pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_SIGNATURES)
                ?: return null
            pi.signatures
        }
    }

    /**
     * Return the application's signature for SHA1 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for SHA1 value
     */
    @JvmStatic
    @JvmOverloads
    fun getAppSignaturesSHA1(packageName: String = Utils.packageName): List<String> {
        return getAppSignaturesHash(packageName, "SHA1")
    }

    /**
     * Return the application's signature for SHA256 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for SHA256 value
     */
    @JvmStatic
    @JvmOverloads
    fun getAppSignaturesSHA256(packageName: String = Utils.packageName): List<String> {
        return getAppSignaturesHash(packageName, "SHA256")
    }

    /**
     * Return the application's signature for MD5 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for MD5 value
     */
    @JvmStatic
    @JvmOverloads
    fun getAppSignaturesMD5(packageName: String = Utils.packageName): List<String> {
        return getAppSignaturesHash(packageName, "MD5")
    }

    /**
     * Return the application's user-ID.
     *
     * @param pkgName The name of the package.
     * @return the application's signature for MD5 value
     */
    @JvmStatic
    @JvmOverloads
    fun getAppUid(pkgName: String = Utils.packageName): Int {
        return try {
            Utils.packageManager.getApplicationInfo(pkgName, 0).uid
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    private fun getAppSignaturesHash(packageName: String, algorithm: String): List<String> {
        val result = ArrayList<String>()
        if (packageName.isBlank()) return result
        val signatures = getAppSignatures(packageName)
        if (signatures == null || signatures.isEmpty()) return result
        for (signature in signatures) {
            val hash = UtilsBridge.bytes2HexString(
                EncryptUtils.hashTemplate(signature.toByteArray(), algorithm)
            ).replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
            result.add(hash)
        }
        return result
    }

    /**
     * Return the application's information.
     *
     *  * name of package
     *  * icon
     *  * name
     *  * path of package
     *  * version name
     *  * version code
     *  * is system
     *
     *
     * @param packageName The name of the package.
     * @return the application's information
     */
    @JvmStatic
    @JvmOverloads
    fun getAppInfo(packageName: String = Utils.packageName): AppInfo? {
        return try {
            val pm = Utils.packageManager
            getBean(pm, pm.getPackageInfo(packageName, 0))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Return the applications' information.
     *
     * @return the applications' information
     */
    @JvmStatic
    @SuppressLint("QueryPermissionsNeeded")
    fun getAppInfo(): List<AppInfo> {
        val list: MutableList<AppInfo> = ArrayList()
        val pm = Utils.packageManager
        val installedPackages = pm.getInstalledPackages(0)
        for (pi in installedPackages) {
            val ai = getBean(pm, pi) ?: continue
            list.add(ai)
        }
        return list
    }

    /**
     * Return the application's package information.
     *
     * @return the application's package information
     */
    @JvmStatic
    fun getApkInfo(apkFile: File): AppInfo? {
        return if (!apkFile.isFile || !apkFile.exists()) null
        else getApkInfo(apkFile.absolutePath)
    }

    /**
     * Return the application's package information.
     *
     * @return the application's package information
     */
    @JvmStatic
    fun getApkInfo(apkFilePath: String): AppInfo? {
        if (apkFilePath.isBlank()) return null
        val pm = Utils.packageManager
        val pi = pm.getPackageArchiveInfo(apkFilePath, 0) ?: return null
        val appInfo = pi.applicationInfo
        appInfo.sourceDir = apkFilePath
        appInfo.publicSourceDir = apkFilePath
        return getBean(pm, pi)
    }

    @JvmStatic
    private fun getBean(pm: PackageManager, pi: PackageInfo?): AppInfo? {
        if (pi == null) return null
        val ai = pi.applicationInfo
        val packageName = pi.packageName
        val name = ai.loadLabel(pm).toString()
        val icon = ai.loadIcon(pm)
        val packagePath = ai.sourceDir
        val versionName = pi.versionName
        val versionCode = pi.versionCode
        val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
        return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
    }

    /**
     * The application's information.
     */
    data class AppInfo(
        val packageName: String, val appName: String, val appIcon: Drawable,
        val packagePath: String, val versionName: String, val versionCode: Int = 0,
        val isSystem: Boolean = false
    ) {

        override fun toString(): String {
            return """{packageName: $packageName appName: $appName appIcon: $appIcon
                 packagePath: $packagePath versionName: $versionName versionCode: $versionCode isSystem: $isSystem}"""
        }

    }
}
