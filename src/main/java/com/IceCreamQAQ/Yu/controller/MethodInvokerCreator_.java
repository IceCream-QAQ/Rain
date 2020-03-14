package com.IceCreamQAQ.Yu.controller;

import com.IceCreamQAQ.Yu.loader.InvokerClassLoader;
import com.IceCreamQAQ.Yu.controller.router.MethodInvoker;
import lombok.val;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class MethodInvokerCreator_ {

    private static final String invokerClassName = Type.getInternalName(MethodInvoker.class);
    private static final String handlerFuncDesc = Type.getMethodDescriptor(MethodInvoker.class.getDeclaredMethods()[0]);

    @Inject
    private InvokerClassLoader classLoader;

    private int index = 0;

    public MethodInvoker getInvoker(Object object, Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        val clazz = createInvoker(method);
        val invoker = clazz.getConstructor(method.getDeclaringClass()).newInstance(object);
        return invoker;
    }

    public Class<? extends MethodInvoker> createInvoker(Method method) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MethodVisitor mv;

        String name = getUniqueName(method);
        String desc = name.replace('.', '/');

        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", new String[]{invokerClassName});

        cw.visitSource(".dynamic", null);
        {
            cw.visitField(ACC_PUBLIC, "instance", Type.getType(method.getDeclaringClass()).getDescriptor(), null, null).visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + Type.getType(method.getDeclaringClass()).getDescriptor() + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, desc, "instance", Type.getType(method.getDeclaringClass()).getDescriptor());
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", handlerFuncDesc, null, new String[]{"java/lang/Exception"});
            mv.visitCode();

            val paras = method.getParameters();

            for (int i = 0; i < paras.length; i++) {
                val para = paras[i];

//                val inject = para.getAnnotation(Inject.class);
//                val pathVar = para.getAnnotation(PathVar.class);

                val named = para.getAnnotation(Named.class);
//                if (name == )

                val label = new Label();
                mv.visitLabel(label);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn(Type.getType(para.getType()));

//                if (inject == null && pathVar == null) {
//                    mv.visitLdcInsn(paraNode.name);
//                    mv.visitMethodInsn(INVOKEVIRTUAL, ActionContextBase.class.getName().replace(".","/"), "injectObj", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;", false);
//                }

                if (named != null) {
//                    mv.visitLdcInsn(Type.getType(named.value()));
                    mv.visitLdcInsn(named.value());
                    mv.visitMethodInsn(INVOKEVIRTUAL, ActionContext.class.getName().replace(".", "/"), "get", "(Ljava/lang/String;)Ljava/lang/Object;", false);
                }

//                if (pathVar != null) {
//                    mv.visitIntInsn(SIPUSH, pathVar.value());
//                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//                    mv.visitFieldInsn(GETSTATIC, "com/IceCreamQAQ/Yu/annotation/PathVar$Type", pathVar.type().toString(), "Lcom/IceCreamQAQ/Yu/annotation/PathVar$Type;");
//                    mv.visitMethodInsn(INVOKEVIRTUAL, ActionContextBase.class.getName().replace(".","/"), "injectPathVar", "(Ljava/lang/Class;Ljava/lang/Integer;Lcom/IceCreamQAQ/Yu/annotation/PathVar$Type;)Ljava/lang/Object;", false);
//
//                }
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(para.getType()));
                mv.visitVarInsn(ASTORE, i + 2);
            }

            Label label = new Label();
            mv.visitLabel(label);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, desc, "instance", Type.getDescriptor(method.getDeclaringClass()));
            for (int i = 2; i < 2 + paras.length; i++) {
                mv.visitVarInsn(ALOAD, i);
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), false);


            if (method.getReturnType().getName().equals("void")) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            } else {
                mv.visitInsn(ARETURN);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        val ret = classLoader.define(getUniqueName(method), cw.toByteArray());

        return (Class<? extends MethodInvoker>) ret;
    }

    private String getUniqueName(Method callback) {
        return String.format("Yu_MethodInvoker_%d_%s_%s_IceCreamQAQ_OpenSource_YuQFramework",
                index,
                callback.getDeclaringClass().getSimpleName(),
                callback.getName());
    }

}
