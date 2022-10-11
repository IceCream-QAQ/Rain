package com.IceCreamQAQ.Yu.loader.enchant;

import com.IceCreamQAQ.Yu.annotation.EnchantBy;
import com.IceCreamQAQ.Yu.loader.AppClassloader;
import com.IceCreamQAQ.Yu.loader.IRainClassLoader;
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer;
import lombok.SneakyThrows;
import lombok.val;
import org.ehcache.core.EhcacheManager;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.lang.annotation.Annotation;
import java.util.List;

public class EnchantManager implements ClassTransformer {

    private final IRainClassLoader classloader;

    public EnchantManager(IRainClassLoader classLoader){
        this.classloader = classLoader;
    }

    @Override
    @SneakyThrows
    public boolean transform(@NotNull ClassNode node, @NotNull String className) {
        if (node.visibleAnnotations != null) {
            boolean en = false;

            for (AnnotationNode annotation : node.visibleAnnotations) {
                val annotationClassName = annotation.desc.substring(1, annotation.desc.length() - 1).replace("/", ".");
                val annotationClass = classloader.forName(annotationClassName, false);
                val aa = annotationClass.getAnnotations();
                for (Annotation a : aa) {
                    if (a instanceof EnchantBy) {
                        en = true;
                        val enchanter = ((EnchantBy) a).value().newInstance();
                        enchanter.enchantClass(node);
                    }
                }
            }
            return en;
        }
        return false;
    }
}
