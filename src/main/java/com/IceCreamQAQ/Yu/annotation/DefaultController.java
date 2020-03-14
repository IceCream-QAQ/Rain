package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.controller.DefaultControllerLoader;
import com.IceCreamQAQ.Yu.controller.DefaultControllerLoaderImpl;

import javax.inject.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@LoadBy_(DefaultControllerLoaderImpl.class)
@Named("default")
public @interface DefaultController {

//    String value() default "";

}
