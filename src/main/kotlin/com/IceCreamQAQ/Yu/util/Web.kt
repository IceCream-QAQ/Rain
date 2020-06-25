package com.IceCreamQAQ.Yu.util

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Default
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.BeanFactory
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.toJSONString
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

interface Web {

    fun get(url: String): String
    fun post(url: String, para: Map<String, String>): String
    fun postJSON(url: String, json: String): String
    fun postJSON(url: String, obj: Any) = postJSON(url, obj.toJSONString())

    fun download(url: String): InputStream
    fun download(url: String, file: File? = null) = IO.copy(download(url), FileOutputStream(file ?: IO.tmpFile()))
    fun download(url: String, saveLocation: String) = download(url, File(saveLocation))

    fun stop()
}

class WebHelperBeanFactory : BeanFactory<Web>,ApplicationService {

    @Config("yu.webHelper.impl")
    @Default("com.IceCreamQAQ.Yu.util.OkHttpWebImpl")
    private lateinit var implClass: String

    @Inject
    private lateinit var context: YuContext

    private var web: Web? = null


    override fun init() {
        web = context.newBean(Class.forName(implClass),null,true) as Web
    }

    override fun start() {

    }

    override fun stop() {
        web?.stop()
    }

    override fun createBean(clazz: Class<Web>, name: String): Web? = web
}

@NotSearch
class OkHttpWebImpl : Web {

    private var client: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        builder.cookieJar(object : CookieJar {
            var cookieMap: MutableMap<String, Cookie> = ConcurrentHashMap()
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                for (cookie in cookies) {
                    cookieMap[cookie.name] = cookie
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return ArrayList(cookieMap.values)
            }
        })
        client = builder.build()
    }

    override fun get(url: String) = client.newCall(Request.Builder().url(url).build()).execute().body!!.string()

    override fun post(url: String, para: Map<String, String>): String {
        val fbBuilder = FormBody.Builder()
        for (s in para.keys) {
            fbBuilder.add(s, para[s] ?: "")
        }
        val formBody = fbBuilder.build()
        val request = Request.Builder().post(formBody).url(url).build()
        val call = client.newCall(request)
        val response = call.execute()
        return response.body!!.string()
    }

    override fun postJSON(url: String, json: String): String {
        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder().post(requestBody).url(url).build()
        val call = client.newCall(request)
        val response = call.execute()
        return response.body!!.string()
    }

    override fun download(url: String) = client.newCall(Request.Builder().url(url).build()).execute().body!!.byteStream()
    override fun stop() {

    }


}