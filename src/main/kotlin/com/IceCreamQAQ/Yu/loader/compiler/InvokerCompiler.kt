package com.IceCreamQAQ.Yu.loader.compiler

import com.IceCreamQAQ.Yu.error.InvokerClassCreateException
import com.IceCreamQAQ.Yu.loader.SpawnClassLoader
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class InvokerCompiler @Inject constructor(val classloader: SpawnClassLoader) {

    val log: Logger = LoggerFactory.getLogger(InvokerCompiler::class.java)
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
    fun doCompile(className: String, code: String): Class<*>? {
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

                throw InvokerClassCreateException("Create Invoker Class $className",it.problems)
            }
            for (c in it.classFiles) {
                val cc = this.classloader.define(className, c.bytes)
                log.trace("Compiler: ${cc.name}")
            }
        },
                problemFactory) {
            override fun handleInternalException(e: Throwable, ud: CompilationUnitDeclaration, result: CompilationResult) {}
        }

        jdtCompiler.compile(compilationUnits)

        return Class.forName(className, true, classloader)
    }


}
