package com.IceCreamQAQ.Yu.loader;

import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer;
import com.IceCreamQAQ.Yu.util.IO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AppClassloader extends ClassLoader implements IRainClassLoader {

    private final List<ClassTransformer> transformers = new ArrayList<>();
    private static File classOutLocation;

    static {
        classOutLocation = new File(IO.getTmpLocation(), "classOutput");
        if (classOutLocation.exists()) classOutLocation.delete();
        classOutLocation.mkdirs();
    }

    public AppClassloader(ClassLoader parent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        super(parent);

        for (String s : transformerList) {
            transformers.add((ClassTransformer) loadClass(s, true, false).newInstance());
        }
    }

    private static final List<String> blackList = new ArrayList<>();
    private static final List<String> transformerList = new ArrayList<>();

    public static void registerBackList(List<String> packageName) {
        blackList.addAll(packageName);
    }

    public static void registerTransformerList(String className) {
        transformerList.add(className);
    }

    public void registerTransformer(String className) throws IllegalAccessException, InstantiationException {
        transformers.add((ClassTransformer) loadClass(className, true, false).newInstance());
    }

    public void registerTransformer(ClassTransformer transformer) throws IllegalAccessException, InstantiationException {
        transformers.add(transformer);
    }

    @SneakyThrows
    public Class<?> loadClass(String name, boolean resolve) {
        return loadClass(name, resolve, true);
    }

    @SneakyThrows
    public Class<?> loadClass(String name, boolean resolve, boolean enhance) {
        Class<?> c = findLoadedClass(name);
        if (c != null) {
            return c;
        }

        if (isBlackListClass(name)) c = this.getParent().loadClass(name);

        try {
            if (c == null) if (enhance) c = loadAppClass(name, resolve);
        } catch (ClassNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("加载类: " + name + " 出错！", e);
        }

        if (null == c) c = super.loadClass(name, resolve);

        val pkgName = name.substring(0, name.lastIndexOf("."));
//        if (getParent())
        if (getPackage(pkgName) == null) {
            try {
                definePackage(pkgName, null, null, null, null, null, null, null);
            } catch (IllegalArgumentException iae) {
                throw new AssertionError("Cannot find package " +
                        pkgName);
            }
        }
        return c;
    }

    private Class<?> loadAppClass(String name, boolean resolve) throws IOException, ClassNotFoundException {
        log.trace(String.format("Load Class: %s.", name));

        val path = name.replace(".", "/") + ".class";

        val in = this.getParent().getResourceAsStream(path);
        if (in == null) throw new ClassNotFoundException(name);

        var changed = false;

        var bytes = IO.read(in, true);
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);


        for (ClassTransformer transformer : transformers) {
            if (transformer.transform(node, name)) changed = true;
        }

        if (changed) {
            ClassWriter ncw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(ncw);

            bytes = ncw.toByteArray();
            IO.writeFile(new File(classOutLocation, name + ".class"), bytes);
        }

        val c = defineClass(name, bytes, 0, bytes.length);
        if (resolve) resolveClass(c);
        return c;
    }

    @NotNull
    public Class<?> define(@NotNull String name, @NotNull byte[] data) {
        return defineClass(name, data, 0, data.length);
    }

    @Override
    protected Package getPackage(String name) {
        return super.getPackage(name);
    }

    public boolean isBlackListClass(String name) {
        val b = name.startsWith("java.")
                || name.startsWith("jdk.")
                || name.startsWith("javax.")
                || name.startsWith("kotlin")
                || name.startsWith("com.google.")
                || name.startsWith("org.apache.")
                || name.startsWith("org.w3c.")
                || name.startsWith("sun.")
                || name.startsWith("com.sun.")
                || name.startsWith("net.sf.ehcache")
                || name.startsWith("com.IceCreamQAQ.Yu.annotation.")
                || name.startsWith("com.IceCreamQAQ.Yu.hook.")
                || name.startsWith("com.IceCreamQAQ.Yu.loader.enchant.")
                || name.startsWith("com.IceCreamQAQ.Yu.loader.AppClassloader")
                || name.startsWith("ch.qos.logback.core.")
                || name.startsWith("org.xml")
                || name.startsWith("org.slf4j.")
//                || name.startsWith("org.hibernate")
                || name.startsWith("org.jboss");
        if (b) return true;
        for (String s : blackList) {
            if (name.startsWith(s)) return true;
        }
        return false;
    }

    @SneakyThrows
    @NotNull
    @Override
    public Class<?> forName(@NotNull String name, boolean initialize) {
        return loadClass(name, initialize);
    }
}
