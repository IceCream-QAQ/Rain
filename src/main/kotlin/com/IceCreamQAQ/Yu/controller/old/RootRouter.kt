package com.IceCreamQAQ.Yu.controller.old

open class RootRouter {
    val router: Router = RouterImpl(1)

    val interceptorInfo = InterceptorInfo(arrayListOf(), arrayListOf(), arrayListOf())
    // var globalBefores: MutableList<MethodInvoker> = ArrayList()
    // var globalAfters: MutableList<MethodInvoker> = ArrayList()
    // var globalCatchs: MutableList<CatchInvoker> = ArrayList()
    //
    // val globalBeforeList: MutableList<DoMethod<Before>> = ArrayList()
    // val globalAfterList: MutableList<DoMethod<After>> = ArrayList()
    // val globalCatchList: MutableList<DoCatch> = ArrayList()
}