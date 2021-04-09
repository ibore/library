package me.ibore.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.view.View
import android.view.Window
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import dev.DevUtils
import java.util.*

/**
 * detail: Activity 工具类 ( 包含 Activity 控制管理 )
 * @author Ttt
 * <pre>
 * 转场动画
 * @see [](https://www.cnblogs.com/tianzhijiexian/p/4087917.html)
 *
 * @see [](https://mp.weixin.qq.com/s/1Bp7ApxstPaRF9BY7AEe6g)
 * ActivityOptionsCompat.makeScaleUpAnimation
 * @see [](https://www.jianshu.com/p/fa1c8deeaa57)
</pre> *
 */
class ActivityUtils private constructor() {
    // ==================
    // = Activity 栈处理 =
    // ==================
    // Activity 栈 ( 后进先出 )
    private val mActivityStacks: Stack<Activity> = Stack<Activity>()

    /**
     * 获取 Activity 栈
     * @return [<]
     */
    val activityStacks: Stack<Activity>
        get() = mActivityStacks

    /**
     * 添加 Activity
     * @param activity [Activity]
     * @return [ActivityUtils]
     */
    fun addActivity(activity: Activity): ActivityUtils {
        synchronized(mActivityStacks) {
            if (mActivityStacks.contains(activity)) {
                return this
            }
            mActivityStacks.add(activity)
        }
        return this
    }

