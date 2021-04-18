package me.ibore.http.progress

import me.ibore.utils.ThreadUtils
import me.ibore.utils.UtilsBridge
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class ProgressResponseBody private constructor(responseBody: ResponseBody?, listener: ProgressListener?, private val isUIThread: Boolean, refreshTime: Int) : ResponseBody() {

    private val mResponseBody = checkNotNull(responseBody, { "responseBody can not null" })
    private val mListener: ProgressListener = checkNotNull(listener, { "ProgressListener can not null" })
    private val mRefreshTime: Int = if (refreshTime <= 0) 300 else refreshTime
    private val mProgress: Progress = Progress()

    override fun contentType(): MediaType? {
        return mResponseBody.contentType()
    }

    override fun contentLength(): Long {
        return mResponseBody.contentLength()
    }

    override fun source(): BufferedSource {
        return CountingSource(mResponseBody.source()).buffer()
    }

    inner class CountingSource internal constructor(delegate: Source) : ForwardingSource(delegate) {

        private var totalBytesRead = 0L
        private var lastRefreshTime = 0L

        @Throws(IOException::class)
        override fun read(sink: Buffer, byteCount: Long): Long {
            val bytesRead = super.read(sink, byteCount)
            if (mProgress.contentLength == 0L) {
                mProgress.contentLength = contentLength()
            }
            val eachBytes = if (bytesRead != -1L) bytesRead else 0
            totalBytesRead += eachBytes
            val curTime = System.currentTimeMillis()
            val eachTime = curTime - lastRefreshTime
            if (eachTime >= mRefreshTime || bytesRead == -1L || totalBytesRead == mProgress.contentLength) {
                mProgress.eachBytes = eachBytes
                mProgress.currentBytes = totalBytesRead
                mProgress.intervalTime = if (eachTime != curTime) eachTime else 0
                mProgress.usedTime = mProgress.usedTime + mProgress.intervalTime
                mProgress.isFinish = bytesRead == -1L && totalBytesRead == mProgress.contentLength
                if (isUIThread) {
                    ThreadUtils.runOnUiThread { mListener.onProgress(mProgress) }
                } else {
                    mListener.onProgress(mProgress)
                }
                lastRefreshTime = curTime
            }
            return bytesRead
        }
    }

    companion object {
        @JvmOverloads
        fun create(responseBody: ResponseBody?, listener: ProgressListener?, isUIThread: Boolean, refreshTime: Int = 300): ProgressResponseBody {
            return ProgressResponseBody(responseBody, listener, isUIThread, refreshTime)
        }
    }
}
