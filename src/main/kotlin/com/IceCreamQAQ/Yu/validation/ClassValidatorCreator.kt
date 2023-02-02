package com.IceCreamQAQ.YuWeb.validation

import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.toUpperCaseFirstOne
import com.IceCreamQAQ.Yu.validation.ValidateBy
import java.lang.reflect.Modifier

class ClassValidatorCreator(
    val classloader: IRainClassLoader
) {

//    private val vds: Array<ValidateData>

//    @Inject
//    private lateinit var compiler: InvokerCompiler

    fun spawnClassValidator(clazz: Class<*>): Class<out ClassValidatorBase> {
        val clazzAName = clazz.name.replace(".", "_")
        val implName = "${clazzAName}ClassValidator"

        val classBuilder = StringBuilder(
            "package impl.icecreamqaq.yu.v.ci.spawnImpl;\n" +
                    "\n" +
                    "import com.IceCreamQAQ.YuWeb.validation.*;\n" +
                    "\n" +
                    "import javax.inject.Inject;\n" +
                    "import java.lang.annotation.Annotation;\n" +
                    "\n" +
                    "public class $implName extends ClassValidatorBase {\n" +
                    "\n"
        )

        val privateParaList = arrayListOf<String>()

        val bodyBuilder = StringBuilder()
        val initBuilder = StringBuilder()
        val validateBuilder = StringBuilder(
            "    @Override\n" +
                    "    public ValidateResult validate(Annotation annotation, Object bean) {\n" +
                    "        ${clazz.name} b = (${clazz.name}) bean;\n" +
                    "        ValidateResult result;\n"
        )
        field@ for (field in clazz.declaredFields) {
//            if (field.is)

            val ff = field.name.toUpperCaseFirstOne()
            if (Modifier.isStatic(field.modifiers)) continue
            val type = if (Modifier.isPublic(field.modifiers)) field.name
            else if ((field.type == Boolean::class.java || field.type == Boolean::class.javaObjectType) && clazz.haveMethod(
                    "is$ff"
                )
            ) "is$ff()"
            else if (clazz.haveMethod("get$ff")) "get$ff()"
            else {
                privateParaList.add(field.name)
                continue
            }

            annotation@ for (annotation in field.annotations) {
                /*val vb = */annotation::class.java.interfaces[0].getAnnotation(ValidateBy::class.java)
                    ?: continue@annotation
                val ac = annotation::class.java.interfaces[0]
                val acn = ac.name.replace(".", "_")
                val vdn = "${field.name}${acn}vd"
                classBuilder.append("    private ValidateData $vdn;\n")
                initBuilder.append("        $vdn = JavaUtil.getValidatorData(factory, getClazz(), \"${field.name}\", ${ac.name}.class);\n")
                validateBuilder.append("        result = $vdn.getValidator().validate($vdn.getAnnotation(), b.$type);\n")
                validateBuilder.append("        if (result != null) return buildResult(\"${field.name}\",result);\n")
            }

        }

        val p = if (privateParaList.size == 0) "" else {
            val ppb = StringBuilder(", new String[]{ \"${privateParaList[0]}\"")
            for (i in 1 until privateParaList.size) {
                ppb.append(" , \"${privateParaList[i]}\"")
            }
            ppb.append(" }")
            ppb.toString()
        }

        classBuilder.append("\n")
        classBuilder.append(
            "    @Inject\n" +
                    "    public $implName(ValidatorFactory factory) {\n" +
                    "        super(factory, ${clazz.name}.class$p);\n" +
                    initBuilder.toString() +
                    "    }\n\n"
        )

        classBuilder.append(validateBuilder.toString())
        if (privateParaList.size == 0) classBuilder.append("        return null;")
        else classBuilder.append("        return reflectValidate(bean);")
        classBuilder.append("\n    }\n\n}")

        val classString = classBuilder.toString()

//        val clazz = compiler.doCompile("impl.icecreamqaq.yu.v.ci.spawnImpl.$implName", classString)
//        return clazz as Class<out ClassValidatorBase>
        TODO()
    }

    fun Class<*>.haveMethod(name: String) = try {
        this.getMethod(name)
        true
    } catch (ex: NoSuchMethodException) {
        false
    }

}