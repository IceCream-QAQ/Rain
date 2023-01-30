package com.IceCreamQAQ.Yu.hook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class HookItem {

    private String type;

    private String basedClass;
    private String basedMethod;
    private String basedDesc;

    private String runnable;


    public HookItem(String className, String methodName, String runnable) {
        this.type = "old";
        this.basedClass = className;
        this.basedMethod = methodName;
        this.runnable = runnable;
    }

    private HookItem(String type, String basedClass, String basedMethod, String basedDesc, String runnable) {
        this.type = type;
        this.basedClass = basedClass;
        this.basedMethod = basedMethod;
        this.basedDesc = basedDesc;
        this.runnable = runnable;
    }

    public static HookItem hookMethod(String className, String methodName, String descriptor, String runnable) {
        return new HookItem("method", className, methodName, descriptor, runnable);
    }

    public static HookItem HookInterface(String className, String methodName, String descriptor, String runnable) {
        return new HookItem("interface", className, methodName, descriptor, runnable);
    }


}
