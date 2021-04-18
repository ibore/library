package me.ibore.utils.gson.factory

import com.google.gson.*
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Excluder
import com.google.gson.internal.bind.TypeAdapters
import me.ibore.utils.gson.factory.data.*
import me.ibore.utils.gson.factory.element.CollectionTypeAdapterFactory
import me.ibore.utils.gson.factory.element.ReflectiveTypeAdapterFactory
import java.lang.reflect.Type
import java.math.BigDecimal
import java.util.*

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2020/11/10
 * desc   : Gson 解析容错适配器
 */
object GsonFactory {
    private val INSTANCE_CREATORS = HashMap<Type, InstanceCreator<*>>(0)
    private val TYPE_ADAPTER_FACTORIES: MutableList<TypeAdapterFactory> = ArrayList()

    @Volatile
    private var sGson: Gson? = null// 加入双重校验锁
    /**
     * 设置单例的 Gson 对象
     */
    /**
     * 获取单例的 Gson 对象
     */
    var singletonGson: Gson?
        get() {
            // 加入双重校验锁
            if (sGson == null) {
                synchronized(GsonFactory::class.java) {
                    if (sGson == null) {
                        sGson = createGsonBuilder().create()
                    }
                }
            }
            return sGson
        }
        set(gson) {
            sGson = gson
        }

    /**
     * 注册类型适配器
     */
    fun registerTypeAdapterFactory(factory: TypeAdapterFactory) {
        TYPE_ADAPTER_FACTORIES.add(factory)
    }

    /**
     * 注册构造函数创建器
     *
     * @param type                  对象类型
     * @param creator               实例创建器
     */
    fun registerInstanceCreator(type: Type, creator: InstanceCreator<*>) {
        INSTANCE_CREATORS[type] = creator
    }

    /**
     * 创建 Gson 构建对象
     */
    fun createGsonBuilder(): GsonBuilder {
        val gsonBuilder = GsonBuilder()
        for (typeAdapterFactory in TYPE_ADAPTER_FACTORIES) {
            gsonBuilder.registerTypeAdapterFactory(typeAdapterFactory)
        }
        val constructorConstructor = ConstructorConstructor(INSTANCE_CREATORS)
        return gsonBuilder
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    String::class.java,
                    StringTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Boolean::class.javaPrimitiveType,
                    Boolean::class.java,
                    BooleanTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Int::class.javaPrimitiveType,
                    Int::class.java,
                    IntegerTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Long::class.javaPrimitiveType,
                    Long::class.java,
                    LongTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Float::class.javaPrimitiveType,
                    Float::class.java,
                    FloatTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Double::class.javaPrimitiveType,
                    Double::class.java,
                    DoubleTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    BigDecimal::class.java,
                    BigDecimalTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(CollectionTypeAdapterFactory(constructorConstructor))
            .registerTypeAdapterFactory(
                ReflectiveTypeAdapterFactory(
                    constructorConstructor,
                    FieldNamingPolicy.IDENTITY,
                    Excluder.DEFAULT
                )
            )
    }
}