package com.IceCreamQAQ.Yu.loader;

import com.IceCreamQAQ.Yu.hook.HookItem;
import com.IceCreamQAQ.Yu.hook.YuHook;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class AppClassloader extends ClassLoader {

    public AppClassloader(ClassLoader parent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        super(parent);
        YuHook.init(this);
    }

    @SneakyThrows
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c != null) {
            return c;
        }

        if (isBlackListClass(name)) c = this.getParent().loadClass(name);

        if (c == null) c = loadAppClass(name);

        if (resolve) {
            this.resolveClass(c);
        }

        if (null == c) {
            return super.loadClass(name, resolve);
        } else {
            return c;
        }
    }

    private Class<?> loadAppClass(String name) throws IOException {
        val in = this.getParent().getResourceAsStream(name.replace(".", "/") + ".class");
        var bytes = new byte[in.available()];
        in.read(bytes);

        val hooks = YuHook.getHooks();

        val newMethod = new HashMap<String, MethodNode>();

//        if (name.equals("com.IceCreamQAQ.Yu.TestA")) {
//            ClassReader reader = new ClassReader(bytes);
//            ClassNode node = new ClassNode();
//            reader.accept(node, 0);
//
//            for (MethodNode method : (List<MethodNode>) node.methods) {
//                if (method.name.equals("a")) {
//                    newMethod.put(method.name, method);
//                    method.name = "a_IceCreamQAQ_YuHook";
//                    method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/IceCreamQAQ/Yu/TestC", "o", "()V"));
//                }
//            }
//
//            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//            node.accept(cw);
//
//            for (String mn : newMethod.keySet()) {
//                val method = newMethod.get(mn);
//                val mv = cw.visitMethod(Opcodes.ACC_PUBLIC, mn, method.desc, method.signature, (String[]) method.exceptions.toArray(new String[method.exceptions.size()]));
//                mv.visitCode();
//                mv.visitVarInsn(ALOAD, 0);
//                mv.visitMethodInsn(INVOKEVIRTUAL, name.replace(".", "/"), mn + "_IceCreamQAQ_YuHook", method.desc, false);
//                mv.visitInsn(RETURN);
//                mv.visitMaxs(0, 0);
//                mv.visitEnd();
//            }
//
////            val mv = cw.visitMethod(Opcodes.ACC_PUBLIC,"a","()V",null,null);
////            mv.visitCode();
////            mv.visitVarInsn(ALOAD, 0);
////            mv.visitMethodInsn(INVOKEVIRTUAL, "com/IceCreamQAQ/Yu/TestA", "a_IceCreamQAQ_Yu_Hook", "()V", false);
////            mv.visitInsn(RETURN);
////            mv.visitMaxs(0,0);
////            mv.visitEnd();
//
//
//            //System.out.println(writer.toByteArray());
//            bytes = cw.toByteArray();
//        }

        bytes = YuHook.checkClass(name, bytes);

        return defineClass(name, bytes, 0, bytes.length);
    }

    public static boolean isBlackListClass(String name) {
        return name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("com.google.")
                || name.startsWith("org.apache.")
                || name.startsWith("sun.")
                || name.startsWith("com.sun.")
                || name.startsWith("com.IceCreamQAQ.Yu.hook")
                ;
    }
}
