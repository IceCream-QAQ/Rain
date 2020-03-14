import com.IceCreamQAQ.Yu.Td
import java.lang.reflect.ParameterizedType

//fun main(){
//    val c = C::class.java
//    val i = c.superclass.interfaces
//    for (clazz in i) {
//        println(clazz.name)
//    }
//
//    val aa = c.getField("aa")
//    println(aa.type.componentType)
//
//    val bb = c.getField("bb")
//    println(bb.type.componentType)
//
//    val cc=c.getField("cc")
//    println(cc.type.name)
//    println(cc.genericType.typeName)
//    println(cc.genericType)
//    println(((cc.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>).name)
//}
//
//
//interface A
//
//open class B :A
//
//class C: B() {
//
//    lateinit var aa:Array<String>
//    lateinit var bb:Array<Array<String>>
//    lateinit var cc:List<String>
//
//}

fun main() {
    val array = arrayOf<Any?>("1", "2", "3")
    Td.`fun`(array)
}