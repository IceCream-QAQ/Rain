package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.controller.DefaultControllerLoaderImpl;
import com.IceCreamQAQ.Yu.loader.enchant.MethodParaNamedEnchanter;

import javax.inject.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@LoadBy(DefaultControllerLoaderImpl.class)
@Named("default")
@EnchantBy(MethodParaNamedEnchanter.class)
public @interface DefaultController {

}
