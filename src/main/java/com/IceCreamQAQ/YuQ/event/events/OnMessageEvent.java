package com.IceCreamQAQ.YuQ.event.events;

import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.Data;

@Data
public class OnMessageEvent extends Event implements CancelEvent {

    private ActionContext context;

}
