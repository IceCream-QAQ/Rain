package com.IceCreamQAQ.Yu.util;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public interface YuEnhancer {

    void visitClass(ClassNode classNode);
    void visitMethod(ClassNode classNode,MethodNode methodNode);
    void visitField(ClassNode classNode, FieldNode fieldNode);

}
