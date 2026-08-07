package test.rain

import rain.classloader.enchant.MethodParaNamedEnchanter

@MethodParaNamedEnchanter.MethodParaNamed
class KMethodParaNamedClass {

    fun method1(param1: String, param2: Int) {
        println("param1: $param1, param2: $param2")
    }

}