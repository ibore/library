package me.ibore.permissions

/**
 * 权限请求实体类，参考 [Manifest.permission]
 * https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn
 * https://developer.android.google.cn/guide/topics/permissions/overview?hl=zh-cn#normal-dangerous
 */
object Permission {
    /** 外部存储权限（特殊权限，需要 Android 11 及以上）  */
    const val MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE"

    /** 安装应用权限（特殊权限，需要 Android 8.0 及以上）  */
    const val REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"

    /** 通知栏权限（特殊权限，需要 Android 6.0 及以上，注意此权限不需要在清单文件中注册也能申请）  */
    const val NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE"

    /** 悬浮窗权限（特殊权限，需要 Android 6.0 及以上）  */
    const val SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW"

    /** 系统设置权限（特殊权限，需要 Android 6.0 及以上）  */
    const val WRITE_SETTINGS = "android.permission.WRITE_SETTINGS"

    /**
     * 读取外部存储
     */
    const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"

    /**
     * 写入外部存储
     */
    const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"

    /** 读取日历  */
    const val READ_CALENDAR = "android.permission.READ_CALENDAR"

    /** 修改日历  */
    const val WRITE_CALENDAR = "android.permission.WRITE_CALENDAR"

    /** 相机权限  */
    const val CAMERA = "android.permission.CAMERA"

    /** 读取联系人  */
    const val READ_CONTACTS = "android.permission.READ_CONTACTS"

    /** 修改联系人  */
    const val WRITE_CONTACTS = "android.permission.WRITE_CONTACTS"

    /** 访问账户列表  */
    const val GET_ACCOUNTS = "android.permission.GET_ACCOUNTS"

    /** 获取精确位置  */
    const val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"

    /** 获取粗略位置  */
    const val ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"

    /** 在后台获取位置（需要 Android 10.0 及以上）  */
    const val ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION"

    /** 读取照片中的地理位置（需要 Android 10.0 及以上） */
    const val ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION"

    /** 录音权限  */
    const val RECORD_AUDIO = "android.permission.RECORD_AUDIO"

    /** 读取电话状态  */
    const val READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"

    /** 拨打电话  */
    const val CALL_PHONE = "android.permission.CALL_PHONE"

    /** 读取通话记录  */
    const val READ_CALL_LOG = "android.permission.READ_CALL_LOG"

    /** 修改通话记录  */
    const val WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG"

    /** 添加语音邮件  */
    const val ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL"

    /** 使用SIP视频  */
    const val USE_SIP = "android.permission.USE_SIP"

    /**
     * 处理拨出电话
     */
    @Deprecated("在 Android 10 已经过时，请见：https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn#PROCESS_OUTGOING_CALLS")
    const val PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS"

    /**
     * 接听电话（需要 Android 8.0 及以上，Android 8.0 以下可以采用模拟耳机按键事件来实现接听电话，这种方式不需要权限）
     */
    const val ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS"

    /** 读取手机号码（需要 Android 8.0 及以上）  */
    const val READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS"

    /** 使用传感器  */
    const val BODY_SENSORS = "android.permission.BODY_SENSORS"

    /** 获取活动步数（需要 Android 10.0 及以上）  */
    const val ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION"

    /** 发送短信  */
    const val SEND_SMS = "android.permission.SEND_SMS"

    /** 接收短信  */
    const val RECEIVE_SMS = "android.permission.RECEIVE_SMS"

    /** 读取短信  */
    const val READ_SMS = "android.permission.READ_SMS"

    /** 接收 WAP 推送消息  */
    const val RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH"

    /** 接收彩信  */
    const val RECEIVE_MMS = "android.permission.RECEIVE_MMS"

    /** 允许呼叫应用继续在另一个应用中启动的呼叫（需要 Android 9.0 及以上）  */
    const val ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER"

    @Deprecated("在 Android 11 已经过时，请使用 {@link Permission#MANAGE_EXTERNAL_STORAGE} 权限")
    val GROUP_STORAGE = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE)

    /** 前台位置权限  */
    val GROUP_LOCATION = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

    /** 后台位置权限  */
    val GROUP_LOCATION_BACKGROUND = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_BACKGROUND_LOCATION)

    /** 日历权限  */
    val GROUP_CALENDAR = arrayOf(READ_CALENDAR, WRITE_CALENDAR)

    /** 联系人权限  */
    val GROUP_CONTACTS = arrayOf(READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS)

    /** 传感器权限  */
    val GROUP_SENSORS = arrayOf(BODY_SENSORS, ACTIVITY_RECOGNITION)
}