package com.IceCreamQAQ.Yu.controller

open class RouterImpl(val level: Int) : Router {

    val needMath = ArrayList<MatchItem>()
    val noMatch = HashMap<String, RouterImpl>()

    override fun init(rootRouter: RootRouter) {
        for (router in noMatch.values) {
            router.init(rootRouter)
        }
        for (item in needMath) {
            item.router.init(rootRouter)
        }
    }

    override suspend fun invoke(path: String, context: ActionContext): Boolean {
        val cps = context.path.size
        val nextPath = when {
            level > cps -> return false
            level == cps -> ""
            else -> context.path[level]
        }
        val nor = noMatch[path]
        if (nor != null) return nor.invoke(nextPath, context)
        for (matchItem in needMath) {
            val m = matchItem.p.matcher(path)
            if (m.find()) {
                if (matchItem.needSave) {
                    for ((i, name) in matchItem.matchNames!!.withIndex()) {
                        context[name] = m.group(i + 1)
                    }
                }
                if (matchItem.invoke(nextPath, context)) return true
            }
        }
        return false
    }

}