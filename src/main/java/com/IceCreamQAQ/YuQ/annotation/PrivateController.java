package com.IceCreamQAQ.YuQ.annotation;

import com.IceCreamQAQ.YuQ.loader.ControllerLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@LoadBy(ControllerLoader.class)
public @interface PrivateController {
    String value() default "";
}
