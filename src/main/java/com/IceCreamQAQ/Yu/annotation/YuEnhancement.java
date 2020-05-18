package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.util.YuEnhancer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface YuEnhancement {

    Class<? extends YuEnhancer> value();

}
