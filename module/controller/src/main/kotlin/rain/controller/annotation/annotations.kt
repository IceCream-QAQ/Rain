package rain.controller.annotation

import rain.controller.ProcessFilter
import rain.controller.ProcessProvider
import kotlin.reflect.KClass

annotation class Before(val weight: Int = 0, val except: Array<String> = [], val only: Array<String> = [])

annotation class After(val weight: Int = 0, val except: Array<String> = [], val only: Array<String> = [])

annotation class Catch(
    val weight: Int = 0,
    val error: KClass<out Throwable>,
    val except: Array<String> = [],
    val only: Array<String> = []
)

/** 声明一个注解的过程过滤器
 * 该注解应该标记于一个 Before，After，Catch 注解一起。
 */
annotation class ProcessFilterBy(val value: KClass<out ProcessFilter>)

annotation class Path(val value: String)
annotation class Global

/*** 声明一个注解的过程提供者
 * 该注解必须标记于一个注解上。
 * 如果某个类直接标记本注解则没有效果。
 * 该注解可以多次标记在某个注解上，用以提供多个过程。
 */
@Repeatable
annotation class ProcessBy(val value: KClass<out ProcessProvider<*>>)
