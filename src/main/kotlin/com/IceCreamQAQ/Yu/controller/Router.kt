package com.IceCreamQAQ.Yu.controller

typealias ParasMap = Map<String, Any>

interface Router {

    fun init(rootRouter: RouterInfo)
    suspend operator fun invoke(context: ActionContext, superParas: ParasMap): Boolean

}

//interface DefaultRouter : Router {
//
//    val staticRouterMap: HashMap<String, Router>
//    val needMatchRouterMap: HashMap<String, Router>
//
////    val actionInvokerMap: HashMap<String, Router>
//
//    override suspend operator fun invoke(context: ActionContext): Boolean {
//
//    }
//
//}





