package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.controller.NewControllerLoaderImpl;
import com.IceCreamQAQ.Yu.loader.enchant.MethodParaNamedEnchanter;

import javax.inject.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Named("default")
@LoadBy(NewControllerLoaderImpl.class)
@EnchantBy(MethodParaNamedEnchanter.class)
public @interface NewDefaultController {
}
