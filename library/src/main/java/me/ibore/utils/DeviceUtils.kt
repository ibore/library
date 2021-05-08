package me.ibore.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/8/1
 * desc  : utils about device
</pre> *
 */
object DeviceUtils {
    /**
     * Return whether device is rooted.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isDeviceRooted: Boolean
        get() {
            val su = "su"
            val locations = arrayOf(
                "/system/bin/",
                "/system/xbin/",
                "/sbin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/data/local/",
                "/system/sbin/",
                "/usr/bin/",
                "/vendor/bin/"
            )
            for (location in locations) {
                if (File(location + su).exists()) {
                    return true
                }
            }
            return false
        }

    /**
     * Return whether ADB is enabled.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @get:RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    val isAdbEnabled: Boolean
        get() = Settings.Secure.getInt(
            Utils.contentResolver,
            Settings.Global.ADB_ENABLED, 0
        ) > 0

    /**
     * Return the version name of device's system.
     *
     * @return the version name of device's system
     */
    val sDKVersionName: String
        get() = Build.VERSION.RELEASE

    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    val sDKVersionCode: Int
        get() = Build.VERSION.SDK_INT

    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @get:SuppressLint("HardwareIds")
    val androidID: String
        get() {
            val id = Settings.Secure.getString(
                Utils.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            return if ("9774d56d682e549c" == id) "" else id ?: ""
        }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`,
     * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @return the MAC address
     */
    @get:RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE])
    val macAddress: String
        get() {
            val macAddress = getMacAddress(*(null as Array<String?>?)!!)
            if (!TextUtils.isEmpty(macAddress) || wifiEnabled) return macAddress
            wifiEnabled = true
            wifiEnabled = false
            return getMacAddress(*(null as Array<String?>?)!!)
        }

    /**
     * Enable or disable wifi.
     *
     * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     */
    @set:RequiresPermission(permission.CHANGE_WIFI_STATE)
    private var wifiEnabled: Boolean
        get() {
            @SuppressLint("WifiManagerLeak") val manager = Utils.app
                .getSystemService(Context.WIFI_SERVICE) as WifiManager?
                ?: return false
            return manager.isWifiEnabled
        }
        private set(enabled) {
            @SuppressLint("WifiManagerLeak") val manager = Utils.app
                .getSystemService(Context.WIFI_SERVICE) as WifiManager?
                ?: return
            if (enabled == manager.isWifiEnabled) return
            manager.isWifiEnabled = enabled
        }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE])
    fun getMacAddress(vararg excepts: String?): String {
        var macAddress = macAddressByNetworkInterface
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = macAddressByInetAddress
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = macAddressByWifiInfo
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = macAddressByFile
        return if (isAddressNotInExcepts(macAddress, *excepts)) {
            macAddress
        } else ""
    }

    private fun isAddressNotInExcepts(address: String, vararg excepts: String?): Boolean {
        if (TextUtils.isEmpty(address)) {
            return false
        }
        if ("02:00:00:00:00:00" == address) {
            return false
        }
        if (excepts.isEmpty()) {
            return true
        }
        for (filter in excepts) {
            if (filter != null && filter == address) {
                return false
            }
        }
        return true
    }

    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    private val macAddressByWifiInfo: String
        get() {
            try {
                val wifi = Utils.app.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
                if (wifi != null) {
                    val info = wifi.connectionInfo
                    if (info != null) {
                        @SuppressLint("HardwareIds") val macAddress = info.macAddress
                        if (!TextUtils.isEmpty(macAddress)) {
                            return macAddress
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }
    private val macAddressByNetworkInterface: String
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (ni == null || !ni.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.isNotEmpty()) {
                        val sb = StringBuilder()
                        for (b in macBytes) {
                            sb.append(String.format("%02x:", b))
                        }
                        return sb.substring(0, sb.length - 1)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }
    private val macAddressByInetAddress: String
        get() {
            try {
                val inetAddress = inetAddress
                if (inetAddress != null) {
                    val ni = NetworkInterface.getByInetAddress(inetAddress)
                    if (ni != null) {
                        val macBytes = ni.hardwareAddress
                        if (macBytes != null && macBytes.isNotEmpty()) {
                            val sb = StringBuilder()
                            for (b in macBytes) {
                                sb.append(String.format("%02x:", b))
                            }
                            return sb.substring(0, sb.length - 1)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }

    // To prevent phone of xiaomi return "10.0.2.15"
    private val inetAddress: InetAddress?
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp) continue
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val inetAddress = addresses.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            val hostAddress = inetAddress.hostAddress
                            if (hostAddress.indexOf(':') < 0) return inetAddress
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return null
        }
    private val macAddressByFile: String
        get() {
            var result = UtilsBridge.execCmd("getprop wifi.interface", false)
            if (result.result == 0) {
                val name = result.successMsg
                if (name.isNotEmpty()) {
                    result = UtilsBridge.execCmd("cat /sys/class/net/$name/address", false)
                    if (result.result == 0) {
                        val address = result.successMsg
                        if (address.isNotEmpty()) {
                            return address
                        }
                    }
                }
            }
            return "02:00:00:00:00:00"
        }

    /**
     * Return the manufacturer of the product/hardware.
     *
     * e.g. Xiaomi
     *
     * @return the manufacturer of the product/hardware
     */
    val manufacturer: String
        get() = Build.MANUFACTURER

    /**
     * Return the model of device.
     *
     * e.g. MI2SC
     *
     * @return the model of device
     */
    val model: String
        get() {
            var model = Build.MODEL
            model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
            return model
        }

    /**
     * Return an ordered list of ABIs supported by this device. The most preferred ABI is the first
     * element in the list.
     *
     * @return an ordered list of ABIs supported by this device
     */
    val aBIs: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
            } else arrayOf(Build.CPU_ABI)
        }

    /**
     * Return whether device is tablet.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isTablet: Boolean
        get() = ((Resources.getSystem().configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)//        boolean checkDebuggerConnected = Debug.isDebuggerConnected();
//        if (checkDebuggerConnected) return true;
    /**
     * Return whether device is emulator.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isEmulator: Boolean
        get() {
            val checkProperty = (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.toLowerCase().contains("vbox")
                    || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                    || "google_sdk" == Build.PRODUCT)
            if (checkProperty) return true
            var operatorName = ""
            val tm = Utils.app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            if (tm != null) {
                val name = tm.networkOperatorName
                if (name != null) {
                    operatorName = name
                }
            }
            val checkOperatorName = operatorName.toLowerCase() == "android"
            if (checkOperatorName) return true
            val url = "tel:" + "123456"
            val intent = Intent()
            intent.data = Uri.parse(url)
            intent.action = Intent.ACTION_DIAL
            return intent.resolveActivity(Utils.packageManager) == null

//        boolean checkDebuggerConnected = Debug.isDebuggerConnected();
//        if (checkDebuggerConnected) return true;
        }

    /**
     * Whether user has enabled development settings.
     *
     * @return whether user has enabled development settings.
     */
    @get:RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    val isDevelopmentSettingsEnabled: Boolean
        get() = Settings.Global.getInt(
            Utils.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) > 0
    private const val KEY_UDID = "KEY_UDID"

    @Volatile
    private var udid: String? = null

    /**
     * Return the unique device id.
     * <pre>android 10 deprecated {prefix}{1}{UUID(macAddress)}</pre>
     * <pre>{prefix}{2}{UUID(androidId )}</pre>
     * <pre>{prefix}{9}{UUID(random    )}</pre>
     *
     * @param prefix   The prefix of the unique device id.
     * @param useCache True to use cache, false otherwise.
     * @return the unique device id
     */
    @JvmStatic
    @JvmOverloads
    fun getUniqueDeviceId(prefix: String = "", useCache: Boolean = true): String {
        if (!useCache) {
            return getUniqueDeviceIdReal(prefix)
        }
        if (udid == null) {
            synchronized(DeviceUtils::class.java) {
                if (udid == null) {
                    val id = Utils.SP.getString(KEY_UDID, null)
                    if (id != null) {
                        udid = id
                        return udid!!
                    }
                    return getUniqueDeviceIdReal(prefix)
                }
            }
        }
        return udid!!
    }

    private fun getUniqueDeviceIdReal(prefix: String): String {
        try {
            val androidId = androidID
            if (!TextUtils.isEmpty(androidId)) {
                return saveUdid(prefix + 2, androidId)
            }
        } catch (ignore: Exception) { /**/
        }
        return saveUdid(prefix + 9, "")
    }

    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET, permission.CHANGE_WIFI_STATE])
    fun isSameDevice(uniqueDeviceId: String): Boolean {
        // {prefix}{type}{32id}
        if (TextUtils.isEmpty(uniqueDeviceId) && uniqueDeviceId.length < 33) return false
        if (uniqueDeviceId == udid) return true
        val cachedId = Utils.SP.getString(KEY_UDID, null)
        if (uniqueDeviceId == cachedId) return true
        val st = uniqueDeviceId.length - 33
        val type = uniqueDeviceId.substring(st, st + 1)
        if (type.startsWith("1")) {
            val macAddress = macAddress
            return if (macAddress == "") {
                false
            } else uniqueDeviceId.substring(st + 1) == getUdid(
                "",
                macAddress
            )
        } else if (type.startsWith("2")) {
            val androidId = androidID
            return if (TextUtils.isEmpty(androidId)) {
                false
            } else uniqueDeviceId.substring(st + 1) == getUdid("", androidId)
        }
        return false
    }

    private fun saveUdid(prefix: String, id: String): String {
        udid = getUdid(prefix, id)
        Utils.SP.put(KEY_UDID, udid)
        return udid!!
    }

    private fun getUdid(prefix: String, id: String): String {
        return if (id == "") {
            prefix + UUID.randomUUID().toString().replace("-", "")
        } else prefix + UUID.nameUUIDFromBytes(id.toByteArray()).toString()
            .replace("-", "")
    }

}