package me.ibore.http.progress

/**
 * description: OKHttp进度回调类
 * author: Ibore Xie
 * date: 2018-01-19 00:04
 * website: ibore.me
 */

interface ProgressListener {

    fun onProgress(progress: Progress)

}
