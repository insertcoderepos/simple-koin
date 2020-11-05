package com.github.leanite

object SimpleKoinContext {
    private val simpleKoin = SimpleKoin()

    fun modules(modules: List<Module>) {
        simpleKoin.loadModules(modules)
    }

    fun getSimpleKoin() = simpleKoin
}

fun startSimpleKoin(block: SimpleKoinContext.() -> Unit) = SimpleKoinContext.apply(block)


