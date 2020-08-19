package com.IceCreamQAQ.Yu.loader;

import javax.inject.Inject;
import javax.inject.Named;

public class SpawnClassLoader extends ClassLoader {

    private Long time;

    @Inject
    public SpawnClassLoader(@Named("appClassLoader") ClassLoader appClassLoader) {
        super(appClassLoader);
    }

    public Class<?> define(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }


    @Override
    public String toString() {
        return "InvokerClassLoader " + time;
    }
}
