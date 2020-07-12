package com.IceCreamQAQ.Yu.loader.enchant;

import com.IceCreamQAQ.Yu.annotation.NotSearch;
import lombok.val;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NotSearch
public class MethodParaNamedEnchanter implements Enchanter {

    @Override
    public void enchantClass(ClassNode cn) {
        for (MethodNode method : (List<MethodNode>) cn.methods) {
            if (method.name.equals("<init>")) continue;

            if (method.localVariables.size() < 2) continue;

            val paraName = new ArrayList<String>();

            LocalVariableNode thisNode = null;

            for (LocalVariableNode localVariable : (List<LocalVariableNode>) method.localVariables) {
                if (localVariable.name.equals("this")) {
                    thisNode = localVariable;
                    break;
                }
            }

            if (thisNode == null) return;

            for (LocalVariableNode localVariable : (List<LocalVariableNode>) method.localVariables) {
                if (localVariable.name.equals("this")) continue;

                if (thisNode.start == localVariable.start && thisNode.end == localVariable.end)
                    paraName.add(localVariable.name);
            }

            List<AnnotationNode>[] pas = method.visibleParameterAnnotations;
            if (pas == null) pas = new List[paraName.size()];
            method.visibleParameterAnnotations = pas;

            for (int i = 0; i < pas.length; i++) {
                val ans = pas[i];

                if (ans == null) {
                    val na = new ArrayList<AnnotationNode>();
                    pas[i] = na;

                    val an = new AnnotationNode("Ljavax/inject/Named;");
                    val values = new ArrayList();

                    values.add("value");
                    values.add(paraName.get(i));

                    an.values = values;

                    na.add(an);
                } else {
                    boolean haveNamed = false;
                    for (val an : ans) {
                        if (!an.desc.equals("Ljavax/inject/Named;")) continue;
                        haveNamed = true;
                        break;
                    }

                    if (haveNamed) continue;

                    val an = new AnnotationNode("Ljavax/inject/Named;");
                    val values = new ArrayList();
                    values.add("value");
                    values.add(paraName.get(i));
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
