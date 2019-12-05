package com.IceCreamQAQ.YuQ.loader;

import lombok.Data;

import java.lang.annotation.Annotation;

@Data
public class LoadItem {

    private Annotation annotation;
    private Class<?> type;
    private Object instance;

}
