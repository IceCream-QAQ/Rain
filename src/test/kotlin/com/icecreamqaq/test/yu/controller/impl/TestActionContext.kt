package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.controller.dss.PathActionContext

class TestActionContext(val channel: String, path: Array<String>) : PathActionContext(path)