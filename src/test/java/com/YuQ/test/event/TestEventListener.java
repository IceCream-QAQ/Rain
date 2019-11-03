package com.YuQ.test.event;

import com.IceCreamQAQ.YuQ.annotation.Event;
import com.IceCreamQAQ.YuQ.annotation.EventListener;
import com.IceCreamQAQ.YuQ.event.events.OnMessageEvent;

@EventListener
public class TestEventListener {

    @Event
    public void onMessage(OnMessageEvent event){
        System.out.println("TestEventListener : onMessage");
    }

}
