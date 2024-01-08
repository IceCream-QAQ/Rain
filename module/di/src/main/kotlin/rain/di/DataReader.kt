package rain.di

interface DataReader<T> {

    operator fun invoke(): T?
    operator fun invoke(name: String): T?

}



