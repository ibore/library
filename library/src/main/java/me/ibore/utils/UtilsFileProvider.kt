package me.ibore.utils

import android.app.Application
import androidx.core.content.FileProvider

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2020/03/19
 * desc  :
</pre> *
 */
class UtilsFileProvider : FileProvider() {
    override fun onCreate(): Boolean {
        Utils.init(context!!.applicationContext as Application)
        return true
    }
}