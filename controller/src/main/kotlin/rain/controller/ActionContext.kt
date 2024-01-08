package rain.controller

interface ActionContext {

    var runtimeError: Throwable?
    var result: Any?

    operator fun get(name: String): Any?
    operator fun set(name: String, obj: Any?)
    fun remove(name: String): Any?
}