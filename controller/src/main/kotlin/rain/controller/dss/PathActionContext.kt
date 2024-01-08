package rain.controller.dss

import rain.controller.ActionContext

open class PathActionContext(val path: Array<String>) : ActionContext {

    val saves = HashMap<String, Any>()
    override var runtimeError: Throwable? = null
    override var result: Any? = null

    override fun get(name: String) = saves[name]

    override fun set(name: String, obj: Any?) {
        if (obj != null) saves[name] = obj
        saves.remove(obj)
    }

    override fun remove(name: String) = saves.remove(name)
}