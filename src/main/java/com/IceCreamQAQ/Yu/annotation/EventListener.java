package com.IceCreamQAQ.Yu.annotation;

import com.IceCreamQAQ.Yu.event.EventListenerLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@LoadBy(EventListenerLoader.class)
public @interface EventListener {
}
