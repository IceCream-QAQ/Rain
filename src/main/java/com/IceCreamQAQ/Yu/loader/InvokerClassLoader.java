package com.IceCreamQAQ.Yu.loader;

import com.IceCreamQAQ.Yu.annotation.Inject;

public class InvokerClassLoader extends ClassLoader {

    private Long time;

    public InvokerClassLoader(@Inject ClassLoader classLoader) {
        super(InvokerClassLoader.class.getClassLoader());
    }

    public Class<?> define(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }


    @Override
    public String toString() {
        return "InvokerClassLoader " + time;
    }
}
