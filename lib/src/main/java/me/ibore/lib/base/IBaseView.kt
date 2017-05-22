package me.ibore.lib.base

import android.os.Bundle

/**
 * Created by Administrator on 2017/5/22.
 */
interface IBaseView {

    fun getLayoutId(): Int

    fun onBindView(savedInstanceState: Bundle?)

    fun onBindData()

    fun showToast(string: String)
}