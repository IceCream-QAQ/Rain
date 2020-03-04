package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.loader.Loader_;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadBy_ {

    Class<? extends Loader_> value();

}
