package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.loader.enchant.Enchanter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE , ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnchantBy {

    Class<? extends Enchanter> value();

}
