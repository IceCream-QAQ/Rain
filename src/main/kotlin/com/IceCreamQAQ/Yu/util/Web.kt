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
import okhttp3.internal.userAgent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class WebProxy {
    lateinit var type: String
    lateinit var host: String
    var port: Int = 0
    var username: String? = null
    var password: String? = null
}

class WebConfig {

    var ua: String? = null

    var readTimeout: Long? = null
    var writeTimeout: Long? = null
    var connectTimeout: Long? = null

    var proxy: WebProxy? = null

}

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

    fun init()

    fun get(url: String): String
    fun post(url: String, para: Map<String, String>): String
    fun postJSON(url: String, json: String): String
    fun postJSON(url: String, obj: Any) = postJSON(url, obj.toJSONString())

    fun download(url: String): InputStream
    fun download(url: String, file: File? = null) = IO.copy(download(url), FileOutputStream(file ?: IO.tmpFile()))
    fun download(url: String, saveLocation: String) = download(url, File(saveLocation))

    fun stop()
}

class WebHelperBeanFactory : BeanFactory<Web> {

    @Config("yu.webHelper.impl")
    @Default("com.IceCreamQAQ.Yu.util.OkHttpWebImpl")
    private lateinit var implClass: String

    @Inject
    private lateinit var context: YuContext

    private var web: Web? = null


    @Inject
    fun init() {
        val clazz = Class.forName(implClass)
        context.register(clazz, true)
        web = context.newBean(clazz, null, true) as Web
        web!!.init()
    }

    override fun createBean(clazz: Class<Web>, name: String): Web? = web
}

@NotSearch
class OkHttpWebImpl : Web {

    lateinit var client: OkHttpClient
    lateinit var ua:String

    val domainMap = ConcurrentHashMap<String, MutableMap<String, Cookie>>()

    @Config("yu.web")
    var config: WebConfig? = null

    fun getDomainCookies(domain: String) =
            domainMap[domain] ?: {
                val domainCookies = HashMap<String, Cookie>()
                domainMap[domain] = domainCookies
                domainCookies
            }()

    override fun init() {
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

        val config = this.config ?: {
            val wc = WebConfig()
            this.config = wc
            wc
        }()
        ua = config.ua?: "Rain/$userAgent"

        val proxy = config.proxy
        if (proxy != null) {
            when (proxy.type.toLowerCase()) {
                "http", "https" -> builder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxy.host, proxy.port)))
                "socks", "socks5" -> builder.proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress(proxy.host, proxy.port)))
            }
            builder.proxyAuthenticator(object : Authenticator {
                @Throws(IOException::class)
                override fun authenticate(route: Route?, response: Response): Request? {
                    //设置代理服务器账号密码
                    val credential = Credentials.basic(proxy.username ?: "", proxy.password ?: "")
                    return response.request.newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build()
                }
            })
        }

        if (config.readTimeout != null) builder.readTimeout(config.readTimeout!!, TimeUnit.MINUTES)
        if (config.writeTimeout != null) builder.writeTimeout(config.writeTimeout!!, TimeUnit.MINUTES)
        if (config.connectTimeout != null) builder.connectTimeout(config.connectTimeout!!, TimeUnit.MINUTES)


//        builder.
//        builder.readTimeout()

        client = builder.build()
    }

    override fun saveCookie(domain: String, path: String, name: String, value: String, expiresAt: Long, httpOnly: Boolean, hostOnly: Boolean) {
        val cb = Cookie.Builder().domain(domain).path(path).name(name).value(value).expiresAt(expiresAt)
        if (httpOnly) cb.httpOnly()
        if (hostOnly) cb.hostOnlyDomain(domain)
        getDomainCookies(domain)[name] = cb.build()
    }

    override fun get(url: String) = client.newCall(createRequest(url)).execute().body!!.string()

    override fun post(url: String, para: Map<String, String>): String {
        val fbBuilder = FormBody.Builder()
        for (s in para.keys) {
            fbBuilder.add(s, para[s] ?: "")
        }
        val formBody = fbBuilder.build()
//        val request = Request.Builder().post(formBody).url(url).build()
        val call = client.newCall(createRequest(url, formBody))
        val response = call.execute()
        return response.body!!.string()
    }

    override fun postJSON(url: String, json: String): String {
        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)
//        val request = Request.Builder().post(requestBody).url(url).build()
        val call = client.newCall(createRequest(url, requestBody))
        val response = call.execute()
        return response.body!!.string()
    }

    fun createRequest(url: String, requestBody: RequestBody? = null): Request {
        val rb = Request.Builder().url(url)
        if (requestBody != null) rb.post(requestBody)
        rb.header("User-Agent", ua)
        return rb.build()
    }

    override fun download(url: String) = client.newCall(createRequest(url)).execute().body!!.byteStream()
    override fun stop() {

    }

}