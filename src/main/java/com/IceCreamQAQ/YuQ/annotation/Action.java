package com.IceCreamQAQ.YuQ.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    String value();

    boolean re() default false;

    boolean at() default false;

    int level() default 2;

    boolean intercept() default false;

}