    /**
     * 移除 Activity
     * @param activity [Activity]
     * @return [ActivityUtils]
     */
    fun removeActivity(activity: Activity): ActivityUtils {
        synchronized(mActivityStacks) {
            val index = mActivityStacks.indexOf(activity)
            if (index == -1) {
                return this
            }
            try {
                mActivityStacks.removeAt(index)
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "removeActivity")
            }
        }
        return this
    }

    /**
     * 移除多个 Activity
     * @param activitys Activity[]
     * @return [ActivityUtils]
     */
    fun removeActivity(vararg activitys: Activity): ActivityUtils {
        if (activitys.isNotEmpty()) {
            var i = 0
            val len = activitys.size
            while (i < len) {
                removeActivity(activitys[i])
                i++
            }
        }
        return this
    }

    /**
     * 获取最后一个 ( 当前 ) Activity
     * @return 最后一个 ( 当前 ) [Activity]
     */
    fun currentActivity(): Activity {
        return mActivityStacks.lastElement()
    }

    /**
     * 检测是否包含指定的 Activity
     * @param clazzs Class(Activity)[]
     * @return `true` yes, `false` no
     */
    fun existActivitys(vararg clazzs: Class<*>): Boolean {
        if (clazzs.isNotEmpty()) {
            synchronized(mActivityStacks) {
                // 保存新的堆栈, 防止出现同步问题
                val stack: Stack<Activity> = Stack<Activity>()
                stack.addAll(mActivityStacks)
                try {
                    // 进行遍历判断
                    val iterator: Iterator<Activity> = stack.iterator()
                    while (iterator.hasNext()) {
                        val activity: Activity = iterator.next()
                        if (activity != null && !activity.isFinishing()) {
                            var i = 0
                            val len = clazzs.size
                            while (i < len) {
                                if (clazzs[i] != null && activity.javaClass.getName() == clazzs[i]!!
                                        .name
                                ) {
                                    return true
                                }
                                i++
                            }
                        }
                    }
                } finally {
                    // 移除数据, 并且清空内存
                    stack.clear()
                }
            }
        }
        return false
    }

    /**
     * 关闭指定 Activity
     * @param activity [Activity]
     * @return [ActivityUtils]
     */
    @JvmOverloads
    fun finishActivity(activity: Activity = mActivityStacks.lastElement()): ActivityUtils {
        // 先移除 Activity
        removeActivity(activity)
        // Activity 不为 null, 并且属于未销毁状态
        if (!activity.isFinishing) {
            activity.finish()
        }
        return this
    }

    /**
     * 关闭多个 Activity
     * @param activitys Activity[]
     * @return [ActivityUtils]
     */
    fun finishActivity(vararg activitys: Activity): ActivityUtils {
        if (activitys.isNotEmpty()) {
            var i = 0
            val len = activitys.size
            while (i < len) {
                finishActivity(activitys[i])
                i++
            }
        }
        return this
    }

    /**
     * 关闭指定类名 Activity
     * @param clazz Activity.class
     * @return [ActivityUtils]
     */
    fun finishActivity(clazz: Class<*>): ActivityUtils {
        synchronized(mActivityStacks) {
            // 保存新的堆栈, 防止出现同步问题
            val stack: Stack<Activity> = Stack<Activity>()
            stack.addAll(mActivityStacks)
            // 清空全部, 便于后续操作处理
            mActivityStacks.clear()
            // 进行遍历移除
            val iterator: MutableIterator<Activity> = stack.iterator()
            while (iterator.hasNext()) {
                val activity: Activity = iterator.next()
                // 判断是否想要关闭的 Activity
                if (activity.javaClass == clazz) {
                    // 如果 Activity 没有 finish 则进行 finish
                    if (!activity.isFinishing) {
                        activity.finish()
                    }
                    // 删除对应的 Item
                    iterator.remove()
                }
            }
            // 把不符合条件的保存回去
            mActivityStacks.addAll(stack)
            // 移除数据, 并且清空内存
            stack.clear()
        }
        return this
    }

    /**
     * 结束多个类名 Activity
     * @param clazzs Class(Activity)[]
     * @return [ActivityUtils]
     */
    fun finishActivity(vararg clazzs: Class<*>): ActivityUtils {
        if (clazzs.isNotEmpty()) {
            synchronized(mActivityStacks) {

                // 保存新的堆栈, 防止出现同步问题
                val stack: Stack<Activity> = Stack<Activity>()
                stack.addAll(mActivityStacks)
                // 清空全部, 便于后续操作处理
                mActivityStacks.clear()
                // 判断是否销毁
                var isRemove: Boolean
                // 进行遍历移除
                val iterator: MutableIterator<Activity> = stack.iterator()
                while (iterator.hasNext()) {
                    val activity: Activity = iterator.next()
                    // 判断是否想要关闭的 Activity
                    if (activity != null) {
                        // 默认不需要销毁
                        isRemove = false
                        // 循环判断
                        var i = 0
                        val len = clazzs.size
                        while (i < len) {

                            // 判断是否相同
                            if (activity.javaClass == clazzs[i]) {
                                isRemove = true
                                break
                            }
                            i++
                        }
                        // 判断是否销毁
                        if (isRemove) {
                            // 如果 Activity 没有 finish 则进行 finish
                            if (!activity.isFinishing()) {
                                activity.finish()
                            }
                            // 删除对应的 Item
                            iterator.remove()
                        }
                    } else {
                        // 删除对应的 Item
                        iterator.remove()
                    }
                }
                // 把不符合条件的保存回去
                mActivityStacks.addAll(stack)
                // 移除数据, 并且清空内存
                stack.clear()
            }
        }
        return this
    }

    /**
     * 结束全部 Activity 除忽略的 Activity 外
     * @param clazz Activity.class
     * @return [ActivityUtils]
     */
    fun finishAllActivityToIgnore(clazz: Class<*>): ActivityUtils {
        synchronized(mActivityStacks) {

            // 保存新的堆栈, 防止出现同步问题
            val stack: Stack<Activity> = Stack<Activity>()
            stack.addAll(mActivityStacks)
            // 清空全部, 便于后续操作处理
            mActivityStacks.clear()
            // 进行遍历移除
            val iterator: MutableIterator<Activity> = stack.iterator()
            while (iterator.hasNext()) {
                val activity: Activity = iterator.next()
                // 判断是否想要关闭的 Activity
                if (activity.javaClass != clazz) {
                    // 如果 Activity 没有 finish 则进行 finish
                    if (!activity.isFinishing) {
                        activity.finish()
                    }
                    // 删除对应的 Item
                    iterator.remove()
                }
            }
            // 把不符合条件的保存回去
            mActivityStacks.addAll(stack)
            // 移除数据, 并且清空内存
            stack.clear()
        }
        return this
    }

    /**
     * 结束全部 Activity 除忽略的 Activity 外
     * @param clazzs Class(Activity)[]
     * @return [ActivityUtils]
     */
    fun finishAllActivityToIgnore(vararg clazzs: Class<*>): ActivityUtils {
        if (clazzs.isNotEmpty()) {
            synchronized(mActivityStacks) {

                // 保存新的堆栈, 防止出现同步问题
                val stack: Stack<Activity> = Stack<Activity>()
                stack.addAll(mActivityStacks)
                // 清空全部, 便于后续操作处理
                mActivityStacks.clear()
                // 判断是否销毁
                var isRemove: Boolean
                // 进行遍历移除
                val iterator: MutableIterator<Activity> = stack.iterator()
                while (iterator.hasNext()) {
                    val activity: Activity = iterator.next()
                    // 判断是否想要关闭的 Activity
                    if (activity != null) {
                        // 默认需要销毁
                        isRemove = true
                        // 循环判断
                        var i = 0
                        val len = clazzs.size
                        while (i < len) {

                            // 判断是否相同
                            if (activity.javaClass == clazzs[i]) {
                                isRemove = false
                                break
                            }
                            i++
                        }
                        // 判断是否销毁
                        if (isRemove) {
                            // 如果 Activity 没有 finish 则进行 finish
                            if (!activity.isFinishing()) {
                                activity.finish()
                            }
                            // 删除对应的 Item
                            iterator.remove()
                        }
                    } else {
                        // 删除对应的 Item
                        iterator.remove()
                    }
                }
                // 把不符合条件的保存回去
                mActivityStacks.addAll(stack)
                // 移除数据, 并且清空内存
                stack.clear()
            }
        }
        return this
    }

    /**
     * 结束所有 Activity
     * @return [ActivityUtils]
     */
    fun finishAllActivity(): ActivityUtils {
        synchronized(mActivityStacks) {

            // 保存新的堆栈, 防止出现同步问题
            val stack: Stack<Activity> = Stack<Activity>()
            stack.addAll(mActivityStacks)
            // 清空全部, 便于后续操作处理
            mActivityStacks.clear()
            // 进行遍历移除
            val iterator: MutableIterator<Activity> = stack.iterator()
            while (iterator.hasNext()) {
                val activity: Activity = iterator.next()
                if (!activity.isFinishing) {
                    activity.finish()
                    // 删除对应的 Item
                    iterator.remove()
                }
            }
            // 移除数据, 并且清空内存
            stack.clear()
        }
        return this
    }
    // =
    /**
     * 退出应用程序
     * @return [ActivityUtils]
     */
    fun exitApplication(): ActivityUtils {
        try {
            finishAllActivity()
            // 退出 JVM (Java 虚拟机 ) 释放所占内存资源, 0 表示正常退出、非 0 的都为异常退出
            System.exit(0)
            // 从操作系统中结束掉当前程序的进程
            Process.killProcess(Process.myPid())
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "exitApplication")
            // =
            System.exit(-1)
        }
        return this
    }

    /**
     * 重启 APP
     * @return [ActivityUtils]
     */
    fun restartApplication(): ActivityUtils {
        try {
            val intent: Intent? =
                AppUtils.packageManager.getLaunchIntentForPackage(AppUtils.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            AppUtils.startActivity(intent)
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e, "restartApplication")
        }
        return this
    }

    /**
     * detail: Activity 跳转回传回调
     * @author Ttt
     */
    interface ResultCallback {
        /**
         * 跳转 Activity 操作
         * <pre>
         * 跳转失败, 必须返回 false 内部会根据返回值关闭 ResultActivity
         * 必须返回正确的值, 表示是否跳转成功
        </pre> *
         * @param activity [Activity]
         * @return `true` success, `false` fail
         */
        fun onStartActivityForResult(activity: Activity?): Boolean

        /**
         * 回传处理
         * @param result     resultCode 是否等于 [Activity.RESULT_OK]
         * @param resultCode resultCode
         * @param data       回传数据
         */
        fun onActivityResult(result: Boolean, resultCode: Int, data: Intent?)
    }

    /**
     * detail: 回传结果处理 Activity
     * @author Ttt
     */
    class ResultActivity : FragmentActivity() {
        // 跳转回传回调
        private var mCallback: ResultCallback? = null

        // 跳转回传回调
        private var mUUIDHash: Int? = null
        protected override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            var result = false // 跳转结果
            try {
                mUUIDHash = getIntent().getIntExtra(EXTRA_UUID, -1)
                mCallback = sResultCallbackMaps[mUUIDHash]
                result = mCallback!!.onStartActivityForResult(this)
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "onCreate")
            }
            if (!result) {
                if (mCallback != null) {
                    mCallback!!.onActivityResult(false, Activity.RESULT_CANCELED, null)
                }
                finish()
            }
        }

        protected override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ) {
            super.onActivityResult(requestCode, resultCode, data)
            if (mCallback != null) {
                mCallback!!.onActivityResult(resultCode == Activity.RESULT_OK, resultCode, data)
            }
            finish()
        }

        protected override fun onDestroy() {
            super.onDestroy()
            // 移除操作
            sResultCallbackMaps.remove(mUUIDHash)
        }

        companion object {
            // 日志 TAG
            private val TAG = ResultActivity::class.java.simpleName

            // 传参 UUID Key
            private const val EXTRA_UUID = "uuid"

            /**
             * 跳转回传结果处理 Activity 内部方法
             * @param callback Activity 跳转回传回调
             * @return `true` success, `false` fail
             */
            fun start(callback: ResultCallback?): Boolean {
                var uuid = -1
                var result = false
                if (callback != null) {
                    uuid = DevCommonUtils.randomUUIDToHashCode()
                    while (sResultCallbackMaps.containsKey(uuid)) {
                        uuid = DevCommonUtils.randomUUIDToHashCode()
                    }
                    sResultCallbackMaps[uuid] = callback
                    try {
                        val intent = Intent(XUtils.context, ResultActivity::class.java)
                        intent.putExtra(EXTRA_UUID, uuid)
                        result = AppUtils.startActivity(intent)
                    } catch (e: Exception) {
                        LogUtils.eTag(TAG, e, "start")
                    }
                }
                if (!result && uuid != -1) {
                    sResultCallbackMaps.remove(uuid)
                }
                return result
            }
        }
    }

    companion object {
        // 日志 TAG
        private val TAG = ActivityUtils::class.java.simpleName
        // ====================
        // = Activity 判断处理 =
        // ====================
        /**
         * 获取 Window
         * @param context [Context]
         * @return [Window]
         */
        fun getWindow(context: Context?): Window {
            return getWindow(getActivity(context))
        }

        /**
         * 获取 Window
         * @param activity [Activity]
         * @return [Window]
         */
        fun getWindow(activity: Activity?): Window? {
            return if (activity != null) activity.getWindow() else null
        }

        /**
         * 通过 Context 获取 Activity
         * @param context [Context]
         * @return [Activity]
         */
        fun getActivity(context: Context?): Activity? {
            if (context != null) {
                try {
                    return context as Activity?
                } catch (e: Exception) {
                    LogUtils.eTag(TAG, e, "getActivity")
                }
            }
            return null
        }

        /**
         * 获取 View context 所属的 Activity
         * @param view [View]
         * @return [Activity]
         */
        fun getActivity(view: View?): Activity? {
            if (view != null) {
                try {
                    var context = view.context
                    while (context is ContextWrapper) {
                        if (context is Activity) {
                            return context as Activity
                        }
                        context = (context as ContextWrapper).getBaseContext()
                    }
                } catch (e: Exception) {
                    LogUtils.eTag(TAG, e, "getActivity")
                }
            }
            return null
        }
        // =
        /**
         * 判断 Activity 是否关闭
         * @param activity [Activity]
         * @return `true` yes, `false` no
         */
        fun isFinishing(activity: Activity?): Boolean {
            return if (activity != null) {
                activity.isFinishing()
            } else false
        }

        /**
         * 判断 Activity 是否关闭
         * @param context [Context]
         * @return `true` yes, `false` no
         */
        fun isFinishing(context: Context?): Boolean {
            if (context != null) {
                try {
                    return (context as Activity).isFinishing()
                } catch (e: Exception) {
                    LogUtils.eTag(TAG, e, "isFinishing")
                }
            }
            return false
        }
        // =
        /**
         * 判断是否存在指定的 Activity
         * @param className Activity.class.getCanonicalName()
         * @return `true` 存在, `false` 不存在
         */
        fun isActivityExists(className: String?): Boolean {
            return isActivityExists(AppUtils.packageName, className)
        }

        /**
         * 判断是否存在指定的 Activity
         * @param packageName 应用包名
         * @param className   Activity.class.getCanonicalName()
         * @return `true` 存在, `false` 不存在
         */
        fun isActivityExists(
            packageName: String?,
            className: String?
        ): Boolean {
            if (packageName == null || className == null) return false
            var result = true
            try {
                val packageManager: PackageManager = AppUtils.packageManager
                val intent = Intent()
                intent.setClassName(packageName, className)
                if (packageManager.resolveActivity(intent, 0) == null) {
                    result = false
                } else if (intent.resolveActivity(packageManager) == null) {
                    result = false
                } else {
                    val lists: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
                    if (lists.size == 0) {
                        result = false
                    }
                }
            } catch (e: Exception) {
                result = false
                LogUtils.eTag(TAG, e, "isActivityExists")
            }
            return result
        }
        // ====================
        // = Activity 获取操作 =
        // ====================
        /**
         * 回到桌面 ( 同点击 Home 键效果 )
         * @return `true` success, `false` fail
         */
        fun startHomeActivity(): Boolean {
            try {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                return AppUtils.startActivity(intent)
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "startHomeActivity")
            }
            return false
        }

        /**
         * 获取 Launcher activity
         * @return package.xx.Activity.className
         */
        val launcherActivity: String?
            get() {
                try {
                    return getLauncherActivity(AppUtils.packageName)
                } catch (e: Exception) {
                    LogUtils.eTag(TAG, e, "getLauncherActivity")
                }
                return null
            }

        /**
         * 获取 Launcher activity
         * @param packageName 应用包名
         * @return package.xx.Activity.className
         */
        fun getLauncherActivity(packageName: String?): String? {
            if (packageName == null) return null
            try {
                val intent = Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val lists: List<ResolveInfo> =
                    AppUtils.packageManager.queryIntentActivities(intent, 0)
                for (resolveInfo in lists) {
                    if (resolveInfo != null && resolveInfo.activityInfo != null) {
                        if (resolveInfo.activityInfo.packageName == packageName) {
                            return resolveInfo.activityInfo.name
                        }
                    }
                }
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getLauncherActivity")
            }
            return null
        }

        /**
         * 获取 Activity 对应的 icon
         * @param clazz Activity.class
         * @return [Drawable] Activity 对应的 icon
         */
        fun getActivityIcon(clazz: Class<*>?): Drawable? {
            if (clazz == null) return null
            try {
                return getActivityIcon(ComponentName(XUtils.context, clazz))
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getActivityIcon")
            }
            return null
        }

        /**
         * 获取 Activity 对应的 icon
         * @param componentName [ComponentName]
         * @return [Drawable] Activity 对应的 icon
         */
        fun getActivityIcon(componentName: ComponentName?): Drawable? {
            if (componentName == null) return null
            try {
                return AppUtils.packageManager.getActivityIcon(componentName)
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getActivityIcon")
            }
            return null
        }

        /**
         * 获取 Activity 对应的 logo
         * @param clazz Activity.class
         * @return [Drawable] Activity 对应的 logo
         */
        fun getActivityLogo(clazz: Class<*>?): Drawable? {
            if (clazz == null) return null
            try {
                return getActivityLogo(ComponentName(XUtils.context, clazz))
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getActivityLogo")
            }
            return null
        }

        /**
         * 获取 Activity 对应的 logo
         * @param componentName [ComponentName]
         * @return [Drawable] Activity 对应的 logo
         */
        fun getActivityLogo(componentName: ComponentName?): Drawable? {
            if (componentName == null) return null
            try {
                return AppUtils.packageManager.getActivityLogo(componentName)
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getActivityLogo")
            }
            return null
        }

        /**
         * 获取对应包名应用启动的 Activity
         * @return package.xx.Activity.className
         */
        val activityToLauncher: String?
            get() = getActivityToLauncher(AppUtils.packageName)

        /**
         * 获取对应包名应用启动的 Activity
         * <pre>
         * android.intent.category.LAUNCHER (android.intent.action.MAIN)
        </pre> *
         * @param packageName 应用包名
         * @return package.xx.Activity.className
         */
        fun getActivityToLauncher(packageName: String?): String? {
            if (packageName == null) return null
            try {
                // 创建一个类别为 CATEGORY_LAUNCHER 的该包名的 Intent
                val resolveIntent = Intent(Intent.ACTION_MAIN, null)
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                resolveIntent.setPackage(packageName)
                // 通过 AppUtils.getPackageManager() 的 queryIntentActivities 方法遍历
                val lists: List<ResolveInfo> =
                    AppUtils.packageManager.queryIntentActivities(resolveIntent, 0)
                for (resolveInfo in lists) {
                    if (resolveInfo.activityInfo != null) {
                        // resolveInfo.activityInfo.packageName; // packageName
                        // 这个就是该 APP 的 LAUNCHER 的 Activity [ 组织形式: packageName.mainActivityName ]
                        return resolveInfo.activityInfo.name
                    }
                }
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getActivityToLauncher")
            }
            return null
        }

        /**
         * 获取系统桌面信息
         * @return [ResolveInfo]
         */
        val launcherCategoryHomeToResolveInfo: ResolveInfo?
            get() {
                try {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    return AppUtils.packageManager.resolveActivity(intent, 0)
                } catch (e: Exception) {
                    LogUtils.eTag(TAG, e, "getLauncherCategoryHomeToResolveInfo")
                }
                return null
            }// 有多个桌面程序存在, 且未指定默认项时

        /**
         * 获取系统桌面信息 ( packageName )
         * <pre>
         * 注: 存在多个桌面时且未指定默认桌面时, 该方法返回 Null, 使用时需处理这个情况
        </pre> *
         * @return packageName
         */
        val launcherCategoryHomeToPackageName: String?
            get() {
                val resolveInfo: ResolveInfo? = launcherCategoryHomeToResolveInfo
                return if (resolveInfo?.activityInfo != null) {
                    // 有多个桌面程序存在, 且未指定默认项时
                    if (resolveInfo.activityInfo.packageName == "android") {
                        null
                    } else {
                        resolveInfo.activityInfo.packageName
                    }
                } else null
            }// 有多个桌面程序存在, 且未指定默认项时

        /**
         * 获取系统桌面信息 ( activityName )
         * @return activityName
         */
        val launcherCategoryHomeToActivityName: String?
            get() {
                val resolveInfo: ResolveInfo? = launcherCategoryHomeToResolveInfo
                return if (resolveInfo?.activityInfo != null) {
                    // 有多个桌面程序存在, 且未指定默认项时
                    if (resolveInfo.activityInfo.packageName == "android") {
                        null
                    } else {
                        resolveInfo.activityInfo.name
                    }
                } else null
            }// 判断是否 . 开头// 判断是否. 开头// 有多个桌面程序存在, 且未指定默认项时

        /**
         * 获取系统桌面信息 ( package/activityName )
         * @return package/activityName
         */
        val launcherCategoryHomeToPackageAndName: String?
            get() {
                val resolveInfo: ResolveInfo? = launcherCategoryHomeToResolveInfo
                if (resolveInfo?.activityInfo != null) {
                    // 有多个桌面程序存在, 且未指定默认项时
                    if (resolveInfo.activityInfo.packageName == "android") {
                        return null
                    } else {
                        // 判断是否. 开头
                        var name: String = resolveInfo.activityInfo.name
                        // 判断是否 . 开头
                        if (name.startsWith(".")) {
                            name = resolveInfo.activityInfo.packageName + name
                        }
                        return resolveInfo.activityInfo.packageName + "/" + name
                    }
                }
                return null
            }
        // ===========
        // = 转场动画 =
        // ===========
        /**
         * 设置跳转动画
         * @param context   [Context]
         * @param enterAnim 进入动画
         * @param exitAnim  退出动画
         * @return [Bundle]
         */
        fun getOptionsBundle(context: Context, enterAnim: Int, exitAnim: Int): Bundle? {
            try {
                return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim)
                    .toBundle()
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getOptionsBundle")
            }
            return null
        }

        /**
         * 设置跳转动画
         * @param activity       [Activity]
         * @param sharedElements 转场动画 View
         * @return [Bundle]
         */
        fun getOptionsBundle(activity: Activity?, sharedElements: Array<View>): Bundle? {
            if (activity == null) return null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val len = sharedElements.size
                    val pairs: Array<Pair<View, String>> = arrayOfNulls<Pair<*, *>>(len)
                    for (i in 0 until len) {
                        pairs[i] = Pair.create(
                            sharedElements[i], sharedElements[i].transitionName
                        )
                    }
                    return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *pairs)
                        .toBundle()
                }
                return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, null, null)
                    .toBundle()
            } catch (e: Exception) {
                LogUtils.eTag(TAG, e, "getOptionsBundle")
            }
            return null
        }

        // ====================
        // = Activity 管理控制 =
        // ====================
        // ActivityUtils 实例
        @Volatile
        private var sInstance: ActivityUtils? = null

        /**
         * 获取 ActivityUtils 管理实例
         * @return [ActivityUtils]
         */
        val manager: ActivityUtils?
            get() {
                if (sInstance == null) {
                    synchronized(ActivityUtils::class.java) {
                        if (sInstance == null) {
                            sInstance = ActivityUtils()
                        }
                    }
                }
                return sInstance
            }

        // ===========
        // = 跳转回传 =
        // ===========
        // 跳转回传回调 Map
        private val sResultCallbackMaps: MutableMap<Int?, ResultCallback> = HashMap()

        /**
         * Activity 跳转回传
         * @param callback Activity 跳转回传回调
         * @return `true` success, `false` fail
         */
        fun startActivityForResult(callback: ResultCallback?): Boolean {
            return ResultActivity.start(callback)
        }
    }
}