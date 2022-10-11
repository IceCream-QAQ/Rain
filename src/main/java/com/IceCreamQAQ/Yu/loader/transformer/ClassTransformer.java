package com.IceCreamQAQ.Yu.loader.transformer;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {

    boolean transform(@NotNull ClassNode node,@NotNull String className);

}
