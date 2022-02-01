package com.IceCreamQAQ.Yu.hook;

public interface HookRunnable {

    default void init(HookInfo info) {
    }

    default boolean preRun(HookMethod method){
        return false;
    }

    default void postRun(HookMethod method){

    }

    default boolean onError(HookMethod method){
        return false;
    }

}
