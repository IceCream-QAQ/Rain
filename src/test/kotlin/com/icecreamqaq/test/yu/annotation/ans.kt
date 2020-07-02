package com.icecreamqaq.test.yu.annotation

import com.IceCreamQAQ.Yu.annotation.HookBy
import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.IceCreamQAQ.Yu.controller.DefaultControllerLoaderImpl
import javax.inject.Named

@LoadBy(DefaultControllerLoaderImpl::class)
@Named("priv")
annotation class CController

annotation class TestHook