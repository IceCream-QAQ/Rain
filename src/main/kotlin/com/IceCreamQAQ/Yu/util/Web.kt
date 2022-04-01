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

class Header(val key: String, val value: String)


object HeaderBuilder {
    infix fun String.to(that: String) = Header(this, that)

    fun headerOf(
            ua: String? = null,
            referer: String? = null,
            vararg headers: Header
    ): List<Header> = arrayListOf<Header>().run {
        ua?.let { add("User-Agent" to it) }
        referer?.let { add("Referer" to it) }
        addAll(headers)
        this
    }
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

    fun get(url: String) = get(url, null)
    fun get(url: String, headers: HeaderBuilder.() -> List<Header>?) = get(url, headers(HeaderBuilder))
    fun get(url: String, headers: List<Header>?): String

    fun post(url: String, para: Map<String, String>) = post(url, para, null)
    fun post(url: String, para: Map<String, String>, headers: HeaderBuilder.() -> List<Header>?) = post(url, para, headers(HeaderBuilder))
    fun post(url: String, para: Map<String, String>, headers: List<Header>?): String

    fun postJSON(url: String, json: String) = postJSON(url, json, null)
    fun postJSON(url: String, json: String, headers: HeaderBuilder.() -> List<Header>?) = postJSON(url, json, headers(HeaderBuilder))
    fun postJSON(url: String, json: String, headers: List<Header>?): String

    fun postJSON(url: String, obj: Any) = postJSON(url, obj.toJSONString(), null)
    fun postJSON(url: String, obj: Any, headers: HeaderBuilder.() -> List<Header>?) = postJSON(url, obj.toJSONString(), headers(HeaderBuilder))
    fun postJSON(url: String, obj: Any, headers: List<Header>?) = postJSON(url, obj.toJSONString(), headers)

    //    fun download(url: String): InputStream
    fun download(url: String) = download(url, headers = null)
    fun download(url: String, headers: HeaderBuilder.() -> List<Header>?) = download(url, headers(HeaderBuilder))
    fun download(url: String, headers: List<Header>?): InputStream


    fun download(url: String, file: File? = null) = IO.writeFile(download(url), file ?: IO.tmpFile())
    fun download(url: String, saveLocation: String) = download(url, File(saveLocation))

    fun stop()
}

@NotSearch
class OkHttpWebImpl : Web {

    lateinit var client: OkHttpClient
    lateinit var ua: String

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
        ua = config.ua ?: "Rain/$userAgent"

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

    //    override fun get(url: String) =
    override fun get(url: String, headers: List<Header>?) = client.newCall(createRequest(url,headers)).execute().body!!.string()

    override fun post(url: String, para: Map<String, String>, headers: List<Header>?): String {
        val fbBuilder = FormBody.Builder()
        for (s in para.keys) {
            fbBuilder.add(s, para[s] ?: "")
        }
        val formBody = fbBuilder.build()
//        val request = Request.Builder().post(formBody).url(url).build()
        val call = client.newCall(createRequest(url, headers, formBody))
        val response = call.execute()
        return response.body!!.string()
    }

    override fun postJSON(url: String, json: String, headers: List<Header>?): String {
        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)
//        val request = Request.Builder().post(requestBody).url(url).build()
        val call = client.newCall(createRequest(url, headers, requestBody))
        val response = call.execute()
        return response.body!!.string()
    }

    fun createRequest(url: String, headers: List<Header>? = null, requestBody: RequestBody? = null): Request {
        val rb = Request.Builder().url(url)
        if (requestBody != null) rb.post(requestBody)
        rb.header("User-Agent", ua)
        headers?.let {
            for (header in headers) {
                rb.header(header.key, header.value)
            }
        }
        return rb.build()
    }

    override fun download(url: String, headers: List<Header>?) = client.newCall(createRequest(url, headers)).execute().body!!.byteStream()
    override fun stop() {

    }

}