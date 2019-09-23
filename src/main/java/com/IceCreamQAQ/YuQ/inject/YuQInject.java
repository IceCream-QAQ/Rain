package com.IceCreamQAQ.YuQ.inject;

import com.IceCreamQAQ.YuQ.annotation.AutoBind;
import com.IceCreamQAQ.YuQ.annotation.Config;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import lombok.val;
import lombok.var;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YuQInject extends YuQInjectBase {

    private Map<String, String> configs;

    public YuQInject() throws IOException {
        configs = new YuQConfig().init();
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

    public <T> T spawnInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        val obj = clazz.newInstance();

        injectObject(obj);

        return obj;
    }

    public void injectObject(Object obj) throws ClassNotFoundException, IllegalAccessException {
        Class clazz = obj.getClass();

        val fields = new ArrayList<Field>();
//        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
//
//        Class superClass= clazz.getSuperclass();
        while (clazz!=null){
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        for (Field field : fields) {
            val inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                var injectType = inject.value().getName();

                if (injectType.equals("com.IceCreamQAQ.YuQ.annotation.Inject") || injectType.equals("com.icecreamqaq.yuq.annotation.Inject"))
                    injectType = field.getType().getName();
                var list = injectObjects.get(injectType);
                if (list == null) {
                    if (injectType.contains("com.IceCreamQAQ.YuQ") || injectType.contains("com.icecreamqaq.yuq")) {
                        val paraType = Class.forName(injectType);
                        if (!paraType.isInterface()&& !Modifier.isAbstract(paraType.getModifiers())){
                            spawnAndPut(paraType, "");
                            list = injectObjects.get(injectType);
                        }
                    }
                }
                if (list == null) {
                    continue;
                }
                val instance = list.get(inject.name());

                field.setAccessible(true);
                field.set(obj, instance);

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
            }
        }
    }

    public <T> T spawnAndPut(Class<T> clazz, String name) {
        try {
            if (name == null) name = "";
            val obj = spawnInstance(clazz);

            putInjectObj(clazz.getName(), name, obj);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
