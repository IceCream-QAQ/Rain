package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.annotation.PathVar;
import com.IceCreamQAQ.YuQ.loader.InvokerClassLoader;
import lombok.val;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.objectweb.asm.Opcodes.*;

public class MethodInvokerCreator {

    private static final String invokerClassName = Type.getInternalName(MethodInvoker.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(MethodInvoker.class.getDeclaredMethods()[0]);

    @Inject
    private InvokerClassLoader classLoader;

    private int index = 0;

    public MethodInvoker getInvoker(Object object, Method method, MethodNode methodNode) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        val clazz = createInvoker(method, methodNode);
        val invoker = clazz.getConstructor(method.getDeclaringClass()).newInstance(object);
        return invoker;
    }

    public Class<? extends MethodInvoker> createInvoker(Method method, MethodNode methodNode) {
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
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "("+Type.getType(method.getDeclaringClass()).getDescriptor()+")V", null, null);
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
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Lcom/IceCreamQAQ/YuQ/controller/ActionContext;)Ljava/lang/Object;", null, new String[]{"java/lang/Exception"});
            mv.visitCode();

            val paras = method.getParameters();
            val paraNodes = (List<LocalVariableNode>) methodNode.localVariables;

            val num = paraNodes.size() - paras.length;

            for (int i = 0; i < paras.length; i++) {
                val para = paras[i];
                val paraNode = paraNodes.get(num + i);

                val inject = para.getAnnotation(Inject.class);
                val pathVar = para.getAnnotation(PathVar.class);

                val label = new Label();
                mv.visitLabel(label);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn(Type.getType(para.getType()));

                if (inject == null && pathVar == null) {
                    mv.visitLdcInsn(paraNode.name);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/IceCreamQAQ/YuQ/controller/ActionContext", "injectObj", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;", false);
                }

                if (inject != null) {
                    mv.visitLdcInsn(Type.getType(inject.value()));
                    mv.visitLdcInsn(inject.name());
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/IceCreamQAQ/YuQ/controller/ActionContext", "injectInject", "(Ljava/lang/Class;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;", false);
                }

                if (pathVar != null) {
                    mv.visitIntInsn(SIPUSH, pathVar.value());
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                    mv.visitFieldInsn(GETSTATIC, "com/IceCreamQAQ/YuQ/annotation/PathVar$Type", pathVar.type().toString(), "Lcom/IceCreamQAQ/YuQ/annotation/PathVar$Type;");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/IceCreamQAQ/YuQ/controller/ActionContext", "injectPathVar", "(Ljava/lang/Class;Ljava/lang/Integer;Lcom/IceCreamQAQ/YuQ/annotation/PathVar$Type;)Ljava/lang/Object;", false);

                }
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
        return String.format("YuQ_MethodInvoker_%d_%s_%s_IceCreamQAQ_OpenSource_YuQFramework",
                index,
                callback.getDeclaringClass().getSimpleName(),
                callback.getName());
    }

}
