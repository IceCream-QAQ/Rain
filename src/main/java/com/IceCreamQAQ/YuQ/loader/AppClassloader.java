package com.IceCreamQAQ.YuQ.loader;

import sun.misc.Resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.jar.Manifest;

public class AppClassloader extends URLClassLoader {

    public AppClassloader(URL[] urls,ClassLoader parent){
        super(urls,parent);
    }

//    @Override
//    public Class<?> defineClass(String name, byte[] b, int off, int len){
//        return null;
//    }
}
