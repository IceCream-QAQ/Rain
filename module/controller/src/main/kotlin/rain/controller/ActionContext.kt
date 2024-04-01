package rain.controller

import rain.api.permission.IUser

interface ActionContext {

    val user: IUser?

    var runtimeError: Throwable?
    var result: Any?

    operator fun get(name: String): Any?
    operator fun set(name: String, obj: Any?)
    fun remove(name: String): Any?
}