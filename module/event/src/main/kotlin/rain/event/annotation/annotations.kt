package rain.event.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubscribeEvent(val weight: Weight = Weight.NORMAL) {
    enum class Weight {
        LOWEST, LOW, NORMAL, HIGH, HIGHEST, RECORD
    }
}