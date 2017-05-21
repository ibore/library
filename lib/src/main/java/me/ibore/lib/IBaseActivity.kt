package me.ibore.lib

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity

/**
 * description:
 * author: Ibore Xie
 * date: 2017-05-22 00:40
 * website: ibore.me
 */
abstract class IBaseActivity : AppCompatActivity(), IBaseView {


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBindView(savedInstanceState);
    }



}

