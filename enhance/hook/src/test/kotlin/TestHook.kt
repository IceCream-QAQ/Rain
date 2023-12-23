import rain.classloader.IRainClassLoader
import rain.classloader.AppClassloader
import rain.classloader.transformer.ClassTransformer
import rain.function.sout
import rain.hook.*

class TestHookTarget {

    fun instanceTargetMethod(who: String) = "Instance: Hello $who!"
    fun standardTargetMethod(who: String, b: Long, c: Long) = "Standard: Hello $who!"


}

@InstanceMode
class TestInstanceHookRunnable : HookRunnable {
    override fun preRun(context: HookContext): Boolean {
        context.params[1] = "Instance"
        return false
    }
}

class TesStandardHookRunnable : HookRunnable {
    override fun preRun(context: HookContext): Boolean {
        context.params[1] = "Standard"
        return false
    }
}

class TestYuHookMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            AppClassloader.registerBackList(
                arrayListOf(
                    "rain.hook.IHook",
                    "rain.hook.HookRunnable",
                    "rain.hook.HookItem",
                    "rain.hook.HookInfo",
                    "rain.loader.transformer.ClassTransformer",
                    "org.objectweb.asm."
                )
            )

            AppClassloader(TestYuHookMain::class.java.classLoader).apply {


                registerTransformer(
                    let {
                        loadClass("rain.hook.HookImpl")
                            .getConstructor(
                                IRainClassLoader::class.java,
                                IHook::class.java
                            ).newInstance(this, null) as ClassTransformer
                    }.apply {
                        this as IHook
                        registerHook(
                            HookItem.hookMethod(
                                "TestHookTarget",
                                "instanceTargetMethod",
                                null,
                                "TestInstanceHookRunnable"
                            )
                        )
                        registerHook(
                            HookItem.hookMethod(
                                "TestHookTarget",
                                "standardTargetMethod",
                                null,
                                "TesStandardHookRunnable"
                            )
                        )
                    }
                )

                val hookRunnableClass = loadClass("TestInstanceHookRunnable")

                loadClass("TestHookTarget")
                    .apply {
                        newInstance().let { instance ->
                            getMethod("setTestInstanceHookRunnable", hookRunnableClass).invoke(
                                instance,
                                hookRunnableClass.newInstance()
                            )
                            getMethod("instanceTargetMethod", String::class.java).apply {
                                invoke(instance, "World!").sout()
                            }
                            getMethod(
                                "standardTargetMethod",
                                String::class.java,
                                Long::class.javaPrimitiveType,
                                Long::class.javaPrimitiveType
                            ).apply {
                                invoke(instance, "World!", 0, 0).sout()
                            }
                        }
                    }
            }

        }
    }
}

