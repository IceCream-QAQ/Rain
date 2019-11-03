package com.YuQ.test.event;

import com.IceCreamQAQ.YuQ.annotation.Event;
import com.IceCreamQAQ.YuQ.annotation.EventHandler;
import com.IceCreamQAQ.YuQ.annotation.EventListener;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.events.*;
import org.meowy.cqp.jcq.entity.CoolQ;

@EventListener
public class TestEventHandler {

    @Inject
    public CoolQ cq;

    /***
     * 这个方法会在所有消息处理之前被调用
     */
//    @Event
    @Event(weight = Event.Weight.high)
    public void onMessage(OnMessageEvent event){
        System.out.println("onMessage");
    }

    /***
     * 这个方法会在群消息处理之前被调用
     */
    @Event
    public void onGroupMessageEvent(OnGroupMessageEvent event){
        System.out.println("onGroupMessageEvent");
    }

    /***
     * 这个方法会在私聊消息处理之前被调用
     */
    @Event
    public void onPrivateMessageEvent(OnPrivateMessageEvent event){
        System.out.println("onPrivateMessageEvent");
    }

}
