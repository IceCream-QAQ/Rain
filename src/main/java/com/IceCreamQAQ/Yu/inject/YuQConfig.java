package com.IceCreamQAQ.Yu.inject;

import lombok.val;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class YuQConfig {

    private ClassLoader appClassLoader;

    public YuQConfig(){
        appClassLoader=this.getClass().getClassLoader();
    }

    public YuQConfig(ClassLoader appClassLoader){
        this.appClassLoader=appClassLoader;
    }


    public Map<String,String> init() throws IOException {
        val in =appClassLoader.getResourceAsStream("YuQ.properties");

        val prop=new Properties();
        prop.load(in);

        val map=new ConcurrentHashMap<String,String>();
        for (Object o : prop.keySet()) {
            map.put(o.toString(),prop.get(o).toString());
        }

        return map;
    }

}
