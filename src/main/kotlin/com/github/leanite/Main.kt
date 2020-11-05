package com.github.leanite

class ViewModel(private val useCase: UseCase) {
    fun getData() {
        println(useCase.execute())
    }
}

class UseCase(private val repo: Repository) {
    fun execute() = repo.getData()
}

class Repository(private val service: Service) {
    fun getData() = service.getData()
}

interface Service {
    fun getData() : String
}

class ServiceImpl : Service {
    override fun getData(): String  = "Data from service!"
}

val module1 = module {
    single { ServiceImpl() as Service }
    factory { ViewModel(get()) }
    factory { Repository(get()) }
}

val module2 = module {
    factory { UseCase(get()) }
}

fun main() {
    startSimpleKoin {
        modules(
            listOf(
                module1,
                module2
            )
        )
    }

    val viewModel: ViewModel by inject()
    val repository: Repository = get()

    viewModel.getData()
    println(repository.getData())
}
