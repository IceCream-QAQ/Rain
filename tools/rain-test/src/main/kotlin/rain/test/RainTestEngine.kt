package rain.test

import org.junit.jupiter.engine.JupiterTestEngine
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration
import org.junit.jupiter.engine.config.JupiterConfiguration
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor
import org.junit.jupiter.engine.discovery.DiscoverySelectorResolver
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import rain.classloader.AppClassloader
import rain.function.annotation
import rain.function.hasAnnotation
import java.lang.reflect.Method


class RainTestEngine : TestEngine by JupiterTestEngine() {

    val appClassLoader: AppClassloader

    override fun getId() = "RainTestEngine"
    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {

        val sourceConfig = DefaultJupiterConfiguration(discoveryRequest.configurationParameters)
        val source = JupiterEngineDescriptor(uniqueId, sourceConfig)
        DiscoverySelectorResolver().resolveSelectors(discoveryRequest, source)
        val local = JupiterEngineDescriptor(uniqueId, sourceConfig)

        source.children.forEach {
            it as ClassTestDescriptor
            if (!it.testClass.hasAnnotation<RainTest>()) {
                local.addChild(it)
                return@forEach
            }
            val clazz = appClassLoader.loadClass(it.legacyReportingName)
            val descriptor = ClassTestDescriptor(it.uniqueId, clazz, sourceConfig)
            it.children.forEach { method ->
                if (method is TestMethodTestDescriptor) descriptor.addChild(method.ofApp(sourceConfig, method.uniqueId))
            }
            local.addChild(descriptor)
        }
        return local
    }

    fun <T> Class<T>.ofApp(): Class<T> =
        appClassLoader.loadClass(name) as Class<T>

    fun Method.ofApp(): Method {
        val clazz = declaringClass.ofApp()
        return clazz.getMethod(name, *parameterTypes.map { it.ofApp() }.toTypedArray())
    }

    fun TestMethodTestDescriptor.ofApp(configuration: JupiterConfiguration, uniqueId: UniqueId) =
        TestMethodTestDescriptor(
            uniqueId,
            testClass.ofApp(),
            testMethod.ofApp(),
            configuration
        )


    init {
        appClassLoader = AppClassloader(RainTestEngine::class.java.classLoader)
        Thread.currentThread().contextClassLoader = appClassLoader
    }
}