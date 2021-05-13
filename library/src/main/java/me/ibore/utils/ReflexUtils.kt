package me.ibore.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import me.ibore.base.mvp.XMvpPresenter
import me.ibore.exception.ClientException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * @author DBoy
 * @date 2020/9/29
 * Class 描述 : 用于反射获取 ViewModel  和 ViewBinding
 */
object ReflexUtils {
    /**
     * 反射获取ViewBinding
     *
     * @param <V>    ViewBinding 实现类
     * @param aClass 当前类
     * @param from   layouinflater
     * @return viewBinding实例
    </V> */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <V : ViewBinding> viewBinding(aClass: Class<*>, from: LayoutInflater): V {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewBinding::class.java.isAssignableFrom(tClass)) {
                    val inflate = tClass.getMethod("inflate", LayoutInflater::class.java)
                    return inflate.invoke(null, from) as V
                }
            }
            return viewBinding(aClass.superclass, from)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw ClientException("ViewBinding初始化失败")
    }

    /**
     * 反射获取ViewBinding\
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <V : ViewBinding> viewBinding(
        aClass: Class<*>, from: LayoutInflater?, viewGroup: ViewGroup?, b: Boolean
    ): V {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewBinding::class.java.isAssignableFrom(tClass)) {
                    val inflate = tClass.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.javaPrimitiveType
                    )
                    return inflate.invoke(null, from, viewGroup, b) as V
                }
            }
            return viewBinding<ViewBinding>(aClass.superclass, from, viewGroup, b) as V
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw ClientException("ViewBinding初始化失败")
    }

    /**
     * 反射获取ViewModel
     *
     * @param <VM>   ViewModel实现类
     * @param aClass 当前class
     * @param owner  生命周期管理
     * @return ViewModel实例
    </VM> */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <VM : ViewModel> viewModel(aClass: Class<*>, owner: ViewModelStoreOwner): VM {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewModel::class.java.isAssignableFrom(tClass)) {
                    return ViewModelProvider(owner)[tClass as Class<VM>]
                }
            }
            return viewModel<VM>(aClass.superclass, owner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw ClientException("ViewModel初始化失败")
    }

    /**
     * 反射获取Presenter
     *
     * @param <VM>   Presenter实现类
     * @param aClass 当前class
     * @return Presenter实例
    </VM> */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <P : XMvpPresenter<*>> presenter(aClass: Class<*>): P {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewModel::class.java.isAssignableFrom(tClass)) {
                    return tClass as P
                }
            }
            return presenter(aClass.superclass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw ClientException("ViewModel初始化失败")
    }

    /**
     * 反射获取ViewModel,这个方法只提供给fragment使用.
     * 如果fragment的父Activity有相同的ViewModel 那么生成的ViewModel将会是同一个实例，做到Fragment与Activity的数据同步,
     * 或者说是同一个Activity中的多个Fragment同步使用用一个ViewModel达到数据之间的同步。
     *
     * @param <VM>     ViewModel实现类
     * @param aClass   当前class
     * @param fragment fragment  调用 [Fragment.requireActivity] 方法
     * @return ViewModel实例
    </VM> */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <VM : ViewModel> viewModelShared(aClass: Class<*>, fragment: Fragment): VM {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewModel::class.java.isAssignableFrom(tClass)) {
                    return ViewModelProvider(fragment.requireActivity())[tClass as Class<VM>]
                }
            }
            return viewModelShared<VM>(aClass.superclass, fragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw ClientException("ViewModel初始化失败")
    }

    @JvmStatic
    fun getTypeByAbstract(any: Any, position: Int): Type? {
        try {
            val type = any.javaClass.genericSuperclass as ParameterizedType
            return type.actualTypeArguments[position]
        } catch (e: Exception) {
        }
        return null
    }

    @JvmStatic
    fun getTypeByInterface(any: Any, position: Int, clazz: Class<*>): Type? {
        try {
            for (type in any.javaClass.genericInterfaces) {
                if ((type as ParameterizedType).rawType == clazz) {
                    return type.actualTypeArguments[position]
                }
            }
        } catch (e: Exception) {
        }
        return null
    }
}