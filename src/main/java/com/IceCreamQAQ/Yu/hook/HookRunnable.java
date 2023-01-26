package com.IceCreamQAQ.Yu.hook;

import org.jetbrains.annotations.NotNull;

public interface HookRunnable {

    default void init(@NotNull HookInfo info) {
    }

    default boolean preRun(@NotNull HookContext method){
        return false;
    }

    default void postRun(@NotNull HookContext method){

    }

    default boolean onError(@NotNull HookContext method){
        return false;
    }

}
