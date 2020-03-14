package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.controller.router.DefaultActionInvoker
import com.IceCreamQAQ.Yu.controller.router.DefaultRouter
import com.IceCreamQAQ.Yu.controller.router.MethodInvoker
import com.IceCreamQAQ.Yu.controller.router.ReflectMethodInvoker
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem_
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Named

class DefaultControllerLoaderImpl :DefaultControllerLoader() {

    @Inject
    private lateinit var context:YuContext

    @Inject
    private lateinit var mic:MethodInvokerCreator_

    override fun load(items: Map<String, LoadItem_>) {
        val rootRouters = HashMap<String,DefaultRouter>()
        for (item in items.values) {
            val clazz = item.type
            val name = clazz.getAnnotation(Named::class.java)?.value ?: item.annotation::class.java.interfaces[0].getAnnotation(Named::class.java)?.value ?: continue
            val rootRouter = rootRouters[name] ?:{
                val r = DefaultRouter(0)
                rootRouters[name] = r
                r
            }()

            controllerToRouter_(context[clazz]?: continue,rootRouter)
        }

        for ((k, v) in rootRouters) {
            context.putBean(v,k)
        }
    }

    override fun createMethodInvoker_(obj: Any, method: Method): MethodInvoker {
        return ReflectMethodInvoker(method,obj)
    }

    override fun createActionInvoker_(level: Int): DefaultActionInvoker {
        return DefaultActionInvoker(level)
    }


}