package com.IceCreamQAQ.Yu.hook

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.HookBy
import com.IceCreamQAQ.Yu.annotation.InstanceMode
import com.IceCreamQAQ.Yu.hasAnnotation
import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer
import com.IceCreamQAQ.Yu.nameWithParams
import com.IceCreamQAQ.Yu.util.*
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.getOrPut


class HookImpl(val classLoader: IRainClassLoader, override val superHook: IHook?) : IHook, ClassTransformer {

    companion object {
        private val systemMethod = arrayOf(
            "<init>",
            "<clinit>"
        )


        private const val iHookOwner = "com/IceCreamQAQ/Yu/hook/IHook"
        private const val iHookType = "L$iHookOwner;"

        private const val yuHookOwner = "com/IceCreamQAQ/Yu/hook/YuHook"
        private const val yuHookType = "L$yuHookOwner;"


        private const val hookInfoOwner = "com/IceCreamQAQ/Yu/hook/HookInfo"
        private const val hookInfoType = "L$hookInfoOwner;"

        private const val hookContextOwner = "com/IceCreamQAQ/Yu/hook/HookContext"
        private const val hookContextType = "L$hookContextOwner;"

        private const val hookRunnableOwner = "com/IceCreamQAQ/Yu/hook/HookRunnable"
        private const val hookRunnableType = "L$hookRunnableOwner;"
    }

    init {
        classLoader.forName("com.IceCreamQAQ.Yu.hook.YuHook", true)
            .getDeclaredField("hookInstance")
            .let {
                it.isAccessible = true
                it.set(null, this)
            }
    }

    private val matchList: ArrayList<IHookItem> = ArrayList()
    private val hookClasses: ArrayList<HookClass> = ArrayList()

    private val hookRunnableInfoMap: HashMap<String, HookRunnableInfo> = HashMap()

    override fun registerHook(item: HookItem) {
        item.toItem().let {
            matchList.add(it)
            hookRunnableInfoMap[it.hookRunnableInfo.className] = it.hookRunnableInfo
        }
    }

    fun HookItem.toItem(): IHookItem =
        findHook(runnable).let { runnable ->
            when (type) {
                "method" -> FullMatchHookItem(basedClass, basedMethod!!, basedDesc, runnable)
                else -> error("[YuHook] 未能解析的 HookItem 类型: $type。($this)")
            }
        }


    override fun findHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo = createInstanceHookInfo(clazz, methodName, sourceMethodName, methodParas)

    override fun createInstanceHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo {
        val paramDesc = toDesc(methodParas)

        val method = clazz.getMethod(methodName, *methodParas)
        val sourceMethod = clazz.getMethod(methodName, *methodParas)
        return let {
            hookClasses.firstOrNull { it.clazz == clazz.name } ?: error("[YuHook] 未找到类: ${clazz.name} 的上下文！")
        }.method
            .run {
                firstOrNull { it.name == methodName && it.paramDesc == paramDesc }
                    ?: error("[YuHook] 未找到类: ${clazz.name}, 方法: ${method.nameWithParams} 的上下文！")
            }
            .let {
                HookInfo(clazz.name, methodName, clazz, method, sourceMethod, methodParas)
                    .apply {
                        it.standardHooks.forEach { hri -> putRunnable(hri.instance) }
                    }
            }
    }

    fun findHook(clazz: String): HookRunnableInfo =
        hookRunnableInfoMap.getOrPut(clazz) { createHookRunnableInfo(clazz) }

    private fun createHookRunnableInfo(clazz: String) =
        classLoader.forName(clazz, false)
            .let { HookRunnableInfo(clazz, it.hasAnnotation<InstanceMode>(), it as Class<out HookRunnable>) }


    data class HookMethodInfo(val method: HookMethod, val node: MethodNode)

