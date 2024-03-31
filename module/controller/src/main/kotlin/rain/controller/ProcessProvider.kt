package rain.controller

import java.lang.reflect.Method

/*** 过程提供者
 * 用以在注解上标记 ProcessBy 提供 Action 过程。
 * 使用时需要在具体实现中标记 Before，After，Catch 来识别对应的过程位置及优先级。
 * @see rain.controller.annotation.ProcessBy
 * @see rain.controller.annotation.Before
 * @see rain.controller.annotation.After
 * @see rain.controller.annotation.Catch
 */
interface ProcessProvider<CTX : ActionContext> {
    /*** 提供 Controller/Action 的过程信息
     * @param provideAnnotation 触发的 Annotation 实例。
     * @param functionAnnotation 功能 Annotation，值为实际触发加载的 Before，After，Catch。
     * @param controllerClass Controller 类
     * @param controllerInstance Controller 实例
     * @param action 被标记注解的 Action 函数，如果本项为空则为注解标记在类上。
     */
    operator fun <T> invoke(
        provideAnnotation: Annotation,
        functionAnnotation: Annotation,
        controllerClass: Class<T>,
        controllerInstance: T,
        action: Method?
    ): ProcessInvoker<CTX>
}