package com.IceCreamQAQ.Yu.loader.enchant;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface Enchanter {

    void enchantClass(ClassNode cn);
    void enchantMethod(ClassNode cn, MethodNode mn);

}