    override fun transform(node: ClassNode, className: String): Boolean {
        val classMatches = matchList.filter { it.checkClass(className, node) }
        if (classMatches.isEmpty()) return false

        /***
         * 数据结构:
         * standardHookMethods 负责记录所有标准 Hook 的 Method。
         * instanceHookMethods 负责记录所有实例 Hook 的 Method。
         * 如果一个方法内有多个 Hook，出现一个 实例 Hook 则记录为 实例 Hook。
         * 如果一个方法是静态方法，则忽略所有实例 Hook。
         *
         * instanceHookRunnableInfos 负责记录具体 HookRunnable 与 Method 关联性。
         * 如 一个 Hook，同时 Hook 了当前类内的多个方法，则需要向多个方法的 HookInfo 内 put 本 Runnable。
         */

        val standardHookMethods = ArrayList<HookMethodInfo>()
        val instanceHookMethods = ArrayList<HookMethodInfo>()
        val instanceHookRunnableInfos = HashMap<HookRunnableInfo, ArrayList<HookMethod>>()

        // 筛选部分，遍历原 Class 所有 Method，将符合条件 Method 分类收集。
        node.methods.filter { it.name !in systemMethod }.forEach { method ->
            val standardHooks = ArrayList<HookRunnableInfo>()
            val instanceHooks = ArrayList<HookRunnableInfo>()

            method.visibleAnnotations?.forEach {
                try {
                    classLoader.forName(
                        it.desc
                            .substring(1, it.desc.length - 1)
                            .replace("/", "."),
                        false
                    ).annotation<HookBy> {
                        findHook(value).apply {
                            (if (isInstanceMode) instanceHooks else standardHooks).add(this)
                        }
                    }
                } catch (ignore: Exception) {
                }
            }

            classMatches.forEach {
                if (it.checkMethod(method.name, method.desc, method))
                    (if (it.hookRunnableInfo.isInstanceMode) instanceHooks else standardHooks).add(it.hookRunnableInfo)
            }

            // 判断 method 是否为静态方法，如果是静态方法则忽略所有 InstanceHook。
            if (method.access shr 3 and 1 == 1) instanceHooks.clear()

            if (standardHooks.isNotEmpty() || instanceHooks.isNotEmpty())
                let {
                    if (instanceHooks.isNotEmpty()) instanceHookMethods
                    else standardHookMethods
                }.add(
                    HookMethodInfo(
                        HookMethod(
                            className,
                            method.name,
                            method.desc,
                            UUID.randomUUID().toString().replace("-", ""),
                            standardHooks,
                            instanceHooks
                        ), method
                    ).apply {
                        instanceHooks.forEach {
                            instanceHookRunnableInfos.getOrPut(it) { ArrayList() }.add(this.method)
                        }
                    }
                )
        }

        // 如果没有收集到任何符合条件的 Method，则当前 Class 不需要增强。
        if (standardHookMethods.isEmpty() && instanceHookMethods.isEmpty()) return false

        val classOwner = className.replace(".", "/")
        val classType = "L$classOwner;"
        val identifier = UUID.randomUUID().toString().replace("-", "")

        val classClass = Type.getType(classType)

        // 开始增强，判断有无 InstanceHook
        if (instanceHookRunnableInfos.isNotEmpty()) {
            val initMethodName = "${identifier}instance"
            val fieldType = Type.getDescriptor(HookInfo::class.java)

            // 创建 InstanceMode 初始化方法。
            node.methods.add(
                MethodNode(ACC_PRIVATE, initMethodName, "()V", null, null)
                    .apply {
                        createInitMethod(
                            instanceHookMethods,
                            classOwner,
                            classClass,
                            false
                        ) {
                            node.fields.add(FieldNode(ACC_PRIVATE, it.method.identifier, fieldType, null, null))
                            node.methods.add(hook(it.method, classOwner, it.node))
                        }
                    }
            )

            // 循环遍历所有构造函数，并将符合条件的构造函数插入调用初始化函数代码。
            node.methods
                .filter { it.name == "<init>" }
                .forEach {
                    for (insn in it.instructions) {
                        if (insn is MethodInsnNode)
                            if (insn.opcode == INVOKESPECIAL && insn.owner == classOwner && insn.name == "<init>") return@forEach
                    }
                    it.instructions.remove(it.instructions.first { insn -> insn is InsnNode && insn.opcode == RETURN })
                    it.instructions.add(VarInsnNode(ALOAD, 0))
                    it.instructions.add(
                        MethodInsnNode(INVOKEVIRTUAL, classOwner, initMethodName, "()V", false)
                    )
                    it.instructions.add(InsnNode(RETURN))
                }

            // 为所有 InstanceHook 生成 Setter。
            instanceHookRunnableInfos.forEach { (hook, methods) ->
                node.methods.add(
                    MethodNode(
                        ACC_PUBLIC,
                        "set${hook.clazz.simpleName}",
                        "(${hook.descriptor})V",
                        null,
                        null
                    ).apply {
                        visibleAnnotations = arrayListOf(AnnotationNode("Ljavax/inject/Inject;"))
                        visitCode()
                        methods.forEach {
                            visitVarInsn(ALOAD, 0)
                            visitFieldInsn(GETFIELD, classOwner, it.identifier, fieldType)
                            visitVarInsn(ALOAD, 1)
                            visitMethodInsn(
                                INVOKEVIRTUAL,
                                hookInfoOwner,
                                "putRunnable",
                                "($hookRunnableType)V",
                                false
                            )
                        }
                        visitInsn(RETURN)
                        visitMaxs(2, 2)
                        visitEnd()
                    }
                )
            }
        }

        // 开始增强，判断有无 StandardHook
        if (standardHookMethods.isNotEmpty()) {
            // 向 <clinit> 内插入调用构建初始化函数的代码
            let {
                node.methods.firstOrNull { it.name == "<clinit>" }
                    ?: MethodNode(ACC_STATIC, "<clinit>", "()V", null, null)
                        .apply {
                            node.methods.add(this)
                            instructions.insert(InsnNode(RETURN))
                        }
            }.instructions.insert(
                MethodInsnNode(
                    INVOKESTATIC,
                    classOwner,
                    "${identifier}standard",
                    "()V",
                    false
                )
            )

            val initMethodName = "${identifier}standard"

            MethodNode(ACC_PRIVATE or ACC_STATIC, initMethodName, "()V", null, null)
                .apply {
                    createInitMethod(
                        standardHookMethods,
                        classOwner,
                        classClass,
                        true
                    ) {
                        node.fields.add(
                            FieldNode(
                                ACC_PRIVATE or ACC_STATIC,
                                it.method.identifier,
                                hookInfoType,
                                null,
                                null
                            )
                        )
                        node.methods.add(hook(it.method, classOwner, it.node))
                    }
                    node.methods.add(this)
                }
        }

        HookClass(className).apply {
            method.apply {
                standardHookMethods.forEach { add(it.method) }
                instanceHookMethods.forEach { add(it.method) }
            }
            hookClasses.add(this)
        }

        return true
    }

