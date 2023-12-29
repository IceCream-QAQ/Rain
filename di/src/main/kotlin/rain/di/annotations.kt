package rain.di

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Config(val value: String = "")

annotation class Nullable
annotation class NotSearch