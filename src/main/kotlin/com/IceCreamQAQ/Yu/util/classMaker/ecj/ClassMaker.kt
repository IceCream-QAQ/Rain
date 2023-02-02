package com.IceCreamQAQ.Yu.util.classMaker.ecj

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.DefaultController
import com.IceCreamQAQ.Yu.cache.EhcacheHelp
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.util.classMaker.*
import com.IceCreamQAQ.Yu.util.sout
import org.eclipse.jdt.internal.compiler.CompilationResult
import org.eclipse.jdt.internal.compiler.Compiler
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies
import org.eclipse.jdt.internal.compiler.IProblemFactory
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit
import org.eclipse.jdt.internal.compiler.env.INameEnvironment
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory
import org.jetbrains.annotations.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.HashMap
import kotlin.collections.HashSet

object ClassMakerCompiler {

    val log: Logger = LoggerFactory.getLogger(ClassMakerCompiler::class.java)
    val sourceList = HashSet<String>()

    val compilerOptions = {
        val opt = { map: MutableMap<String, String>, key: String, value: String ->
            map[key] = value
        }
        val map = HashMap<String, String>()
        opt(map, CompilerOptions.OPTION_ReportMissingSerialVersion, CompilerOptions.IGNORE)
        opt(map, CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE)
        opt(map, CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE)
        opt(map, CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE)
        opt(map, CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE)
        opt(map, CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE)
        opt(map, CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE)
        opt(map, CompilerOptions.OPTION_Encoding, "UTF-8")
        opt(map, CompilerOptions.OPTION_Process_Annotations, CompilerOptions.ENABLED)
        opt(map, CompilerOptions.OPTION_Source, "8")
        opt(map, CompilerOptions.OPTION_TargetPlatform, "8")
        opt(map, CompilerOptions.OPTION_Compliance, "8")
        opt(map, CompilerOptions.OPTION_MethodParametersAttribute, CompilerOptions.GENERATE)
        CompilerOptions(map)
    }()

    val nameEnv = object : INameEnvironment {

        fun findType(type: String): NameEnvironmentAnswer? {
            val cl = this::class.java.classLoader
            val i = cl.getResourceAsStream(type.replace(".", "/") + ".class") ?: return null
            val bytes = i.readBytes()
            val classFileReader = ClassFileReader(bytes, type.toCharArray(), true)
            return NameEnvironmentAnswer(classFileReader, null)
        }

        override fun findType(type: Array<out CharArray>): NameEnvironmentAnswer? {
            val sb = StringBuilder()
            for (i in type.indices) {
                if (i != 0) {
                    sb.append('.')
                }
                sb.append(type[i])
            }
            return findType(sb.toString())
        }

        override fun findType(typeName: CharArray, packageName: Array<out CharArray>): NameEnvironmentAnswer? {
            val sb = StringBuilder()
            for (chars in packageName) {
                sb.append(chars)
                sb.append('.')
            }
            sb.append(typeName)
            return findType(sb.toString())
        }

        override fun isPackage(parentPackageName: Array<out CharArray>?, packageName: CharArray): Boolean {
            val sb = StringBuilder()
            if (parentPackageName != null) {
                for (p in parentPackageName) {
                    sb.append(String(p))
                    sb.append(".")
                }
            }
            sb.append(String(packageName))
            val name = sb.toString()
            if (sourceList.contains(name))return false
            return try {
                Class.forName(name)
                false
            } catch (e: ClassNotFoundException) {
                true
            }
        }

        override fun cleanup() {
        }

    }

    @Synchronized
    fun doCompile(classLoader: AppClassloader, className: String, code: String): Class<*>? {
        val compilationUnits = arrayOfNulls<ICompilationUnit>(1)
        sourceList.add(className)
        compilationUnits[0] = object : ICompilationUnit {
            override fun getFileName() = "$mainTypeName.java".toCharArray()

            override fun getContents() = code.toCharArray()

            override fun getMainTypeName() = className.split(".").last()/*.split("$")[0]*/.toCharArray()

            override fun getPackageName(): Array<CharArray?> {
                val f = className.split(".")
                if (f.size == 1) return emptyArray()
                val c = arrayOfNulls<CharArray>(f.size - 1)
                for (i in 0..f.size - 2) {
                    c[i] = f[i].toCharArray()
                }
                return c
            }

        }
        val policy = DefaultErrorHandlingPolicies.exitOnFirstError()
        val problemFactory: IProblemFactory = DefaultProblemFactory(Locale.ENGLISH)

        val jdtCompiler = object : Compiler(
            nameEnv,
            policy,
            compilerOptions, {

                if (it.hasErrors()) {

//                    throw InvokerClassCreateException("Create Invoker Class $className",it.problems)
                }
                for (c in it.classFiles) {
                    val cc = classLoader.define(className, c.bytes)
                    log.trace("Compiler: ${cc.name}")
                }
            },
            problemFactory) {
            override fun handleInternalException(e: Throwable, ud: CompilationUnitDeclaration, result: CompilationResult) {}
        }

        jdtCompiler.compile(compilationUnits)

        return Class.forName(className, true, classLoader)
    }

}

fun <T> makeClass(
    name: String,
    access: Access = Access.PUBLIC,
    abstract: Boolean = false,
    static: Boolean = false,
    final: Boolean = false,
    superClass: Class<*> = Any::class.java,
    interfaceClass: Class<*>? = null,
    op: ClassMaker.() -> Unit
): String {
    val classMaker = ClassMaker(name, access, abstract, static, final, superClass, interfaceClass)

    op(classMaker)

    return classMaker.toString()
}


fun main() {

    makeClass<Any>("com.IceCreamQAQ.make.test.Test") {
        annotation<DefaultController>()

        field<EhcacheHelp<String>>("sessionContext") {
            annotation<Inject>()
            annotation<Named>("\"SessionContext\"")
        }

        field<YuContext>("context", Access.PRIVATE) {
            annotation<Inject>()

            setter()
        }

        method("testAction") {
            annotation<Action>("\"testAction\"")

            parameters {
                "name" to String::class.java
                parameter<String>("clazz")
                parameter<String>("group") {
                    annotation<NotNull>()
                }
            }
            returnType<String>()

            body(
                """
                    return "HelloWorld!";
                """.trimIndent()
            )
        }

    }.sout()
}