package com.IceCreamQAQ.YuQ.event.events;

public interface CancelEvent{

    default boolean cancelAble(){
        return true;
    }

}
