package com.IceCreamQAQ.Yu.hook;

import java.util.HashMap;
import java.util.Map;

public class HookContext {

    public HookInfo info;

    private Map<String,Object> saves;

    public Object[] params;
    public Object result;
    public Throwable error;

    public void saveInfo(String key,Object value){
        if (saves == null) saves = new HashMap<>();
        saves.put(key, value);
    }

    public Object getInfo(String key){
        if (saves == null) return null;
        return saves.get(key);
    }

    public Object delInfo(String key){
        if (saves == null) return null;
        return saves.remove(key);
    }

}
