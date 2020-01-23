package com.IceCreamQAQ.Yu.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Paths.class)
public @interface Path {
    String value();
}
