package rain.controller.dss

import rain.api.permission.IUser
import rain.controller.ActionContext

abstract class PathActionContext : ActionContext {

    abstract val path: Array<String>

    val saves = HashMap<String, Any>()

    override var user: IUser? = null
        protected set

    override var runtimeError: Throwable? = null
    override var result: Any? = null

    override fun get(name: String) = saves[name]

    override fun set(name: String, obj: Any?) {
        if (obj != null) saves[name] = obj
        saves.remove(obj)
    }

    override fun remove(name: String) = saves.remove(name)
}