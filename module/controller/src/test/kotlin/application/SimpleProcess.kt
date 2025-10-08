package application

import controller.TestActionContext
import rain.controller.ProcessInvoker
import rain.controller.ProcessProvider
import rain.controller.annotation.Before
import rain.controller.annotation.ProcessBy
import rain.controller.special.ActionResult
import java.lang.reflect.Method

@ProcessBy(ChannelProcess::class)
annotation class Channel(val value: String)

@Before
class ChannelProcess: ProcessProvider<TestActionContext> {
    override fun <T> invoke(
        provideAnnotation: Annotation,
        functionAnnotation: Annotation,
        controllerClass: Class<T>,
        controllerInstance: T,
        action: Method?
    ): ProcessInvoker<TestActionContext> {
        provideAnnotation as Channel
        return ProcessInvoker { context ->
            if (context.channel != provideAnnotation.value)
                throw ActionResult("Channel not match" )
        }
    }
}