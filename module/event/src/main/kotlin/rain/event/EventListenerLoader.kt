package rain.event

import org.slf4j.LoggerFactory
import rain.api.di.DiContext
import rain.api.loader.LoadItem
import rain.api.loader.Loader
import rain.event.annotation.SubscribeEvent
import rain.function.annotation
import rain.function.isBean
import javax.inject.Inject

open class EventListenerLoader : Loader {

    companion object {
        private val log = LoggerFactory.getLogger(EventListenerLoader::class.java)
    }

    @Inject
    lateinit var eventBus: EventBusImpl

    @Inject
    lateinit var context: DiContext


    override fun load(items: Collection<LoadItem>) {
        log.info("[事件服务] 开始加载事件监听器。")
        for (item in items) {
            if (!item.clazz.isBean) continue
            log.debug("[事件服务] 注册事件监听器: ${item.clazz.name}.")
            try {
                register(item.clazz, context.getBean(item.clazz, "")!!)
                log.info("[事件服务] 注册事件监听器: ${item.clazz.name} 成功!")
            } catch (e: Exception) {
                e.printStackTrace()
                log.error("[事件服务] 注册事件监听器: ${item.clazz.name} 出错!", e)
            }
        }
        log.info("[事件服务] 加载事件监听器完成。")
    }

    @Inject
    lateinit var creator: EventInvokerCreator

    open fun register(clazz: Class<*>, instance: Any) {
        clazz.methods.forEach {
            it.annotation<SubscribeEvent> {
                eventBus.register(
                    EventListenerInfo(
                        clazz = clazz,
                        method = it,
                        weight = weight,
                        instance = instance,
                        invoker = creator
                            .createEventHandlerInvokerClass(it)
                            .run { getConstructor(Any::class.java).newInstance(instance) }
                                as EventInvoker
                    )
                )
            }
        }
    }

    override fun priority(): Int {
        return 10
    }
}