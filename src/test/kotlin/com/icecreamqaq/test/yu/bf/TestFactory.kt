package com.icecreamqaq.test.yu.bf

import com.IceCreamQAQ.Yu.di.BeanFactory
import com.icecreamqaq.test.yu.util.TestUtil

class TestFactory :BeanFactory<TestUtil> {

    init {
        println("Created TestFactory")
    }

    override fun createBean(): TestUtil {
        println("new TestUtil")
        return TestUtil("Test")
    }
}