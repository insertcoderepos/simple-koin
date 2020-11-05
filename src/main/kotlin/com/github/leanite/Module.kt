package com.github.leanite

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class Module {
    val declarationMap: MutableMap<KClass<*>, Declaration<Any>> = ConcurrentHashMap()
    val instanceMap: MutableMap<KClass<*>, Any> = ConcurrentHashMap()
    val injectionTypeMap: MutableMap<KClass<*>, InjectionType> = ConcurrentHashMap()

    inline fun <reified T: Any> factory(noinline declaration: Declaration<T>) {
        declarationMap[T::class] = declaration
        injectionTypeMap[T::class] = InjectionType.FACTORY
    }

    inline fun <reified T: Any> single(noinline declaration: Declaration<T>) {
        instanceMap[T::class] = declaration.invoke()
        injectionTypeMap[T::class] = InjectionType.SINGLE
    }

    inline fun <reified T: Any> get(): T {
        val injectionType = getInjectionType(T::class)
        var instance: Any? = null

        when(injectionType) {
            InjectionType.SINGLE -> instance = getSingletonInstance(T::class)
            InjectionType.FACTORY -> instance = getFactoryInstance(T::class)
        }

        if (instance == null)
            error("Unable to find declaration of type ${T::class.qualifiedName}")

        return instance as T
    }

    fun getInjectionType(type: KClass<*>) = injectionTypeMap[type] ?: getSimpleKoin().getInjectionType(type)

    fun getSingletonInstance(type: KClass<*>) = instanceMap[type] ?: getSimpleKoin().getInstance(type)

    fun getFactoryInstance(type: KClass<*>) =
            declarationMap[type]?.invoke() ?: getSimpleKoin().getDeclaration(type)?.invoke()
}

val List<Module>.allDeclarations: Map<KClass<*>, Declaration<Any>>
    get() = this.fold(this[0].declarationMap) { allDeclarations, module ->
        (allDeclarations + module.declarationMap) as MutableMap<KClass<*>, Declaration<Any>>
    }

val List<Module>.allInstances: Map<KClass<*>, Any>
    get() = this.fold(this[0].instanceMap) { allInstances, module ->
        (allInstances + module.instanceMap) as MutableMap<KClass<*>, Any>
    }

val List<Module>.allInjectionTypes: Map<KClass<*>, InjectionType>
    get() = this.fold(this[0].injectionTypeMap) { allInjectionTypes, module ->
        (allInjectionTypes + module.injectionTypeMap) as MutableMap<KClass<*>, InjectionType>
    }

fun module(block: Module.() -> Unit) = Module().apply(block)

inline fun <reified T: Any> get(): T {
    val injectionType = getSimpleKoin().getInjectionType(T::class)
    var instance: Any? = null

    when(injectionType) {
        InjectionType.SINGLE -> instance = getSimpleKoin().getInstance(T::class)
        InjectionType.FACTORY -> instance = getSimpleKoin().getDeclaration(T::class)?.invoke()
    }

    if (instance == null)
        error("Unable to find declaration of type ${T::class.qualifiedName}")

    return instance as T
}

inline fun <reified T: Any> inject(): Lazy<T> = lazy { get<T>() }
