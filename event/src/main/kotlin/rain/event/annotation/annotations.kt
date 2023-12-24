package rain.event.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Event(val weight: Weight = Weight.normal) {
    enum class Weight {
        lowest, low, normal, high, highest, record
    }
}