package me.ibore.utils

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import me.ibore.utils.UtilsBridge.inputStream2Bytes
import me.ibore.utils.UtilsBridge.inputStream2Lines
import me.ibore.utils.UtilsBridge.isSpace
import me.ibore.utils.UtilsBridge.writeFileFromIS
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/05/07
 * desc  : utils about resource
</pre> *
 */
object ResourceUtils {

    private const val BUFFER_SIZE = 8192

    /**
     * Return the drawable by identifier.
     *
     * @param id The identifier.
     * @return the drawable by identifier
     */
    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(Utils.app, id)
    }

    /**
     * Return the id identifier by name.
     *
     * @param name The name of id.
     * @return the id identifier by name
     */
    fun getIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(name, "id", Utils.app.packageName)
    }

    /**
     * Return the string identifier by name.
     *
     * @param name The name of string.
     * @return the string identifier by name
     */
    fun getStringIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(
            name,
            "string",
            Utils.app.packageName
        )
    }

    /**
     * Return the color identifier by name.
     *
     * @param name The name of color.
     * @return the color identifier by name
     */
    fun getColorIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(name, "color", Utils.app.packageName)
    }

    /**
     * Return the dimen identifier by name.
     *
     * @param name The name of dimen.
     * @return the dimen identifier by name
     */
    fun getDimenIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(name, "dimen", Utils.app.packageName)
    }

    /**
     * Return the drawable identifier by name.
     *
     * @param name The name of drawable.
     * @return the drawable identifier by name
     */
    fun getDrawableIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(
            name,
            "drawable",
            Utils.app.packageName
        )
    }

    /**
     * Return the mipmap identifier by name.
     *
     * @param name The name of mipmap.
     * @return the mipmap identifier by name
     */
    fun getMipmapIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(
            name,
            "mipmap",
            Utils.app.packageName
        )
    }

    /**
     * Return the layout identifier by name.
     *
     * @param name The name of layout.
     * @return the layout identifier by name
     */
    fun getLayoutIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(
            name,
            "layout",
            Utils.app.packageName
        )
    }

    /**
     * Return the style identifier by name.
     *
     * @param name The name of style.
     * @return the style identifier by name
     */
    fun getStyleIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(name, "style", Utils.app.packageName)
    }

    /**
     * Return the anim identifier by name.
     *
     * @param name The name of anim.
     * @return the anim identifier by name
     */
    fun getAnimIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(name, "anim", Utils.app.packageName)
    }

    /**
     * Return the menu identifier by name.
     *
     * @param name The name of menu.
     * @return the menu identifier by name
     */
    fun getMenuIdByName(name: String?): Int {
        return Utils.app.resources.getIdentifier(name, "menu", Utils.app.packageName)
    }

    /**
     * Copy the file from assets.
     *
     * @param assetsFilePath The path of file in assets.
     * @param destFilePath   The path of destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFileFromAssets(assetsFilePath: String, destFilePath: String): Boolean {
        var res = true
        try {
            val assets = Utils.app.assets.list(assetsFilePath)
            if (assets != null && assets.isNotEmpty()) {
                for (asset in assets) {
                    res = res and copyFileFromAssets(
                        "$assetsFilePath/$asset",
                        "$destFilePath/$asset"
                    )
                }
            } else {
                res = writeFileFromIS(
                    destFilePath,
                    Utils.app.assets.open(assetsFilePath)
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            res = false
        }
        return res
    }
    /**
     * Return the content of assets.
     *
     * @param assetsFilePath The path of file in assets.
     * @param charsetName    The name of charset.
     * @return the content of assets
     */
    @JvmOverloads
    fun readAssets2String(assetsFilePath: String?, charsetName: String? = null): String {
        return try {
            val `is` = Utils.app.assets.open(assetsFilePath!!)
            val bytes = inputStream2Bytes(`is`) ?: return ""
            if (isSpace(charsetName)) {
                String(bytes)
            } else {
                try {
                    String(bytes, Charset.forName(charsetName))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    ""
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
    /**
     * Return the content of file in assets.
     *
     * @param assetsPath  The path of file in assets.
     * @param charsetName The name of charset.
     * @return the content of file in assets
     */
    @JvmOverloads
    fun readAssets2List(
        assetsPath: String,
        charsetName: String? = null
    ): List<String>? {
        return try {
            inputStream2Lines(
                Utils.app.resources.assets.open(
                    assetsPath
                ), charsetName
            )
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Copy the file from raw.
     *
     * @param resId        The resource id.
     * @param destFilePath The path of destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFileFromRaw(@RawRes resId: Int, destFilePath: String?): Boolean {
        return writeFileFromIS(
            destFilePath,
            Utils.app.resources.openRawResource(resId)
        )
    }
    /**
     * Return the content of resource in raw.
     *
     * @param resId       The resource id.
     * @param charsetName The name of charset.
     * @return the content of resource in raw
     */
    @JvmOverloads
    fun readRaw2String(@RawRes resId: Int, charsetName: String? = null): String? {
        val `is` = Utils.app.resources.openRawResource(resId)
        val bytes = inputStream2Bytes(`is`) ?: return null
        return if (isSpace(charsetName)) {
            String(bytes)
        } else {
            try {
                String(bytes, Charset.forName(charsetName))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                ""
            }
        }
    }
    /**
     * Return the content of resource in raw.
     *
     * @param resId       The resource id.
     * @param charsetName The name of charset.
     * @return the content of file in assets
     */
    @JvmOverloads
    fun readRaw2List(
        @RawRes resId: Int,
        charsetName: String? = null
    ): List<String>? {
        return inputStream2Lines(Utils.app.resources.openRawResource(resId), charsetName)
    }
}