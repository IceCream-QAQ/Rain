package com.IceCreamQAQ.YuQ.inject;

import lombok.val;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class YuQConfig {

    public Map<String,String> init() throws IOException {
        val in =getClass().getClassLoader().getResourceAsStream("YuQ.properties");

        if (in==null);

        val prop=new Properties();
        prop.load(in);

        val map=new ConcurrentHashMap<String,String>();
        for (Object o : prop.keySet()) {
            map.put(o.toString(),prop.get(o).toString());
        }

        return map;
    }

}
