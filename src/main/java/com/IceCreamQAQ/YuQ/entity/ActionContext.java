package com.IceCreamQAQ.YuQ.entity;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.annotation.PathVar;
import com.IceCreamQAQ.YuQ.inject.ActionContextInject;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import lombok.val;
import lombok.var;

public abstract class ActionContext {

    @Inject
    protected YuQInject globalInject;

    protected ActionContextInject contextInject;

    public ActionContext(){
        contextInject=new ActionContextInject();
        contextInject.putInjectObj(ActionContext.class.getName(),"",this);
    }

    public void saveObj(Object object) {
        saveObj("", object);
    }

    public void saveObj(String name, Object object) {
        if (name == null) name = "";
        contextInject.putInjectObj(object.getClass().getName(), name, object);
    }

    public Object injectObj(Inject inject, Class<?> clazz) {
        var obj = this.contextInject.getObj(inject, clazz);
        if (obj == null) {
            obj = globalInject.getObj(inject, clazz);
        }
        return obj;
    }

    public <T> T injectObj(Class<T> clazz, String name) {
        var obj = this.contextInject.getObj(clazz.getName(), name);
        if (obj == null) {
            obj = globalInject.getObj(clazz.getName(), name);
        }
        return (T) obj;
    }

    public <T> T injectInject(Class<T> paraType, Class<?> injectType, String name) {
        var obj = this.contextInject.getObj(paraType.getName(), injectType.getName(), name);
        if (obj == null) {
            obj = globalInject.getObj(paraType.getName(), injectType.getName(), name);
        }
        return (T) obj;
    }

    public abstract <T> T injectPathVar(Class<T> clazz, Integer key, PathVar.Type type);

}
