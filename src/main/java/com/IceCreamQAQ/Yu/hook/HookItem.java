package com.IceCreamQAQ.Yu.hook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HookItem {

    private String ClassName;
    private String methodName;
    private String runnable;

}
