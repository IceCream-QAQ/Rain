package rain.controller

fun interface ProcessInvoker<T : ActionContext> {

    suspend operator fun invoke(context: T): Any?

}