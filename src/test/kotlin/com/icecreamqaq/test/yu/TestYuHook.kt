package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.InstanceMode
import com.IceCreamQAQ.Yu.hook.HookContext
import com.IceCreamQAQ.Yu.hook.HookItem
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.IceCreamQAQ.Yu.hook.IHook
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer
import com.IceCreamQAQ.Yu.util.sout

class TestHookTarget {

    fun instanceTargetMethod(who: String) = "Instance: Hello $who!"
    fun standardTargetMethod(who: String) = "Standard: Hello $who!"


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
                    "com.IceCreamQAQ.Yu.hook.IHook",
                    "com.IceCreamQAQ.Yu.hook.HookRunnable",
                    "com.IceCreamQAQ.Yu.hook.HookItem",
                    "com.IceCreamQAQ.Yu.hook.HookInfo",
                    "com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer",
                    "org.objectweb.asm."
                )
            )

            AppClassloader(TestYuHookMain::class.java.classLoader).apply {


                registerTransformer(
                    let {
                        loadClass("com.IceCreamQAQ.Yu.hook.HookImpl")
                            .getConstructor(
                                IRainClassLoader::class.java,
                                IHook::class.java
                            ).newInstance(this, null) as ClassTransformer
                    }.apply {
                        this as IHook
                        registerHook(
                            HookItem.hookMethod(
                                "com.icecreamqaq.test.yu.TestHookTarget",
                                "instanceTargetMethod",
                                null,
                                "com.icecreamqaq.test.yu.TestInstanceHookRunnable"
                            )
                        )
                        registerHook(
                            HookItem.hookMethod(
                                "com.icecreamqaq.test.yu.TestHookTarget",
                                "standardTargetMethod",
                                null,
                                "com.icecreamqaq.test.yu.TesStandardHookRunnable"
                            )
                        )
                    }
                )

                val hookRunnableClass = loadClass("com.icecreamqaq.test.yu.TestInstanceHookRunnable")

                loadClass("com.icecreamqaq.test.yu.TestHookTarget")
                    .apply {
                        newInstance().let { instance ->
                            getMethod("setTestInstanceHookRunnable", hookRunnableClass).invoke(
                                instance,
                                hookRunnableClass.newInstance()
                            )
                            getMethod("instanceTargetMethod", String::class.java).apply {
                                invoke(instance, "World!").sout()
                            }
                            getMethod("standardTargetMethod", String::class.java).apply {
                                invoke(instance, "World!").sout()
                            }
                        }
                    }
            }

        }
    }
}

