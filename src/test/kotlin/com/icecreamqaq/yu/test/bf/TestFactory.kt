package com.icecreamqaq.yu.test.bf

import com.IceCreamQAQ.Yu.di.BeanFactory
import com.icecreamqaq.yu.test.util.TestUtil

class TestFactory :BeanFactory<TestUtil> {

    init {
        println("Created TestFactory")
    }

    override fun createBean(): TestUtil {
        println("new TestUtil")
        return TestUtil("Test")
    }
}