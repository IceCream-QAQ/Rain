package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.event.EventListenerLoader_;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@LoadBy_(EventListenerLoader_.class)
public @interface EventListener_ {
}
