package me.ibore.http.progress

class Progress  {

    /**
     * 当前已上传或下载的总长度
     */
    var currentBytes: Long = 0
        internal set
    /**
     * 数据总长度
     */
    var contentLength: Long = 0
        internal set
    /**
     * 本次调用距离上一次被调用所间隔的时间(毫秒)
     */
    var intervalTime: Long = 0
        internal set
    /**
     * 本次调用距离上一次被调用的间隔时间内上传或下载的byte长度
     */
    var eachBytes: Long = 0
        internal set
    /**
     * 下载所花费的时间(毫秒)
     */
    var usedTime: Long = 0
        internal set
    /**
     * 进度是否完成
     */
    var isFinish: Boolean = false
        internal set

    var tag: Any? = null

    /**
     * 获取百分比,该计算舍去了小数点,如果你想得到更精确的值,请自行计算
     *
     * @return 获取已下载百分比
     */
    val percent: Int get() = if (currentBytes <= 0 || contentLength <= 0) 0 else (100 * currentBytes / contentLength).toInt()

    /**
     * 获取上传或下载网络速度,单位为byte/s,如果你想得到更精确的值,请自行计算
     *
     * @return byte/s
     */
    val speed: Long get() = if (eachBytes <= 0 || intervalTime <= 0) 0 else eachBytes * 1000 / intervalTime

    override fun toString(): String {
        return "Progress(percent=$percent, speed=$speed, currentBytes=$currentBytes, contentLength=$contentLength, intervalTime=$intervalTime, eachBytes=$eachBytes, usedTime=$usedTime, isFinish=$isFinish, tag=${tag.toString()})"
    }

}
