package me.ibore.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.text.format.Formatter
import androidx.annotation.RequiresPermission
import me.ibore.utils.ShellUtils.execCmd
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : utils about network
</pre> *
 */
object NetworkUtils {
    /**
     * Open the settings of wireless.
     */
    fun openWirelessSettings() {
        Utils.app.startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    /**
     * Return whether network is connected.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: connected<br></br>`false`: disconnected
     */
    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isConnected: Boolean
        get() {
            val info = activeNetworkInfo
            return info != null && info.isConnected
        }

    /**
     * Return whether network is available.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableAsync(consumer: Utils.Consumer<Boolean>): Utils.Task<Boolean> {
        return Utils.doAsync(object : Utils.Task<Boolean>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): Boolean {
                return isAvailable
            }
        })
    }

    /**
     * Return whether network is available.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @get:RequiresPermission(permission.INTERNET)
    val isAvailable: Boolean
        get() = isAvailableByDns || isAvailableByPing(null)

    /**
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * The default ping ip: 223.5.5.5
     *
     * @param consumer The consumer.
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByPingAsync(consumer: Utils.Consumer<Boolean>) {
        isAvailableByPingAsync("", consumer)
    }

    /**
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param ip       The ip address.
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByPingAsync(
        ip: String?,
        consumer: Utils.Consumer<Boolean>
    ): Utils.Task<Boolean> {
        return Utils.doAsync(object : Utils.Task<Boolean>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): Boolean {
                return isAvailableByPing(ip)
            }
        })
    }

    /**
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * The default ping ip: 223.5.5.5
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @get:RequiresPermission(permission.INTERNET)
    val isAvailableByPing: Boolean
        get() = isAvailableByPing("")

    /**
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param ip The ip address.
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByPing(ip: String?): Boolean {
        val realIp = if (TextUtils.isEmpty(ip)) "223.5.5.5" else ip!!
        val result = execCmd(String.format("ping -c 1 %s", realIp), false)
        return result.result == 0
    }

    /**
     * Return whether network is available using domain.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param consumer The consumer.
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByDnsAsync(consumer: Utils.Consumer<Boolean>) {
        isAvailableByDnsAsync("", consumer)
    }

    /**
     * Return whether network is available using domain.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain   The name of domain.
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByDnsAsync(
        domain: String?, consumer: Utils.Consumer<Boolean>
    ): Utils.Task<Boolean> {
        return Utils.doAsync(object : Utils.Task<Boolean>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): Boolean {
                return isAvailableByDns(domain)
            }
        })
    }

    /**
     * Return whether network is available using domain.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @get:RequiresPermission(permission.INTERNET)
    val isAvailableByDns: Boolean
        get() = isAvailableByDns("")

    /**
     * Return whether network is available using domain.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain The name of domain.
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByDns(domain: String?): Boolean {
        val realDomain = if (domain.isNullOrEmpty()) "www.baidu.com" else domain
        val inetAddress: InetAddress?
        return try {
            inetAddress = InetAddress.getByName(realDomain)
            inetAddress != null
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Return whether mobile data is enabled.
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    val mobileDataEnabled: Boolean
        @SuppressLint("MissingPermission")
        get() {
            try {
                val tm = Utils.app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    ?: return false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return tm.isDataEnabled
                }
                @SuppressLint("PrivateApi") val getMobileDataEnabledMethod =
                    tm.javaClass.getDeclaredMethod("getDataEnabled")
                if (null != getMobileDataEnabledMethod) {
                    return getMobileDataEnabledMethod.invoke(tm) as Boolean
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    /**
     * Return whether using mobile data.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isMobileData: Boolean
        get() {
            val info = activeNetworkInfo
            return (null != info && info.isAvailable
                    && info.type == ConnectivityManager.TYPE_MOBILE)
        }

    /**
     * Return whether using 4G.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is4G(): Boolean {
        val info = activeNetworkInfo
        return (info != null && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
    }

    /**
     * Return whether using 4G.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is5G(): Boolean {
        val info = activeNetworkInfo
        return (info != null && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_NR)
    }
    /**
     * Return whether wifi is enabled.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    /**
     * Enable or disable wifi.
     *
     * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     */
    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    @set:RequiresPermission(permission.CHANGE_WIFI_STATE)
    var wifiEnabled: Boolean
        get() {
            @SuppressLint("WifiManagerLeak") val manager =
                Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    ?: return false
            return manager.isWifiEnabled
        }
        set(enabled) {
            @SuppressLint("WifiManagerLeak") val manager =
                Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    ?: return
            if (enabled == manager.isWifiEnabled) return
            manager.isWifiEnabled = enabled
        }

    /**
     * Return whether wifi is connected.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: connected<br></br>`false`: disconnected
     */
    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isWifiConnected: Boolean
        get() {
            val cm = Utils.app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
            val ni = cm.activeNetworkInfo
            return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
        }

    /**
     * Return whether wifi is available.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return `true`: available<br></br>`false`: unavailable
     */
    @get:RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET])
    val isWifiAvailable: Boolean
        get() = wifiEnabled && isAvailable

    /**
     * Return whether wifi is available.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET])
    fun isWifiAvailableAsync(consumer: Utils.Consumer<Boolean>): Utils.Task<Boolean> {
        return Utils.doAsync(object : Utils.Task<Boolean>(consumer) {
            @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET])
            override fun doInBackground(): Boolean {
                return isWifiAvailable
            }
        })
    }

    /**
     * Return the name of network operate.
     *
     * @return the name of network operate
     */
    val networkOperatorName: String
        get() {
            val tm = Utils.app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                ?: return ""
            return tm.networkOperatorName
        }

    /**
     * Return type of network.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return type of network
     *
     *  * [NetworkType.NETWORK_ETHERNET]
     *  * [NetworkType.NETWORK_WIFI]
     *  * [NetworkType.NETWORK_4G]
     *  * [NetworkType.NETWORK_3G]
     *  * [NetworkType.NETWORK_2G]
     *  * [NetworkType.NETWORK_UNKNOWN]
     *  * [NetworkType.NETWORK_NO]
     *
     */
    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val networkType: NetworkType
        get() {
            if (isEthernet) {
                return NetworkType.NETWORK_ETHERNET
            }
            val info = activeNetworkInfo
            return if (info != null && info.isAvailable) {
                if (info.type == ConnectivityManager.TYPE_WIFI) {
                    NetworkType.NETWORK_WIFI
                } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    when (info.subtype) {
                        TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.NETWORK_2G
                        TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.NETWORK_3G
                        TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.NETWORK_4G
                        TelephonyManager.NETWORK_TYPE_NR -> NetworkType.NETWORK_5G
                        else -> {
                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                NetworkType.NETWORK_3G
                            } else {
                                NetworkType.NETWORK_UNKNOWN
                            }
                        }
                    }
                } else {
                    NetworkType.NETWORK_UNKNOWN
                }
            } else NetworkType.NETWORK_NO
        }

    /**
     * Return whether using ethernet.
     *
     * Must hold
     * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private val isEthernet: Boolean
        get() {
            val cm = Utils.app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
            val info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
            val state = info.state ?: return false
            return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private val activeNetworkInfo: NetworkInfo?
        get() {
            val cm = Utils.app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return null
            return cm.activeNetworkInfo
        }

    /**
     * Return the ip address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param useIPv4  True to use ipv4, false otherwise.
     * @param consumer The consumer.
     * @return the task
     */
    fun getIPAddressAsync(
        useIPv4: Boolean,
        consumer: Utils.Consumer<String>
    ): Utils.Task<String> {
        return Utils.doAsync(object : Utils.Task<String>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): String {
                return getIPAddress(useIPv4)
            }
        })
    }

    /**
     * Return the ip address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(permission.INTERNET)
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    val hostAddress = add.hostAddress
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return hostAddress
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress.indexOf('%')
                            return if (index < 0) hostAddress.toUpperCase() else hostAddress.substring(
                                0,
                                index
                            ).toUpperCase()
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    val broadcastIpAddress: String
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                val adds = LinkedList<InetAddress>()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (!ni.isUp || ni.isLoopback) continue
                    val ias = ni.interfaceAddresses
                    var i = 0
                    val size = ias.size
                    while (i < size) {
                        val ia = ias[i]
                        val broadcast = ia.broadcast
                        if (broadcast != null) {
                            return broadcast.hostAddress
                        }
                        i++
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ""
        }

    /**
     * Return the domain address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain   The name of domain.
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun getDomainAddressAsync(domain: String?, consumer: Utils.Consumer<String>): Utils.Task<String> {
        return Utils.doAsync(object : Utils.Task<String>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): String {
                return getDomainAddress(domain)
            }
        })
    }

    /**
     * Return the domain address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain The name of domain.
     * @return the domain address
     */
    @RequiresPermission(permission.INTERNET)
    fun getDomainAddress(domain: String?): String {
        val inetAddress: InetAddress
        return try {
            inetAddress = InetAddress.getByName(domain)
            inetAddress.hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val ipAddressByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager? ?: return ""
            return Formatter.formatIpAddress(wm.dhcpInfo.ipAddress)
        }

    /**
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val gatewayByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    ?: return ""
            return Formatter.formatIpAddress(wm.dhcpInfo.gateway)
        }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val netMaskByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    ?: return ""
            return Formatter.formatIpAddress(wm.dhcpInfo.netmask)
        }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val serverAddressByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    ?: return ""
            return Formatter.formatIpAddress(wm.dhcpInfo.serverAddress)
        }

    /**
     * Return the ssid.
     *
     * @return the ssid.
     */
    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val sSID: String
        get() {
            val wm = Utils.app.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                ?: return ""
            val wi = wm.connectionInfo ?: return ""
            val ssid = wi.ssid
            if (TextUtils.isEmpty(ssid)) {
                return ""
            }
            return if (ssid.length > 2 && ssid[0] == '"' && ssid[ssid.length - 1] == '"') {
                ssid.substring(1, ssid.length - 1)
            } else ssid
        }

    /**
     * Register the status of network changed listener.
     *
     * @param listener The status of network changed listener
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun registerNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener?) {
        NetworkChangedReceiver.getInstance().registerListener(listener)
    }

    /**
     * Return whether the status of network changed listener has been registered.
     *
     * @param listener The listener
     * @return true to registered, false otherwise.
     */
    fun isRegisteredNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener?): Boolean {
        return NetworkChangedReceiver.Companion.getInstance().isRegistered(listener)
    }

    /**
     * Unregister the status of network changed listener.
     *
     * @param listener The status of network changed listener.
     */
    fun unregisterNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener?) {
        NetworkChangedReceiver.Companion.getInstance().unregisterListener(listener)
    }

    @get:RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_COARSE_LOCATION])
    val wifiScanResult: WifiScanResults
        get() {
            val result = WifiScanResults()
            if (!wifiEnabled) return result
            @SuppressLint("WifiManagerLeak") val wm =
                Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val results = wm.scanResults
            if (results != null) {
                result.allResults = results
            }
            return result
        }
    private const val SCAN_PERIOD_MILLIS: Long = 3000
    private val SCAN_RESULT_CONSUMERS: MutableSet<Utils.Consumer<WifiScanResults?>> =
        CopyOnWriteArraySet()
    private var sScanWifiTimer: Timer? = null
    private var sPreWifiScanResults: WifiScanResults? = null

    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE, permission.ACCESS_COARSE_LOCATION])
    fun addOnWifiChangedConsumer(consumer: Utils.Consumer<WifiScanResults?>?) {
        if (consumer == null) return
        ThreadUtils.runOnUiThread(Runnable {
            if (SCAN_RESULT_CONSUMERS.isEmpty()) {
                SCAN_RESULT_CONSUMERS.add(consumer)
                startScanWifi()
                return@Runnable
            }
            consumer.accept(sPreWifiScanResults)
            SCAN_RESULT_CONSUMERS.add(consumer)
        })
    }

    private fun startScanWifi() {
        sPreWifiScanResults = WifiScanResults()
        sScanWifiTimer = Timer()
        sScanWifiTimer!!.schedule(object : TimerTask() {
            @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE, permission.ACCESS_COARSE_LOCATION])
            override fun run() {
                startScanWifiIfEnabled()
                val scanResults = wifiScanResult
                if (isSameScanResults(sPreWifiScanResults!!.allResults, scanResults.allResults)) {
                    return
                }
                sPreWifiScanResults = scanResults
                ThreadUtils.runOnUiThread {
                    for (consumer in SCAN_RESULT_CONSUMERS) {
                        consumer.accept(sPreWifiScanResults)
                    }
                }
            }
        }, 0, SCAN_PERIOD_MILLIS)
    }

    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE])
    private fun startScanWifiIfEnabled() {
        if (!wifiEnabled) return
        @SuppressLint("WifiManagerLeak") val wm =
            Utils.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wm.startScan()
    }

    fun removeOnWifiChangedConsumer(consumer: Utils.Consumer<WifiScanResults?>?) {
        if (consumer == null) return
        ThreadUtils.runOnUiThread {
            SCAN_RESULT_CONSUMERS.remove(consumer)
            if (SCAN_RESULT_CONSUMERS.isEmpty()) {
                stopScanWifi()
            }
        }
    }

    private fun stopScanWifi() {
        if (sScanWifiTimer != null) {
            sScanWifiTimer!!.cancel()
            sScanWifiTimer = null
        }
    }

    private fun isSameScanResults(l1: List<ScanResult>?, l2: List<ScanResult>?): Boolean {
        if (l1 == null && l2 == null) {
            return true
        }
        if (l1 == null || l2 == null) {
            return false
        }
        if (l1.size != l2.size) {
            return false
        }
        for (i in l1.indices) {
            val r1 = l1[i]
            val r2 = l2[i]
            if (!isSameScanResultContent(r1, r2)) {
                return false
            }
        }
        return true
    }

    private fun isSameScanResultContent(r1: ScanResult?, r2: ScanResult?): Boolean {
        return (r1 != null && r2 != null && StringUtils.equals(r1.BSSID, r2.BSSID)
                && StringUtils.equals(r1.SSID, r2.SSID)
                && StringUtils.equals(r1.capabilities, r2.capabilities)
                && r1.level == r2.level)
    }

    enum class NetworkType {
        NETWORK_ETHERNET, NETWORK_WIFI, NETWORK_5G, NETWORK_4G, NETWORK_3G, NETWORK_2G, NETWORK_UNKNOWN, NETWORK_NO
    }

    class NetworkChangedReceiver : BroadcastReceiver() {
        private var mType: NetworkType? = null
        private val mListeners: MutableSet<OnNetworkStatusChangedListener> = HashSet()

        @RequiresPermission(permission.ACCESS_NETWORK_STATE)
        fun registerListener(listener: OnNetworkStatusChangedListener?) {
            if (listener == null) return
            ThreadUtils.runOnUiThread {
                val preSize = mListeners.size
                mListeners.add(listener)
                if (preSize == 0 && mListeners.size == 1) {
                    mType = networkType
                    val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    Utils.app.registerReceiver(getInstance(), intentFilter)
                }
            }
        }

        fun isRegistered(listener: OnNetworkStatusChangedListener?): Boolean {
            return if (listener == null) false else mListeners.contains(listener)
        }

        fun unregisterListener(listener: OnNetworkStatusChangedListener?) {
            if (listener == null) return
            ThreadUtils.runOnUiThread {
                val preSize = mListeners.size
                mListeners.remove(listener)
                if (preSize == 1 && mListeners.size == 0) {
                    Utils.app.unregisterReceiver(getInstance())
                }
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                // debouncing
                ThreadUtils.runOnUiThreadDelayed(Runnable {
                    val networkType = networkType
                    if (mType == networkType) return@Runnable
                    mType = networkType
                    if (networkType == NetworkType.NETWORK_NO) {
                        for (listener in mListeners) {
                            listener.onDisconnected()
                        }
                    } else {
                        for (listener in mListeners) {
                            listener.onConnected(networkType)
                        }
                    }
                }, 1000)
            }
        }

        private object LazyHolder {
            val INSTANCE = NetworkChangedReceiver()
        }

        companion object {
            fun getInstance(): NetworkChangedReceiver {
                return LazyHolder.INSTANCE
            }
        }
    }


    interface OnNetworkStatusChangedListener {
        fun onDisconnected()
        fun onConnected(networkType: NetworkType?)
    }

    class WifiScanResults {
        var allResults: List<ScanResult> = ArrayList()
            set(value) {
                field = value
                field = filterScanResult(field)
            }
        var filterResults: List<ScanResult> = ArrayList()
            private set

        companion object {
            private fun filterScanResult(results: List<ScanResult>?): List<ScanResult> {
                if (results == null || results.isEmpty()) {
                    return ArrayList()
                }
                val map = LinkedHashMap<String, ScanResult>(results.size)
                for (result in results) {
                    if (TextUtils.isEmpty(result.SSID)) {
                        continue
                    }
                    val resultInMap = map[result.SSID]
                    if (resultInMap != null && resultInMap.level >= result.level) {
                        continue
                    }
                    map[result.SSID] = result
                }
                return ArrayList(map.values)
            }
        }
    }
}