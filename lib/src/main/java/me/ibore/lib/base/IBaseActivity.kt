package me.ibore.lib.base

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

/**
 * description:
 * author: Ibore Xie
 * date: 2017-05-22 00:40
 * website: ibore.me
 */
abstract class IBaseActivity : AppCompatActivity(), IBaseView {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutView(getLayoutId()))
        onBindView(savedInstanceState)
    }

    fun getLayoutView(layoutId: Int): View {
        return layoutInflater.inflate(layoutId, null)
    }


    override fun showToast(string: String) {
        Toast.makeText(applicationContext, string, Toast.LENGTH_SHORT).show()
    }

}

