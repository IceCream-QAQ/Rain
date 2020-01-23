package com.IceCreamQAQ.Yu.event.events;

import lombok.Data;

@Data
public class Event {

    public boolean cancel=false;

    public boolean cancelAble(){
        return false;
    }

}
