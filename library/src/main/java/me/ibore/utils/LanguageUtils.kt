package me.ibore.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import me.ibore.utils.UtilsBridge.activityList
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/06/20
 * desc  : utils about language
</pre> *
 */
object LanguageUtils {

    private const val KEY_LOCALE = "KEY_LOCALE"
    private const val VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM"

    /**
     * Apply the system language.
     *
     * @param isRelaunchApp True to relaunch app, false to recreate all activities.
     */
    @JvmOverloads
    fun applySystemLanguage(isRelaunchApp: Boolean = false) {
        applyLanguageReal(null, isRelaunchApp)
    }

    /**
     * Apply the language.
     *
     * @param locale        The language of locale.
     * @param isRelaunchApp True to relaunch app, false to recreate all activities.
     */
    @JvmOverloads
    fun applyLanguage(locale: Locale, isRelaunchApp: Boolean = false) {
        applyLanguageReal(locale, isRelaunchApp)
    }

    private fun applyLanguageReal(locale: Locale?, isRelaunchApp: Boolean) {
        if (locale == null) {
            Utils.SP.put(KEY_LOCALE, VALUE_FOLLOW_SYSTEM, true)
        } else {
            Utils.SP.put(KEY_LOCALE, locale2String(locale), true)
        }
        val destLocal = locale
            ?: getLocal(Resources.getSystem().configuration)
        updateAppContextLanguage(destLocal, object : Utils.Consumer<Boolean> {
            override fun accept(t: Boolean) {
                if (t) {
                    restart(isRelaunchApp)
                } else {
                    AppUtils.relaunchApp()
                }
            }
        })
    }

    private fun restart(isRelaunchApp: Boolean) {
        if (isRelaunchApp) {
            AppUtils.relaunchApp()
        } else {
            for (activity in activityList) {
                activity.recreate()
            }
        }
    }

    /**
     * Return whether applied the language by [LanguageUtils].
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isAppliedLanguage: Boolean
        get() = getAppliedLanguage() != null

    /**
     * Return whether applied the language by [LanguageUtils].
     *
     * @param locale The locale.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAppliedLanguage(locale: Locale): Boolean {
        val appliedLocale = getAppliedLanguage() ?: return false
        return isSameLocale(locale, appliedLocale)
    }

    /**
     * Return the applied locale.
     *
     * @return the applied locale
     */
    fun getAppliedLanguage(): Locale? {
        val spLocaleStr = Utils.SP.getString(KEY_LOCALE)
        return if (spLocaleStr.isEmpty() || VALUE_FOLLOW_SYSTEM == spLocaleStr) null
        else string2Locale(spLocaleStr)
    }

    /**
     * Return the locale of context.
     *
     * @return the locale of context
     */
    fun getContextLanguage(context: Context): Locale {
        return getLocal(context.resources.configuration)
    }

    /**
     * Return the locale of applicationContext.
     *
     * @return the locale of applicationContext
     */
    val appContextLanguage: Locale
        get() = getContextLanguage(Utils.app)

    /**
     * Return the locale of system
     *
     * @return the locale of system
     */
    val systemLanguage: Locale
        get() = getLocal(Resources.getSystem().configuration)

    /**
     * Update the locale of applicationContext.
     *
     * @param destLocale The dest locale.
     * @param consumer   The consumer.
     */
    fun updateAppContextLanguage(destLocale: Locale, consumer: Utils.Consumer<Boolean>?) {
        pollCheckAppContextLocal(destLocale, 0, consumer)
    }

    fun pollCheckAppContextLocal(
        destLocale: Locale, index: Int, consumer: Utils.Consumer<Boolean>?
    ) {
        val appResources = Utils.app.resources
        val appConfig = appResources.configuration
        val appLocal = getLocal(appConfig)
        setLocal(appConfig, destLocale)
        Utils.app.resources.updateConfiguration(appConfig, appResources.displayMetrics)
        if (consumer == null) return
        if (isSameLocale(appLocal, destLocale)) {
            consumer.accept(true)
        } else {
            if (index < 20) {
                ThreadUtils.runOnUiThreadDelayed({
                    pollCheckAppContextLocal(
                        destLocale,
                        index + 1,
                        consumer
                    )
                }, 16)
                return
            }
            Log.e("LanguageUtils", "appLocal didn't update.")
            consumer.accept(false)
        }
    }

    /**
     * If applyLanguage not work, try to call it in [Activity.attachBaseContext].
     *
     * @param context The baseContext.
     * @return the context with language
     */
    fun attachBaseContext(context: Context): Context {
        val spLocaleStr = Utils.SP.getString(KEY_LOCALE)
        if (spLocaleStr.isEmpty() || VALUE_FOLLOW_SYSTEM == spLocaleStr) {
            return context
        }
        val settingsLocale = string2Locale(spLocaleStr) ?: return context
        val resources = context.resources
        val config = resources.configuration
        setLocal(config, settingsLocale)
        return context.createConfigurationContext(config)
    }

    fun applyLanguage(activity: Activity) {
        val spLocale = Utils.SP.getString(KEY_LOCALE)
        if (spLocale.isEmpty()) return
        val destLocal: Locale? = if (VALUE_FOLLOW_SYSTEM == spLocale) {
            getLocal(Resources.getSystem().configuration)
        } else string2Locale(spLocale)
        if (destLocal == null) return
        updateConfiguration(activity, destLocal)
        updateConfiguration(Utils.app, destLocal)
    }

    private fun updateConfiguration(context: Context, destLocal: Locale) {
        val resources = context.resources
        val config = resources.configuration
        setLocal(config, destLocal)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun locale2String(locale: Locale): String {
        val localLanguage = locale.language // this may be empty
        val localCountry = locale.country // this may be empty
        return "$localLanguage$$localCountry"
    }

    private fun string2Locale(str: String): Locale? {
        val locale = string2LocaleReal(str)
        if (locale == null) {
            Log.e("LanguageUtils", "The string of $str is not in the correct format.")
            Utils.SP.remove(KEY_LOCALE)
        }
        return locale
    }

    private fun string2LocaleReal(str: String): Locale? {
        return if (!isRightFormatLocalStr(str)) {
            null
        } else try {
            val splitIndex = str.indexOf("$")
            Locale(str.substring(0, splitIndex), str.substring(splitIndex + 1))
        } catch (ignore: Exception) {
            null
        }
    }

    private fun isRightFormatLocalStr(localStr: String): Boolean {
        val chars = localStr.toCharArray()
        var count = 0
        for (c in chars) {
            if (c == '$') {
                if (count >= 1) return false
                ++count
            }
        }
        return count == 1
    }

    private fun isSameLocale(l0: Locale, l1: Locale): Boolean {
        return (StringUtils.equals(l1.language, l0.language)
                && StringUtils.equals(l1.country, l0.country))
    }

    private fun getLocal(configuration: Configuration): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            configuration.locale
        }
    }

    private fun setLocal(configuration: Configuration, locale: Locale) {
        configuration.setLocale(locale)
    }
}