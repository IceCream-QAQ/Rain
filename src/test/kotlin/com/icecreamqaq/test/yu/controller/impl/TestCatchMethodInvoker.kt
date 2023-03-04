package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.controller.ControllerInstanceGetter
import com.IceCreamQAQ.Yu.controller.dss.PathActionContext
import com.IceCreamQAQ.Yu.controller.simple.SimpleKJReflectMethodInvoker
import java.lang.reflect.Method

class TestCatchMethodInvoker(
    method: Method,
    instance: ControllerInstanceGetter,
    val throwableType:Class<out Throwable>
) : TestMethodInvoker(method, instance) {



}