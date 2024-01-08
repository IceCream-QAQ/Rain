package rain.di

interface BeanInjector<T> {
    operator fun invoke(bean: T): T
}


