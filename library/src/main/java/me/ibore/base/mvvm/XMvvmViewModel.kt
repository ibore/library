package me.ibore.base.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @Author: QuYunShuo
 * @Time: 2020/8/27
 * @Class: BaseViewModel
 * @Remark: ViewModel 基类
 */
abstract class XMvvmViewModel<R : XMvvmRepository> : ViewModel() {

    // Loading 状态
    val isLoading = MutableLiveData(false)

    // 请求异常
    val requestError = MutableLiveData<Throwable?>()

    protected val mRepository: R by lazy { initRepository() }

    protected abstract fun initRepository(): R
}