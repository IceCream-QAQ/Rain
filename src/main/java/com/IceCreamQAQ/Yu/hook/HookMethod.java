package com.IceCreamQAQ.Yu.hook;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class HookMethod {

    public String className;
    public String methodName;

    public Object[] paras;
    public Object result;
    public Throwable error;

}
