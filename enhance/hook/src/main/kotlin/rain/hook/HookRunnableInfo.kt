package rain.hook

data class HookRunnableInfo(
    val className: String,
    val isInstanceMode: Boolean,
    val clazz: Class<out HookRunnable>
){
    private var _instance: HookRunnable? = null

    val descriptor: String = "L${className.replace(".","/")};"

    val instance:HookRunnable
        get() {
            if (_instance == null){
                _instance = clazz.newInstance()
            }
            return _instance!!
        }
}