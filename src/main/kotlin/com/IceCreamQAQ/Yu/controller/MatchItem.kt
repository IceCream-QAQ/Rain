package com.IceCreamQAQ.Yu.controller

import java.util.regex.Pattern

open class MatchItem(
        val needSave: Boolean,
        matchString: String,
        val matchNames: Array<String>?,
        val router: Router
) {
    val p: Pattern = Pattern.compile(matchString)

    suspend fun invoke(path: String, context: ActionContext): Boolean {
        return router.invoke(path, context)
    }
}