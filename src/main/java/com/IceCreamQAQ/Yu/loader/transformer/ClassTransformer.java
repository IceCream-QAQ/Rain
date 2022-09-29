package com.IceCreamQAQ.Yu.loader.transformer;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {

    boolean transform(ClassNode node, String className);

}
