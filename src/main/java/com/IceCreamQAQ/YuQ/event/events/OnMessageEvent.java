package com.IceCreamQAQ.YuQ.event.events;

import com.IceCreamQAQ.YuQ.controller.MessageActionContext;
import lombok.Data;

@Data
public class OnMessageEvent extends Event implements CancelEvent {

    private MessageActionContext context;

}
