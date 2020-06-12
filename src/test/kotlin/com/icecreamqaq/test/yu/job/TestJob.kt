package com.icecreamqaq.test.yu.job

import com.IceCreamQAQ.Yu.annotation.Cron
import com.IceCreamQAQ.Yu.annotation.JobCenter
import com.IceCreamQAQ.Yu.cache.EhcacheHelp
import com.IceCreamQAQ.Yu.util.DateUtil
import javax.inject.Inject
import javax.inject.Named

@JobCenter
class TestJob {

    @Inject
    private lateinit var dateUtil: DateUtil

    @Inject
    @field:Named("testCache2")
    private lateinit var c: EhcacheHelp<Int>

    @Cron("2s")
    fun c1() {
//        println("时间：${dateUtil.formatDateTimeSSS()}，c1 定时任务触发。")
//        c["ccc"] = (c["ccc"] ?: 0) + 1
    }

    @Cron("10s")
    fun c2() {
//        println("时间：${dateUtil.formatDateTimeSSS()}，c2 定时任务触发。当前 Cache: ${c["ccc"]}")
    }

    @Cron("At::d::02:32")
    fun curfew(){
        println("到32啦！")
        println("当前时间：${dateUtil.formatDateTimeSSS()}")
    }
    @Cron("At::h::57")
    fun morning2(){
        println("到57啦！")
        println("当前时间：${dateUtil.formatDateTimeSSS()}")
    }

    @Cron("At::h::00")
    fun morning(){
        println("到整点啦！")
        println("当前时间：${dateUtil.formatDateTimeSSS()}")
    }


}