package com.YuQ.test.event;

import com.IceCreamQAQ.YuQ.annotation.Event;
import com.IceCreamQAQ.YuQ.annotation.EventHandler;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.events.GroupAdminAddEvent;
import com.IceCreamQAQ.YuQ.event.events.GroupAdminEvent;
import com.sobte.cqp.jcq.entity.CoolQ;

@EventHandler
public class TestEventHandler {

    @Inject
    public CoolQ cq;

    @Event
    public void group(GroupAdminEvent e) {
        if (e instanceof GroupAdminAddEvent) {
            cq.logInfo("群管理员事件", "群：" + e.getGroup() + "，新增管理员：" + e.getQq());
        }else {
            cq.logInfo("群管理员事件", "群：" + e.getGroup() + "，移除管理员：" + e.getQq());
        }
    }

}
