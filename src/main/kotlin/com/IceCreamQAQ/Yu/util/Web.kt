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
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

interface Web {

    fun saveCookie(
            domain: String,
            path: String,
            name: String,
            value: String,
            expiresAt: Long = 253402300799999L,
            httpOnly: Boolean = true,
            hostOnly: Boolean = false
    )

    fun get(url: String): String
    fun post(url: String, para: Map<String, String>): String
    fun postJSON(url: String, json: String): String
    fun postJSON(url: String, obj: Any) = postJSON(url, obj.toJSONString())

    fun download(url: String): InputStream
    fun download(url: String, file: File? = null) = IO.copy(download(url), FileOutputStream(file ?: IO.tmpFile()))
    fun download(url: String, saveLocation: String) = download(url, File(saveLocation))

    fun stop()
}

class WebHelperBeanFactory : BeanFactory<Web>, ApplicationService {

    @Config("yu.webHelper.impl")
    @Default("com.IceCreamQAQ.Yu.util.OkHttpWebImpl")
    private lateinit var implClass: String

    @Inject
    private lateinit var context: YuContext

    private var web: Web? = null


    override fun init() {
        val clazz = Class.forName(implClass)
        context.register(clazz, true)
        web = context.newBean(clazz, null, true) as Web
    }

    override fun start() {

    }

    override fun stop() {
        web?.stop()
    }

    override fun width() = 2

    override fun createBean(clazz: Class<Web>, name: String): Web? = web
}

@NotSearch
class OkHttpWebImpl : Web {

    var client: OkHttpClient

    val domainMap = ConcurrentHashMap<String, MutableMap<String, Cookie>>()

    fun getDomainCookies(domain: String) =
            domainMap[domain] ?: {
                val domainCookies = HashMap<String, Cookie>()
                domainMap[domain] = domainCookies
                domainCookies
            }()

    init {
        val builder = OkHttpClient.Builder()
        builder.cookieJar(object : CookieJar {


            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                for (cookie in cookies) {
                    val domain = cookie.domain
                    val host = url.host
                    if (domain == host) {
                        getDomainCookies(domain)[cookie.name] = cookie
                    }
                    if (host.endsWith(".$domain")) {
                        getDomainCookies(domain)[cookie.name] = cookie
                    }
                }
            }

            fun domainType(domain: String): Int {
                if (domain.contains(":")) return 2
                return if (domain.last().toLowerCase().toInt() > 97) 0 else 1
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = ArrayList<Cookie>()
                val host = url.host

                val domains = ArrayList<String>()
                domains.add(host)
                if (domainType(host) == 0)
                    for ((i, c) in host.withIndex()) {
                        if (c == '.') domains.add(host.substring(i + 1))
                    }

                val time = System.currentTimeMillis()
                val path = url.encodedPath
                for (domain in domains) {
                    val dcs = domainMap[domain] ?: continue
                    val i = dcs.keys.iterator()
                    while (i.hasNext()) {
                        val cookieName = i.next()
                        val cookie = dcs[cookieName]!!
                        if (cookie.expiresAt < time) {
                            i.remove()
                            continue
                        }
                        if (path.startsWith(cookie.path)) if (!cookie.hostOnly) cookies.add(cookie) else if (cookie.domain == domain) cookies.add(cookie)
                    }
                }
                return cookies
            }
        })
        client = builder.build()
    }

    override fun saveCookie(domain: String, path: String, name: String, value: String, expiresAt: Long, httpOnly: Boolean, hostOnly: Boolean) {
        val cb = Cookie.Builder().domain(domain).path(path).name(name).value(value).expiresAt(expiresAt)
        if (httpOnly) cb.httpOnly()
        if (hostOnly) cb.hostOnlyDomain(domain)
        getDomainCookies(domain)[name] = cb.build()
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