    private fun MethodVisitor.createInitMethod(
        methods: List<HookMethodInfo>,
        classOwner: String,
        classType: Type,
        static: Boolean,
        body: (HookMethodInfo) -> Unit
    ) {

        visitCode()
        val invokeMethod = if (static) "findHookInfo" else "createInstanceHookInfo"

        val infoParamIndex = if (static) 0 else 1

        visitMethodInsn(
            INVOKESTATIC,
            yuHookOwner,
            "findHook",
            "()$iHookType",
            false
        )
        visitVarInsn(ASTORE, infoParamIndex)

        var hasParam = false

        methods.forEach {
            body(it)

            if (!static) visitVarInsn(ALOAD, 0)
            visitVarInsn(ALOAD, infoParamIndex)

            visitLdcInsn(classType)
            visitLdcInsn(it.method.name)
            visitLdcInsn(it.method.changeToName)

            val params = toClassArray(it.method.descriptor)
            visitIntInsn(params.size)
            visitTypeInsn(ANEWARRAY, "java/lang/Class")

            if (params.isNotEmpty()) {
                hasParam = true
                params.forEachIndexed { i, param ->
                    visitInsn(DUP)
                    visitIntInsn(i)
                    if (param.simple) visitFieldInsn(GETSTATIC, param.type, "TYPE", "Ljava/lang/Class;")
                    else visitLdcInsn(Type.getType(param.type))
                    visitInsn(AASTORE)
                }
            }

            visitMethodInsn(
                INVOKEINTERFACE,
                iHookOwner,
                invokeMethod,
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Class;)$hookInfoType",
                true
            )
            if (static) visitFieldInsn(PUTSTATIC, classOwner, it.method.identifier, hookInfoType)
            else visitFieldInsn(PUTFIELD, classOwner, it.method.identifier, hookInfoType)
        }

