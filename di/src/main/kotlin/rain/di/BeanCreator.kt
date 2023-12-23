package rain.di

interface BeanCreator<T> {

    operator fun invoke(): T

}