package com.IceCreamQAQ.Yu.util.classMaker

import com.IceCreamQAQ.Yu.util.*

open class ClassMaker @JvmOverloads constructor(
    val name: String,
    override var access: Access = Access.PUBLIC,
    override var abstract: Boolean = false,
    override var static: Boolean = false,
    override var final: Boolean = false,
    var superClass: Class<*> = Any::class.java,
    var interfaceClass: Class<*>? = null
) : AnnotationAble, AbstractAble, StaticAble, FinalAble, AccessAble {

    val fields = arrayListOf<FieldMaker>()
    val methods = arrayListOf<MethodMaker>()
    override val annotations = arrayListOf<AnnotationMaker>()

//    override fun toString() = """
//        package ${name.split(".").let {
//            val sb = StringBuilder()
//            it.forEachIndexed { index, s -> if (index < it.size - 1) sb.append(s).append(".") }
//            sb.toString().subStringByLast(1)
//        }};
//
//        ${annotationsToString("\n")}${access.value} class ${name.split(".").last()}{
//            ${makeFieldsString()}${makeMethodsString()}
//        }
//    """.trimIndent()

    override fun toString() =
        StringBuilder().apply {
            val np = name.split(".")
            if (np.size > 1){
                append("package ${np[0]}")
                for (i in 1 until np.size - 1){
                    append(".")
                    append(np[i])
                }
                append(";\n\n")
            }

            append(annotationsToString("\n"))
            append("${access.value} class ${np.last()}{\n\n")
            append(makeFieldsString())
            append(makeMethodsString())
            append("}")
        }.toString()

    private fun makeFieldsString() =
        StringBuilder().apply { fields.forEach { append(it).append("\n\n") } }.toString()

    private fun makeMethodsString() =
        StringBuilder().apply { methods.forEach { append(it).append("\n\n") } }.toString()

//    // 构造函数部分
//    constructor(
//        name: String,
//        access: String = "public",
//        static: Boolean = false,
//        final: Boolean = false,
//        superClass: Class<*>
//    ) : this(name, access, static, final, superClass.name, null)
//
//    constructor(
//        name: String,
//        access: String = "public",
//        static: Boolean = false,
//        final: Boolean = false,
//        interfaceClass: Array<Class<*>>
//    ) : this(
//        name,
//        access,
//        static,
//        final,
//        Any::class.java.name,
//        interfaceClass.map { it.name }.toTypedArray()
//    )
//
//    constructor(
//        name: String,
//        access: String = "public",
//        static: Boolean = false,
//        final: Boolean = false,
//        superClass: Class<*>,
//        interfaceClass: Array<Class<*>>
//    ) : this(
//        name,
//        access,
//        static,
//        final,
//        superClass.name,
//        interfaceClass.map { it.name }.toTypedArray()
//    )
//    // 构造函数结束


}

inline fun <reified T> ClassMaker.field(
    name: String,
    access: Access = Access.PUBLIC,
    static: Boolean = false,
    final: Boolean = false,
    defaultValue: String? = null,
    noinline op: FieldMaker.() -> Unit
) = field(name, access, static, final, T::class.java, defaultValue, op)

fun ClassMaker.field(
    name: String,
    access: Access = Access.PUBLIC,
    static: Boolean = false,
    final: Boolean = false,
    type: Class<*> = Any::class.java,
    defaultValue: String? = null,
    op: FieldMaker.() -> Unit
) {
    val fieldMaker = FieldMaker(this, name, access, static, final, type, defaultValue)
    op(fieldMaker)
    fields.add(fieldMaker)
}

inline fun <reified T> ClassMaker.field(
    name: String,
    access: Access = Access.PUBLIC,
    static: Boolean = false,
    final: Boolean = false,
    defaultValue: String? = null
) = field(name, access, static, final, T::class.java, defaultValue)

fun ClassMaker.field(
    name: String,
    access: Access = Access.PUBLIC,
    static: Boolean = false,
    final: Boolean = false,
    type: Class<*> = Any::class.java,
    defaultValue: String? = null
) {
    val fieldMaker = FieldMaker(this, name, access, static, final, type, defaultValue)
    fields.add(fieldMaker)
}

fun ClassMaker.method(
    name: String,
    access: Access = Access.PUBLIC,
    static: Boolean = false,
    final: Boolean = false,
    parameters: Array<MethodParameter>? = null,
    returnType: Class<*> = Unit::class.java,
    op: MethodMaker.() -> Unit
) {
    val methodMaker = MethodMaker(name, access, static, final, parameters, returnType)
    op(methodMaker)
    methods.add(methodMaker)
}