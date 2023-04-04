package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.util.classMaker.asm.ASMClass


fun main() {
    ASMClass.makeClass<Any>("com.make.class.Hello") {
        public()
        final()

        method("main"){
            public()
            static()

            val args = parameter<String>("args")

        }
    }
}