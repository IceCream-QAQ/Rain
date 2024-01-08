package rain.controller

interface ProcessInvoker<T : ActionContext> {

    suspend operator fun invoke(context: T): Any?

}