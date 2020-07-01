package com.IceCreamQAQ.Yu.loader.enchant;

import com.IceCreamQAQ.Yu.annotation.EnchantBy;
import com.IceCreamQAQ.Yu.loader.AppClassloader;
import lombok.SneakyThrows;
import lombok.val;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.lang.annotation.Annotation;
import java.util.List;

public class EnchantManager {

    private static AppClassloader classloader;

    public static void init(AppClassloader appClassloader) {
        classloader = appClassloader;
    }

    @SneakyThrows
    public static byte[] checkClass(byte[] bytes) {
        val reader = new ClassReader(bytes);
        val node = new ClassNode();
        reader.accept(node, 0);

        if (node.visibleAnnotations != null) {
            boolean en = false;

            for (AnnotationNode annotation : (List<AnnotationNode>) node.visibleAnnotations) {
                val annotationClassName = annotation.desc.substring(1, annotation.desc.length() - 1).replace("/", ".");
                val annotationClass = classloader.loadClass(annotationClassName, false, false);
                val aa = annotationClass.getAnnotations();
                for (Annotation a : aa) {
                    if (a instanceof EnchantBy) {
                        en = true;
                        val enchanter = ((EnchantBy) a).value().newInstance();
                        enchanter.enchantClass(node);
                    }
                }
            }
            if (en){
                ClassWriter cw = new ClassWriter(0);
                node.accept(cw);
                bytes = cw.toByteArray();
            }
        }
        return bytes;
    }


}
