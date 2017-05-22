package me.ibore.library

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import me.ibore.lib.base.IBaseActivity

class MainActivity : IBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onBindView(savedInstanceState: Bundle?) {
        mTextMessage = findViewById(R.id.message) as TextView
        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onBindData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mTextMessage: TextView? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                mTextMessage!!.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                mTextMessage!!.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                mTextMessage!!.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
