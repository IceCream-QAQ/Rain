package rain.controller

fun interface ControllerInstanceGetter {
    operator fun invoke(): Any
}