package com.IceCreamQAQ.YuQ.loader;

import com.IceCreamQAQ.YuQ.annotation.Inject;

public class InvokerClassLoader extends ClassLoader {

    private Long time;

    public InvokerClassLoader(@Inject ClassLoader classLoader) {
        super(classLoader);
        time = System.currentTimeMillis();
    }

    public Class<?> define(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }


    @Override
    public String toString() {
        return "InvokerClassLoader " + time;
    }
}
