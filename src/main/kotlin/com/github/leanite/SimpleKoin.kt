package com.github.leanite

import kotlin.reflect.KClass

class SimpleKoin {
    private lateinit var declarations: Map<KClass<*>, Declaration<Any>>
    private lateinit var instances: Map<KClass<*>, Any>
    private lateinit var injectionTypes: Map<KClass<*>, InjectionType>

    fun loadModules(modules: List<Module>) {
        declarations = modules.allDeclarations
        instances = modules.allInstances
        injectionTypes = modules.allInjectionTypes
    }

    fun getDeclaration(type: KClass<*>) = declarations[type]
    fun getInstance(type: KClass<*>) = instances[type]
    fun getInjectionType(type: KClass<*>) = injectionTypes[type]
}

fun getSimpleKoin() = SimpleKoinContext.getSimpleKoin()