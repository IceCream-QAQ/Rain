package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.annotation.PathVar;
import lombok.Data;
import lombok.val;
import lombok.var;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectMethodInvoker implements MethodInvoker {

    private MethodPara[] mps;
    private boolean returnFlag;

    private Object instance;
    private Method method;

    public ReflectMethodInvoker(Object instance, Method method) {

        val paras = method.getParameters();
        if (paras.length != 0) {
            val mps = new MethodPara[paras.length];

            for (int i = 0; i < paras.length; i++) {
                val para = paras[i];
                val mp = new MethodPara();
                mp.clazz = para.getType();
                val inject = para.getAnnotation(Inject.class);
                val pathVar = para.getAnnotation(PathVar.class);

                if (inject == null && pathVar == null) {
                    mp.type = 0;
                    mp.inject = para.getName();
                    continue;
                }

                if (inject != null) {
                    mp.type = 1;
                    mp.inject = inject;
                    continue;
                }

                //注释掉这里是因为目前没有其他分支不需要额外判断浪费性能，什么时候支持更多的注解再进行更多的判断。其实就是看着IDE的提示烦。
//            if (pathVar != null) {
                mp.type = 1;
                mp.inject = pathVar;
//                continue;
//            }

                mps[i] = mp;
            }

            this.mps = mps;

            val re = method.getReturnType().getName();
            if (re.equals("void")) returnFlag = false;
            else returnFlag = true;
        }

        this.instance = instance;
        this.method = method;
    }

    public static String classToClass(String clazz) {
        String result = null;

        if (clazz.length() == 1) {

            switch (clazz) {
                case "I":
                    result = "java.lang.Integer";
                    break;
                case "S":
                    result = "java.lang.Short";
                    break;
                case "J":
                    result = "java.lang.Long";
                    break;
                case "Z":
                    result = "java.lang.Boolean";
                    break;
                case "C":
                    result = "java.lang.Character";
                    break;
                case "B":
                    result = "java.lang.Byte";
                    break;
                case "F":
                    result = "java.lang.Float";
                    break;
                case "D":
                    result = "java.lang.Double";
                    break;
            }

        } else {
            String s1 = clazz.substring(0, 1);

            if (s1.equals("[")) result = clazz.replace("/", ".");
            if (s1.equals("L")) result = clazz.substring(1, clazz.length() - 1).replace("/", ".");
        }

        return result;
    }

    public ReflectMethodInvoker(Object instance, Method method, MethodNode methodNode) {

        val paras = method.getParameters();
        if (paras.length != 0) {
            val mps = new MethodPara[paras.length];

            val paraNodes = (List<LocalVariableNode>) methodNode.localVariables;

            var thisIndex = 1;
            for (val paraNode : paraNodes) {
                if (paraNode.name.equals("this"))break;
                thisIndex++;
            }

            for (int i = 0; i < paras.length; i++) {
                val para = paras[i];
                val mp = new MethodPara();
                mp.clazz = para.getType();
                val inject = para.getAnnotation(Inject.class);
                val pathVar = para.getAnnotation(PathVar.class);

                if (inject == null && pathVar == null) {
                    mp.type = 0;
                    mp.inject = paraNodes.get(i + thisIndex).name;
                }

                if (inject != null) {
                    mp.type = 1;
                    mp.inject = inject;
                }

                if (pathVar != null) {
                    mp.type = 2;
                    mp.inject = pathVar;
                }

                mps[i] = mp;
            }

            this.mps = mps;
        }

        val re = method.getReturnType().getName();
        returnFlag = !re.equals("void");

        this.instance = instance;
        this.method = method;
    }

    public Object invoke(ActionContext context) throws Exception {
        if (mps == null) {
            if (returnFlag) {
                return method.invoke(instance);
            }
            method.invoke(instance);
            return null;
        }
        val paras = new Object[mps.length];
        for (int i = 0; i < mps.length; i++) {
            val mp = mps[i];

            //觉得switch性能低下的同学可以转由if else 实现。
            Object para;
            switch (mp.type) {
                case 0:
                    para = context.injectObj(mp.clazz, (String) mp.inject);
                    break;
                case 1:
                    para = context.injectObj((Inject) mp.inject, mp.clazz);
                    break;
                case 2:
                    val pathVar = (PathVar) mp.inject;

                    val key = pathVar.value();
                    para = context.injectPathVar(mp.clazz,key,pathVar.type());
                    break;
                default:
                    para = null;
            }

            paras[i] = para;
        }
        if (returnFlag) {
            return method.invoke(instance, paras);
        }
        method.invoke(instance, paras);
        return null;
    }

    @Data
    private class MethodPara {
        private Class<?> clazz;
        private Integer type;
        private Object inject;
    }
}
