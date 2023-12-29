package rain.hook;

import org.jetbrains.annotations.NotNull;

public interface HookRunnable {

    /** 初始化时执行方法。
     * @param info 被 Hook 目标方法信息。
     */
    default void init(@NotNull HookInfo info) {
    }

    /** 方法运行前回调
     * @param context 被 Hook 方法执行上下文。
     * @return 拦截标志
     *     返回 true 则终止方法继续执行，将上下文中的 result 作为返回值，如果方法没有返回值，则忽略，直接向上返回。
     *     返回 false 则继续执行，调用目标方法，目标方法执行后，若目标方法有返回值则写入上下文中的 result，没有则忽略，并触发 postRun。
     *         若目标方法执行异常，则将异常值写入上下文中的 error，并触发 onError。
     */
    default boolean preRun(@NotNull HookContext context){
        return false;
    }

    /** 方法运行后回调
     * 方法运行后 context 内 result 属性为方法的返回值，如果方法没有返回值，则为 null。
     * @param context 被 Hook 方法执行上下文。
     */
    default void postRun(@NotNull HookContext context){

    }

    /** 方法执行异常时回调
     * 方法运行后 context 内 error 属性为方法的异常内容。
     * @param context context 被 Hook 方法执行上下文。
     * @return 拦截标志
     *     返回 true 则代表正常执行，将上下文中的 result 作为返回值，如果方法没有返回值，则忽略，直接向上返回。
     *     返回 false 则代表方法执行异常，将上下文中的 error 作为异常，向上抛出。
     */
    default boolean onError(@NotNull HookContext context){
        return false;
    }

}
