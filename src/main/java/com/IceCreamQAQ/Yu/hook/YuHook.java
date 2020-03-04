package com.IceCreamQAQ.Yu.hook;

import com.IceCreamQAQ.Yu.loader.AppClassloader;
import lombok.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class YuHook {

    private static List<HookItem> hooks = new ArrayList();

    public static void put(HookItem item) {
        hooks.add(item);
    }

    public static List<HookItem> getHooks() {
        return hooks;
    }

    public static HookRunnable[] r;

    public static void init(AppClassloader classloader) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        r = new HookRunnable[hooks.size()];
        for (int i = 0; i < hooks.size(); i++) {
            val hookItem = hooks.get(i);
            r[i] = (HookRunnable) classloader.loadClass(hookItem.getRunnable()).newInstance();
        }
    }

    public static byte[] checkClass(String name, byte[] bytes) {
        val need = new HashMap<Integer, HookItem>();

        for (int i = 0; i < hooks.size(); i++) {
            val hookItem = hooks.get(i);

            if (name.equals(hookItem.getClassName())) need.put(i, hookItem);
        }

        if (need.size() == 0) return bytes;

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);


        val newMethods = new HashMap<String, MethodNode>();

        for (val method : (List<MethodNode>) node.methods) {
            if (isSysMethod(method.name)) continue;
            val runnables = new ArrayList<Integer>();

            for (Integer id : need.keySet()) {
                val hookItem = need.get(id);
                if (method.name.equals(hookItem.getMethodName())) runnables.add(id);
            }

            if (runnables.size() == 0) continue;

            newMethods.put(method.name, method);
            method.name += "_IceCreamQAQ_YuHook";
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(cw);

        val cName = name.replace(".", "/");


        for (String mn : newMethods.keySet()) {
            val method = newMethods.get(mn);
            val returnType = method.desc;


            val mv = cw.visitMethod(method.access, mn, method.desc, method.signature, new String[]{"java/lang/Throwable"});
//            mv.visitCode();
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitMethodInsn(INVOKEVIRTUAL, name.replace(".", "/"), mn + "_IceCreamQAQ_YuHook", method.desc, false);
//            mv.visitInsn(RETURN);
//            mv.visitMaxs(0, 0);
//            mv.visitEnd();

//            val mv = cw.visitMethod(ACC_PUBLIC, mn, "()Ljava/lang/Object;", null, new String[]{"java/lang/Throwable"});
            mv.visitCode();
            Label tryStart = new Label();
            Label tryEnd = new Label();
            Label catchLabel = new Label();
            mv.visitTryCatchBlock(tryStart, tryEnd, catchLabel, "java/lang/Throwable");


            val maxPara = method.localVariables.size();
            int stack = maxPara - 1;
            int hookMethodStack;
            int paraStack;
            {// HookMethod
                val newHookMethodLabel = new Label();
                mv.visitLabel(newHookMethodLabel);
                mv.visitTypeInsn(NEW, "com/IceCreamQAQ/Yu/hook/HookMethod");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "com/IceCreamQAQ/Yu/hook/HookMethod", "<init>", "()V", false);
                mv.visitVarInsn(ASTORE, ++stack);

                hookMethodStack = stack;

                val setClassNameLabel = new Label();
                mv.visitLabel(setClassNameLabel);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitLdcInsn(name);
                mv.visitFieldInsn(PUTFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "className", "Ljava/lang/String;");

                val setMethodNameLabel = new Label();
                mv.visitLabel(setMethodNameLabel);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitLdcInsn(mn);
                mv.visitFieldInsn(PUTFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "methodName", "Ljava/lang/String;");

//                mv.visitVarInsn(ICONST_0,1);
//                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
//                mv.visitVarInsn(ASTORE, 3);

                {// Paras

                    val newParasLabel = new Label();
                    mv.visitLabel(newParasLabel);
                    mv.visitIntInsn(SIPUSH, maxPara);
                    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                    mv.visitVarInsn(ASTORE, ++stack);

                    paraStack = stack;

                    for (int i = 0; i < maxPara; i++) {
                        mv.visitVarInsn(ALOAD, paraStack);
                        mv.visitIntInsn(SIPUSH, i);
                        mv.visitVarInsn(ALOAD, i);
                        mv.visitInsn(AASTORE);
                    }


                    val setParasLabel = new Label();
                    mv.visitLabel(setParasLabel);
                    mv.visitVarInsn(ALOAD, hookMethodStack);
//                    mv.visitIntInsn(SIPUSH, 0);
                    mv.visitVarInsn(ALOAD, paraStack);
                    mv.visitFieldInsn(PUTFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "paras", "[Ljava/lang/Object;");

//                    Label label9 = new Label();
//                    mv.visitLabel(label9);
//                    mv.visitLineNumber(23, label9);
//                    mv.visitVarInsn(ALOAD, 2);
//                    mv.visitVarInsn(ALOAD, 3);
//                    mv.visitFieldInsn(PUTFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "paras", "[Ljava/lang/Object;");
                }
            }
            int hookRunnableStack;
            {// HookRunnable

                val getHookRunnableLabel = new Label();
                mv.visitLabel(getHookRunnableLabel);
                mv.visitFieldInsn(GETSTATIC, "com/IceCreamQAQ/Yu/hook/YuHook", "r", "[Lcom/IceCreamQAQ/Yu/hook/HookRunnable;");
                mv.visitInsn(ICONST_0);
                mv.visitInsn(AALOAD);
                mv.visitVarInsn(ASTORE, ++stack);

                hookRunnableStack = stack;
            }
            {// preRun

                val preRunLabel = new Label();
                mv.visitLabel(preRunLabel);
                mv.visitVarInsn(ALOAD, hookRunnableStack);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitMethodInsn(INVOKEINTERFACE, "com/IceCreamQAQ/Yu/hook/HookRunnable", "preRun", "(Lcom/IceCreamQAQ/Yu/hook/HookMethod;)Z", true);
                mv.visitJumpInsn(IFEQ, tryStart);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitFieldInsn(GETFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "result", "Ljava/lang/Object;");
//                mv.visitTypeInsn(CHECKCAST, "java/lang/String");
                mv.visitInsn(ARETURN);
            }
            {// try
                mv.visitLabel(tryStart);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitVarInsn(ALOAD, 0);

                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(AALOAD);
                mv.visitTypeInsn(CHECKCAST, "java/lang/String");

//                mv.visitVarInsn(ALOAD, 5);
//                mv.visitInsn(ICONST_2);
//                mv.visitInsn(AALOAD);
//                mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
//                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
//
//                mv.visitVarInsn(ALOAD, 5);
//                mv.visitInsn(ICONST_3);
//                mv.visitInsn(AALOAD);
//                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
//                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);

                mv.visitMethodInsn(INVOKEVIRTUAL, cName, mn + "_IceCreamQAQ_YuHook", method.desc, false);
                mv.visitFieldInsn(PUTFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "result", "Ljava/lang/Object;");

                val postRunLabel = new Label();
                mv.visitLabel(postRunLabel);
                mv.visitVarInsn(ALOAD, hookRunnableStack);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitMethodInsn(INVOKEINTERFACE, "com/IceCreamQAQ/Yu/hook/HookRunnable", "postRun", "(Lcom/IceCreamQAQ/Yu/hook/HookMethod;)V", true);

                val returnLabel = new Label();
                mv.visitLabel(returnLabel);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitFieldInsn(GETFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "result", "Ljava/lang/Object;");
                mv.visitLabel(tryEnd);
                mv.visitInsn(ARETURN);
            }
            {// catch
                mv.visitLabel(catchLabel);
                mv.visitVarInsn(ASTORE, ++stack);

                val errorStack = stack;


                val setErrorLabel = new Label();
                mv.visitLabel(setErrorLabel);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitVarInsn(ALOAD, errorStack);
                mv.visitFieldInsn(PUTFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "error", "Ljava/lang/Throwable;");


                val onErrorLabel = new Label();
                mv.visitLabel(onErrorLabel);
                mv.visitVarInsn(ALOAD, hookRunnableStack);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitMethodInsn(INVOKEINTERFACE, "com/IceCreamQAQ/Yu/hook/HookRunnable", "onError", "(Lcom/IceCreamQAQ/Yu/hook/HookMethod;)Z", true);


                val throwLabel = new Label();
                val returnLabel = new Label();
                mv.visitLabel(returnLabel);
                mv.visitJumpInsn(IFEQ, throwLabel);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitFieldInsn(GETFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "result", "Ljava/lang/Object;");
                mv.visitInsn(ARETURN);


                mv.visitLabel(throwLabel);
                mv.visitVarInsn(ALOAD, hookMethodStack);
                mv.visitFieldInsn(GETFIELD, "com/IceCreamQAQ/Yu/hook/HookMethod", "error", "Ljava/lang/Throwable;");
                mv.visitInsn(ATHROW);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        val bs = cw.toByteArray();
        try {
            val o = new FileOutputStream(new File("/Users/icecream/1.class"));
            o.write(bs);
            o.flush();
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class MethodPara {
        private String name;
        private String castType;
        private String type;
        private boolean basicType;
    }

    static List<MethodPara> getMethodPara(MethodNode method) {
        val desc = method.desc;
        val localVariables = method.localVariables;

        val paraStr = desc.substring(1).split("\\)")[0];
        val paraChars = paraStr.toCharArray();

        val paras = new ArrayList<MethodPara>();
        read(paraChars, 0, paras);
        return paras;
    }

    static void read(char[] chars, int i, List<MethodPara> paras) {
        val c = chars[i];
        var sz = false;
        if (c == '[') {
            i++;
            sz = true;
        }
        readL(chars, i);
    }
    static String readNext(char[] chars, int i){
//        val c = chars[i];
//        val sz = false;
//        if (c == '[') {
//            i++;
//            sz = true;
//        }else {
//            if ()
//        }
        val s = readNext(chars,i);
        return s;
    }

    static String readL(char[] chars, int i) {

        return "";
    }

    static boolean isSysMethod(String name) {
        return name.equals("<init>")
                || name.equals("<clinit>")
                ;
    }
}
