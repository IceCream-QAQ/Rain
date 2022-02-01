package com.IceCreamQAQ.Yu.hook;

import org.jetbrains.annotations.NotNull;

public interface HookRunnable {

    default void init(@NotNull HookInfo info) {
    }

    default boolean preRun(@NotNull HookMethod method){
        return false;
    }

    default void postRun(@NotNull HookMethod method){

    }

    default boolean onError(@NotNull HookMethod method){
        return false;
    }

}
