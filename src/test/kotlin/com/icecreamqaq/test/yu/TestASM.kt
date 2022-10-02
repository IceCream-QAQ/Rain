package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.IceCreamQAQ.Yu.error.ControllerLoadErr
import com.IceCreamQAQ.Yu.event.EventListenerLoader
import com.IceCreamQAQ.Yu.hook.HookMethod
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import javax.inject.Named
import kotlin.reflect.KClass

annotation class TestAnnotation(
    val z: Boolean,
    val b: Byte,
    val s: Short,
    val i: Int,
    val l: Long,
    val f: Float,
    val d: Double,
    val c: Char,
    val w: String,
    val t: KClass<*>,
    val za: BooleanArray,
    val ba: ByteArray,
    val sa: ShortArray,
    val ia: IntArray,
    val la: LongArray,
    val fa: FloatArray,
    val da: DoubleArray,
    val ca: CharArray,
    val wa: Array<String>,
    val ta: Array<KClass<*>>
)

@TestAnnotation(
    z = true,
    b = 0.toByte(),
    s = 0.toShort(),
    i = 0,
    l = 0L,
    f = 0F,
    d = 0.0,
    c = 'C',
    w = "String",
    t = TestAnnotation::class,
    za = [true, false],
    ba = [0, 1],
    sa = [0, 1],
    ia = [0, 1],
    la = [0, 1],
    fa = [0F, 1F],
    da = [0.0, 1.0],
    ca = ['0', '1'],
    wa = ["String0", "String1"],
    ta = [TestAnnotation::class, TestASM::class]
)
class TestASM

fun main() {

    val reader = ClassReader(
        TestASM::class.java.classLoader.getResourceAsStream("com/icecreamqaq/test/yu/TestASM.class")!!.readBytes()
    )
    val node = ClassNode()

    reader.accept(node, 0)

    node.visibleAnnotations.forEach {
        println("------------------------------------")
        println(it.desc)


        it.values.forEach { value ->
            println("\t${value::class.java}")
            if (value is List<*>){
                value.forEach { lv -> println("\t\t${lv!!::class.java}") }
            }
        }
    }

}