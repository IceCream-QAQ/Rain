package com.IceCreamQAQ.Yu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVar {

    int value();

    Type type() default Type.string;

    public enum Type{
        qq,
        group,
        flag,
        string,
        number
    }
}
