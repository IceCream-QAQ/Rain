package com.IceCreamQAQ.YuQ.inject;

import com.IceCreamQAQ.YuQ.annotation.AutoBind;
import com.IceCreamQAQ.YuQ.annotation.Config;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import lombok.val;
import lombok.var;

import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class YuQInject extends YuQInjectBase {

    private Map<String, String> configs;

    public YuQInject() throws IOException {
        configs = new YuQConfig().init();
    }

    public YuQInject(ClassLoader appClassLoader) throws IOException {
        configs = new YuQConfig(appClassLoader).init();
    }

    public void putInjectObj(String clazz, String name, Object instance) {
        if (name == null) name = "";

        var list = injectObjects.get(clazz);
        if (list == null) {
            list = new ConcurrentHashMap<String, Object>();
            injectObjects.put(clazz, list);
        }
        list.put(name, instance);

        if (clazz.equals(instance.getClass().getName())) {
            val type = instance.getClass();
            val autoBind = type.getAnnotation(AutoBind.class);
            if (autoBind != null) {
                var bindType = autoBind.value().getName();
                if (bindType.equals("com.IceCreamQAQ.YuQ.annotation.AutoBind") || bindType.equals("com.icecreamqaq.yuq.annotation.AutoBind")) {
                    val listInterface = type.getInterfaces();
                    for (Class<?> aClass : listInterface) {
                        putInjectObj(aClass.getName(), name, instance);
                    }
                } else {
                    putInjectObj(bindType, name, instance);
                }
            }
        }
    }

    public <T> T spawnInstance(Class<T> clazz) {
        try {
            val obj = createInstance(clazz);

            injectObject(obj);

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> T createInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        val constructorNum = clazz.getConstructors().length;
        if (constructorNum < 1) return null;
        val constructor = clazz.getConstructors()[0];

        val paras = constructor.getParameters();

        if (paras.length == 0) {
            return clazz.newInstance();
        }

        val objs = new Object[paras.length];
        for (int i = 0; i < paras.length; i++) {
            val para = paras[i];
            val inject = para.getAnnotation(Inject.class);
            if (inject == null) return null;

            objs[i] = getObj(inject, para.getType());
        }

        return (T) constructor.newInstance(objs);
    }

    public void injectObject(Object obj) throws ClassNotFoundException, IllegalAccessException {
        Class clazz = obj.getClass();

        val fields = new ArrayList<Field>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        for (Field field : fields) {
            val inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                var injectType = inject.value().getName();
                if (injectType.equals("com.IceCreamQAQ.YuQ.annotation.Inject") || injectType.equals("com.icecreamqaq.yuq.annotation.Inject"))
                    injectType = field.getType().getName();

                field.setAccessible(true);
                field.set(obj, getObjByName(injectType, inject.name()));

                continue;
            }

            val config = field.getAnnotation(Config.class);
            if (config != null) {
                val name = config.value();
                val value = configs.get(name);
                if (value == null) {
                    field.setAccessible(true);
                    field.set(obj, config.defaultValue());
                } else {
                    field.setAccessible(true);
                    field.set(obj, value);
                }

                continue;
            }

            val injectJsr = field.getAnnotation(javax.inject.Inject.class);
            if (injectJsr != null) {
                var name = "";
                val named = field.getAnnotation(Named.class);
                if (named != null) name = named.value();

                field.setAccessible(true);
                field.set(obj, getObjByName(field.getType().getName(), name));
            }
        }
    }

    public Object getObjByName(String injectType, String name) throws ClassNotFoundException {
        var list = injectObjects.get(injectType);
        if (list == null) {
            if (injectType.contains("com.IceCreamQAQ.YuQ") || injectType.contains("com.icecreamqaq.yuq")) {
                val paraType = Class.forName(injectType);
                if (!paraType.isInterface() && !Modifier.isAbstract(paraType.getModifiers())) {
                    spawnAndPut(paraType);
                    list = injectObjects.get(injectType);
                }
            }
        }
        if (list == null) {
            return null;
        }
        return list.get(name);
    }

    public <T> T spawnAndPut(Class<T> clazz, String name) {
        try {
            if (name == null) name = "";
            val obj = createInstance(clazz);
            putInjectObj(clazz.getName(), name, obj);

            injectObject(obj);

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T spawnAndPut(Class<T> clazz) {
        try {
            val named = clazz.getAnnotation(Named.class);
            var name = "";
            if (named != null) name = named.value();

            val obj = createInstance(clazz);
            if (obj == null) return null;
            putInjectObj(clazz.getName(), name, obj);

            injectObject(obj);

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getConfig(@NotNull String key) {
        return configs.get(key);
    }

}
