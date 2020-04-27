package com.IceCreamQAQ.Yu.loader.enchant;

import lombok.val;
import lombok.var;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodParaNamedEnchanter implements Enchanter {

    @Override
    public void enchantClass(ClassNode cn) {
        for (MethodNode method : (List<MethodNode>) cn.methods) {
            if (method.name.equals("<init>")) continue;

            if (method.visibleParameterAnnotations == null) continue;

            var paraIndex = 0;
            val paraName = new String[method.visibleParameterAnnotations.length];

            for (LocalVariableNode localVariable : (List<LocalVariableNode>) method.localVariables) {
                if (localVariable.name.equals("this")) continue;

                paraName[paraIndex] = localVariable.name;
                paraIndex++;

                if (paraIndex > paraName.length) break;
            }

            List<AnnotationNode>[] pas = method.visibleParameterAnnotations;

            for (int i = 0; i < pas.length; i++) {
                val ans = pas[i];

                if (ans == null) {
                    val na = new ArrayList<AnnotationNode>();
                    pas[i] = na;

                    val an = new AnnotationNode("Ljavax/inject/Named;");
                    val values = new ArrayList<String>();

                    values.add("value");
                    values.add(paraName[i]);

                    an.values = values;

                    na.add(an);
                } else {
                    var haveNamed = false;
                    for (val an : ans) {
                        if (!an.desc.equals("Ljavax/inject/Named;")) continue;
                        haveNamed = true;
                        break;
                    }

                    if (haveNamed) continue;

                    val an = new AnnotationNode("Ljavax/inject/Named;");
                    val values = new ArrayList<String>();
                    values.add("value");
                    values.add(paraName[i]);
                    an.values = values;
                    ans.add(an);
                }
            }
        }
    }

    @Override
    public void enchantMethod(ClassNode cn, MethodNode mn) {

    }
}
