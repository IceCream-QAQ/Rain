package com.IceCreamQAQ.YuQ.event.events;

import com.IceCreamQAQ.YuQ.event.EventInvoker;
import com.IceCreamQAQ.YuQ.event.TestEventHandler;

public class TestInvokerImpl implements EventInvoker {

    private TestEventHandler instance;

    public TestInvokerImpl(TestEventHandler a){
        instance=a;
    }

    @Override
    public void invoke(Event event) {
        if (!(event instanceof OnGroupMessageEvent))return;
        instance.onGroupMessageEvent((OnGroupMessageEvent) event);
    }
}
