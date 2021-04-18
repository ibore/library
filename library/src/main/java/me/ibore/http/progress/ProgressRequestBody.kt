package me.ibore.http.progress

import me.ibore.utils.ThreadUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

open class ProgressRequestBody(requestBody: RequestBody, listener: ProgressListener, private val isUIThread: Boolean, refreshTime: Int) : RequestBody() {

    private val mRefreshTime: Int = if (refreshTime <= 0) 300 else refreshTime
    private val mRequestBody: RequestBody = requestBody
    private val mListener: ProgressListener = listener
    private val mProgress: Progress = Progress()
    private var mSink: Sink? = null

    override fun contentType(): MediaType? {
        return mRequestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mRequestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        mSink = CountingSink(sink)
        val bufferedSink = mSink!!.buffer()
        mRequestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    protected inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {

        private var totalBytesRead = 0L
        private var lastRefreshTime = 0L

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            if (mProgress.contentLength == 0L) {
                mProgress.contentLength = contentLength()
            }
            totalBytesRead += byteCount
            val curTime = System.currentTimeMillis()
            val eachTime = curTime - lastRefreshTime
            if (eachTime >= mRefreshTime || totalBytesRead == mProgress.contentLength) {
                mProgress.eachBytes = byteCount
                mProgress.currentBytes = totalBytesRead
                mProgress.intervalTime = if (eachTime != curTime) eachTime else 0
                mProgress.usedTime = mProgress.usedTime + mProgress.intervalTime
                mProgress.isFinish = totalBytesRead == mProgress.contentLength
                if (isUIThread) {
                    ThreadUtils.runOnUiThread { mListener.onProgress(mProgress) }
                } else {
                    mListener.onProgress(mProgress)
                }
                lastRefreshTime = curTime
            }
        }
    }

    companion object {

        @JvmOverloads
        fun create(requestBody: RequestBody, listener: ProgressListener, isUIThread: Boolean, refreshTime: Int = 300): ProgressRequestBody {
            return ProgressRequestBody(requestBody, listener, isUIThread, refreshTime)
        }
    }
}