        visitInsn(RETURN)
        visitMaxs((if (static) 5 else 6).let { if (hasParam) it + 3 else it }, if (static) 1 else 2)
        visitEnd()
    }

    /*** Hook 实现思路
     *
     * 将原本方法改名，并创建一个新的方法取代原方法。
     * 将原方法所有方法注解，与参数注解删除，转移到新方法上。
     *
     * 新方法结构大致如下:
     *
     *     public ReturnType newMethod(params) throws Throwable {
     *         HookContext context = new HookContext();
     *         HookInfo info = infoField;
     *         Object[] params = new Object[]{ params };
     *         context.info = info;
     *         context.params = params;
     *
     *         if (info.perRun(context)) {
     *             return (String) context.result;
     *         }
     *         try {
     *             context.result = this.oldMethod(context.params[]);
     *         } catch (Throwable error) {
     *             context.error = error;
     *             if (info.onError(context)) {
     *                 return (ReturnType) context.result;
     *             }
     *             throw context.error;
     *         }
     *         info.postRun(context);
     *         return (ReturnType) hookMethod.result;
     *     }
     *
     */
    private fun hook(
        hook: HookMethod,
        classOwner: String,
        sourceMethod: MethodNode,
    ): MethodNode {
        val isStatic = sourceMethod.access shr 3 and 1 == 1
        val firstStack = if (isStatic) 0 else 1

        val descSplit = sourceMethod.desc.split(")")

        val params = readPara(descSplit[0].substring(1), firstStack)

        val returnFlag = descSplit[1] != "V"
        val returnType = descSplit[1]

        var stack = if (isStatic) 0 else 1
        params.forEach { stack += it.stackSize }


        val newMethod = MethodNode(
            sourceMethod.access,
            sourceMethod.name,
            sourceMethod.desc,
            sourceMethod.signature,
            arrayOf("java/lang/Throwable")
        ).apply {
            visitCode()
            val tryStart = Label()
            val tryEnd = Label()
            val catchLabel = Label()
            val resultLabel = Label()
            visitTryCatchBlock(tryStart, tryEnd, catchLabel, "java/lang/Throwable")

            val hookContextStack = stack++
            val hookInfoStack = stack++
            val paramsStack = stack++


            // new HookMethod
            apply {
                visitTypeInsn(NEW, hookContextOwner)
                visitInsn(DUP)
                visitMethodInsn(INVOKESPECIAL, hookContextOwner, "<init>", "()V", false)
                visitVarInsn(ASTORE, hookContextStack)
            }

            // 准备 info
            apply {
                if (hook.isInstanceMode) {
                    visitVarInsn(ALOAD, 0)
                    visitFieldInsn(GETFIELD, classOwner, hook.identifier, hookInfoType)
                } else visitFieldInsn(GETSTATIC, classOwner, hook.identifier, hookInfoType)
                visitVarInsn(ASTORE, hookInfoStack)
            }

            // 准备 Params
            apply {
                // new Array
                visitIntInsn(params.size + 1)
                visitTypeInsn(ANEWARRAY, "java/lang/Object")

                // 向 Param Array 中写入 this，当方法为 static 时写入 null。
                visitInsn(DUP)
                visitInsn(ICONST_0)
                if (isStatic) visitInsn(ACONST_NULL) else visitVarInsn(ALOAD, 0)
                visitInsn(AASTORE)

                // 遍历参数，向数组中写入参数。
                params.forEachIndexed { i, it ->
                    visitInsn(DUP)
                    visitIntInsn(i + 1)
                    it.type.let { type ->
                        visitVarInsn(getLoad(type), it.stackNum)
                        // 判断是否为基础数据类型，如果是基础数据类型则需要调用对应封装类型 valueOf 转换为封装类型。
                        if (type.length == 1)
                            getTyped(type).let { typed ->
                                visitMethodInsn(INVOKESTATIC, typed, "valueOf", "($type)L$typed;", false)
                            }
                    }
                    visitInsn(AASTORE)
                }
                visitVarInsn(ASTORE, paramsStack)
            }

            // 将准备好的 HookInfo 与 Params Array 写入 HookContext
            apply {
                // 写入 HookInfo
                visitVarInsn(ALOAD, hookContextStack)
                visitVarInsn(ALOAD, hookInfoStack)
                visitFieldInsn(PUTFIELD, hookContextOwner, "info", hookInfoType)

                // 写入 Params Array
                visitVarInsn(ALOAD, hookContextStack)
                visitVarInsn(ALOAD, paramsStack)
                visitFieldInsn(PUTFIELD, hookContextOwner, "params", "[Ljava/lang/Object;")
            }

            // preRun
            apply {
                // invoke
                visitVarInsn(ALOAD, hookInfoStack)
                visitVarInsn(ALOAD, hookContextStack)
                visitMethodInsn(INVOKEVIRTUAL, hookInfoOwner, "preRun", "($hookContextType)Z", false)

                // 读取 perRun 结果
                // preRun 返回 true 则中断执行，向上返回 context 中的 result
                visitJumpInsn(IFEQ, tryStart)

                // 判断方法是否有返回值，如果有则返回 result，没有则直接返回。
                if (returnFlag) {
                    visitVarInsn(ALOAD, hookContextStack)
                    visitFieldInsn(GETFIELD, hookContextOwner, "result", "Ljava/lang/Object;")
                    makeCast(this, returnType)
                    visitInsn(getReturn(returnType))
                } else visitInsn(RETURN)
            }

            // try 部分， invoke 原方法。
            apply {
                visitLabel(tryStart)

                // 如果有返回值就将 hookContext 压入栈，等待后续写入 result
                if (returnFlag) visitVarInsn(ALOAD, hookContextStack)

                if (!isStatic) visitVarInsn(ALOAD, 0)

                // 循环遍历 Params Array，将数组内的值作为参数读出。
                params.forEachIndexed { i, it ->
                    visitVarInsn(ALOAD, paramsStack)
                    visitIntInsn(BIPUSH, i + 1)
                    visitInsn(AALOAD)
                    makeCast(this, it.type)
                }

                // invoke原方法
                visitMethodInsn(
                    if (isStatic) INVOKESTATIC else INVOKEVIRTUAL,
                    classOwner,
                    hook.changeToName,
                    hook.descriptor,
                    false
                )

                // 如果有返回值，则将返回值写入 result
                if (returnFlag) {
                    if (returnType.length == 1)
                        getTyped(returnType).let { typed ->
                            visitMethodInsn(INVOKESTATIC, typed, "valueOf", "($returnType)L$typed;", false)
                        }
                    visitFieldInsn(PUTFIELD, hookContextOwner, "result", "Ljava/lang/Object;")
                }

                // try 结束部分
                visitLabel(tryEnd)
                visitJumpInsn(GOTO, resultLabel)

            }

            // catch 部分
            apply {
                visitLabel(catchLabel)
                val errorStack = stack++
                visitVarInsn(ASTORE, errorStack)

                // 将 error 写入 context
                visitVarInsn(ALOAD, hookContextStack)
                visitVarInsn(ALOAD, errorStack)
                visitFieldInsn(PUTFIELD, hookContextOwner, "error", "Ljava/lang/Throwable;")

                // invoke onError
                visitVarInsn(ALOAD, hookInfoStack)
                visitVarInsn(ALOAD, hookContextStack)
                visitMethodInsn(INVOKEVIRTUAL, hookInfoOwner, "onError", "($hookContextType)Z", false)

                // 读取 onError 结果
                // onError 返回 true 则视为正常结束，向上返回 context 中的 result，否者向上抛出 context 中的 error。
                val throwLabel = Label()
                visitJumpInsn(IFEQ, throwLabel)

                // 判断方法是否有返回值，如果有则返回 result，没有则直接返回。
                if (returnFlag) {
                    visitVarInsn(ALOAD, hookContextStack)
                    visitFieldInsn(GETFIELD, hookContextOwner, "result", "Ljava/lang/Object;")
                    makeCast(this, returnType)
                    visitInsn(getReturn(returnType))
                } else visitInsn(RETURN)

                // throw 部分
                visitLabel(throwLabel)
                visitVarInsn(ALOAD, hookContextStack)
                visitFieldInsn(GETFIELD, hookContextOwner, "error", "Ljava/lang/Throwable;")
                visitInsn(ATHROW)
            }

            // postRun & return
            apply {
                visitLabel(resultLabel)

                // postRun
                visitVarInsn(ALOAD, hookInfoStack)
                visitVarInsn(ALOAD, hookContextStack)
                visitMethodInsn(INVOKEVIRTUAL, hookInfoOwner, "postRun", "($hookContextType)V", false)

                // return
                if (returnFlag) {
                    visitVarInsn(ALOAD, hookContextStack)
                    visitFieldInsn(GETFIELD, hookContextOwner, "result", "Ljava/lang/Object;")
                    makeCast(this, returnType)
                    visitInsn(getReturn(returnType))
                } else visitInsn(RETURN)
            }

            visitMaxs(0, 0)
            visitEnd()
        }

        newMethod.visibleAnnotations = sourceMethod.visibleAnnotations;
        sourceMethod.visibleAnnotations = null

        newMethod.invisibleAnnotations = sourceMethod.invisibleAnnotations;
        sourceMethod.invisibleAnnotations = null

        newMethod.visibleParameterAnnotations = sourceMethod.visibleParameterAnnotations;
        sourceMethod.visibleParameterAnnotations = null

        sourceMethod.name = hook.changeToName
        return newMethod
    }

}