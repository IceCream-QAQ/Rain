package com.IceCreamQAQ.Yu.event.events;

public interface CancelEvent{

    default boolean cancelAble(){
        return true;
    }

}
