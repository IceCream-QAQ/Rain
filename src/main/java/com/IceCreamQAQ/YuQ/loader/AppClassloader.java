package com.IceCreamQAQ.YuQ.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class AppClassloader extends URLClassLoader {

    public AppClassloader(URL[] urls,ClassLoader parent){
        super(urls,parent);
    }


}
