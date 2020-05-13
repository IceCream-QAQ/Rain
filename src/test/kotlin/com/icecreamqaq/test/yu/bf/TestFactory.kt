package com.icecreamqaq.test.yu.bf

import com.IceCreamQAQ.Yu.di.BeanFactory
import com.icecreamqaq.test.yu.util.TestUtil

class TestFactory : BeanFactory<TestUtil> {

    override fun createBean(clazz: Class<TestUtil>, name: String): TestUtil {
        println("new TestUtil")
        return TestUtil(name)
    }
}