package rain.test

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstanceFactory
import org.junit.jupiter.api.extension.TestInstanceFactoryContext

class RainTestExtension : TestInstanceFactory, BeforeTestExecutionCallback {

    val findInstance: (String) -> Any?

    init {
        val (classloader, context) = TestApplicationStarter.launch()

        val getBeanFun = context::class.java.getMethod("getBean", Class::class.java)
        findInstance = { name ->
            classloader.loadClass(name).run {
                getBeanFun(context, this)
            }
        }
    }

    override fun createTestInstance(p0: TestInstanceFactoryContext, p1: ExtensionContext?): Any? {
        return findInstance(p0.testClass.name)
    }

    override fun beforeTestExecution(p0: ExtensionContext?) {

    }
}