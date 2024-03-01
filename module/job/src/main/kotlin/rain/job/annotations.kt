package rain.job

import rain.api.annotation.LoadBy

@Target(AnnotationTarget.CLASS)
@LoadBy(JobLoader::class)
annotation class CronTab

@Target(AnnotationTarget.FUNCTION)
annotation class Cron(
    // 执行规则表达式
    val value: String,
    /*** 异步执行
     * 如果为同步执行，则在本次任务执行完成之前，不会执行下一次任务。
     * 如果任务执行时间超过任务执行间隔，则会等待到下一个时间点执行任务，中间的时间点会被跳过。
     * 举例：如果任务间隔为5秒，任务执行消耗七秒，则会在第10秒执行下一次任务。
     * 如果为异步执行，如果任务执行时间超过下一次任务执行时间，则会同时执行多个任务。
     */
    val async: Boolean = false,
    // 启动时执行
    val runWithStart: Boolean = false
)