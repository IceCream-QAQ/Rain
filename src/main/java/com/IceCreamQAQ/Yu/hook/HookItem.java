package com.IceCreamQAQ.Yu.hook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class HookItem {

    private String ClassName;
    private String methodName;
    private String runnable;

    public HookItem() {
    }

    public HookItem(String className, String methodName, String runnable) {
        ClassName = className;
        this.methodName = methodName;
        this.runnable = runnable;
    }
}
