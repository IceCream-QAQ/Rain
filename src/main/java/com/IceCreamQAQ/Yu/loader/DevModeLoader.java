package com.IceCreamQAQ.Yu.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class DevModeLoader extends URLClassLoader {


    public DevModeLoader(URL[] urls) {
        super(urls);
    }

    public DevModeLoader(URL[] urls,ClassLoader p) {
        super(urls,p);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
