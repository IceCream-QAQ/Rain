package com.IceCreamQAQ.Yu.controller.dss.router

import com.IceCreamQAQ.Yu.controller.dss.PathActionContext

fun interface RouterMatcher<CTX : PathActionContext> {

    operator fun invoke(path: String?, context: CTX): Boolean

}

class NamedVariableMatcher<CTX : PathActionContext>(val name: String) : RouterMatcher<CTX> {
    override fun invoke(path: String?, context: CTX): Boolean {
        context[name] = path
        return true
    }
}

class RegexMatcher<CTX : PathActionContext>(regex: String, val names: Array<String>) : RouterMatcher<CTX> {

    val matcher = regex.toRegex()

    override fun invoke(path: String?, context: CTX): Boolean =
        path?.let { matcher.find(it) }?.run {
            names.forEachIndexed { i, it -> context[it] = groupValues[i + 1] }
            true
        } ?: false

}