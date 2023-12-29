package rain.hook

import java.lang.reflect.Method

class HookInfo(
    val className: String,
    val methodName: String,
    val clazz: Class<*>,
    val method: Method,
    val sourceMethod: Method,
    val methodParams: Array<Class<*>>
) {

    val saveInfo = HashMap<String, Any?>()

    private val runnables = ArrayList<HookRunnable>()

    fun putRunnable(runnable: HookRunnable){
        runnables.add(runnable)
        runnable.init(this)
    }

    fun preRun(method: HookContext): Boolean {
        for (runnable in runnables) {
            if (runnable.preRun(method)) return true
        }
        return false
    }

    fun postRun(method: HookContext) {
        for (runnable in runnables) {
            runnable.postRun(method)
        }
    }

    fun onError(method: HookContext): Boolean {
        for (runnable in runnables) {
            if (runnable.onError(method)) return true
        }
        return false
    }

}