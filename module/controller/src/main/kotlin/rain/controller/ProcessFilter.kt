package rain.controller

import java.lang.reflect.Method

interface ProcessFilter {

    /*** 提供 Controller/Action 的过程信息
     * @param provideAnnotation 触发的 Annotation 实例。如果是通过 ProcessBy 加载，既为触发注解，否则为 Before，After，Catch 注解。
     * @param functionAnnotation 功能 Annotation，值为实际触发加载的 Before，After，Catch。
     * @param controllerClass Controller 类
     * @param controllerInstance Controller 实例
     * @param action 被标记注解的 Action 函数，如果本项为空则为注解标记在类上。
     *
     * @return 返回 true 代表 Process 对该 Action 生效，否则为不生效。
     */
    operator fun <T> invoke(
        provideAnnotation: Annotation,
        functionAnnotation: Annotation,
        controllerClass: Class<T>,
        controllerInstance: T,
        action: Method?
    ): Boolean

}