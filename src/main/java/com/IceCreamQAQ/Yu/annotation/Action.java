package com.IceCreamQAQ.Yu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    String value();
    boolean prefix() default true;
    boolean suffix() default true;
    int loadPriority() default 0;

}
