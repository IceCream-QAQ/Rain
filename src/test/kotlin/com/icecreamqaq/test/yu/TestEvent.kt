package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.job.JobManager
import com.IceCreamQAQ.Yu.md5
import com.IceCreamQAQ.Yu.toJSONObject
import com.IceCreamQAQ.Yu.toJSONString
import com.IceCreamQAQ.Yu.util.Web
import okhttp3.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@EventListener
class TestEvent {

    @Inject
    private lateinit var testInterface: TestInterface

    @Inject
    private lateinit var jobManager: JobManager

    @Inject
    private lateinit var web: Web

    @Event
    fun onStart(e: AppStartEvent) {

        val mhyVersion = "2.1.0"
        val n = mhyVersion.md5
        val i = java.lang.String.valueOf(Date().time).substring(0, 10)
        val r = "1x7pr0"
        val c = "salt=$n&t=$i&r=$r".md5
        val ds = "$i,$r,$c"

        println(web.get("https://api-takumi.mihoyo.com/game_record/genshin/api/index?server=cn_gf01&role_id=103850772"){
            headerOf(
                    ua = "Mozilla/5.0 (Linux; Android 9; Unspecified Device) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Mobile Safari/537.36 miHoYoBBS/2.2.",
                    referer = "https://webstatic.mihoyo.com/app/community-game-records/index.html?v=6",
                    "DS" to ds,
                    "x-rpc-app_version" to mhyVersion,
                    "x-rpc-client_type" to "4",
                    "X-Requested-With" to "com.mihoyo.hyperion"
            )
        })

    }

}



@AutoBind
interface TestInterface {
    fun a()
}

class TestInterfaceImpl : TestInterface {
    override fun a() {
        println("1231231231323")
    }

}