package com.music.android.lin.modules

import android.content.Context
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.ksp.generated.module
import kotlin.reflect.KClass

/**
 * @author: liuzhongao
 * @since: 2024/10/23 00:12
 */
object AppKoin {

    private val koinApplication: KoinApplication
        get() = requireNotNull(GlobalContext.getKoinApplicationOrNull())
    private val koin: Koin get() = GlobalContext.get()

    fun init(context: Context) {
        val properties = ApplicationModule.customProperties(context) +
                AppDatabaseModule.customProperties("anonymous_user")
        startKoin {
            properties(properties)
            modules(AppModule.module)
        }
    }

    inline fun <reified T : Any> get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null,
    ): T = getInner(T::class, qualifier, parameters)

    inline fun <reified T : Any> getOrNull(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null,
    ): T? = getInnerOrNull(T::class, qualifier, parameters)

    fun <T : Any> getInner(
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null,
    ): T = koin.get(clazz, qualifier, parameters)

    fun <T : Any> getInnerOrNull(
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null,
    ): T? = koin.getOrNull(clazz, qualifier, parameters)

    fun loadModules(modules: List<Module>) {
        koin.loadModules(modules)
    }

    fun unloadModules(modules: List<Module>) {
        koin.unloadModules(modules)
    }

    fun setProperties(properties: Map<String, Any>) {
        koinApplication.properties(properties)
    }

    fun unSetProperties(propertyKeys: List<String>) {
        propertyKeys.forEach { propertyKey ->
            koin.deleteProperty(propertyKey)
        }
    }
}