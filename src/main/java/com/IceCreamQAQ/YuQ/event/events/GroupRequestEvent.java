package com.IceCreamQAQ.YuQ.event.events;

import lombok.Data;

@Data
public class GroupRequestEvent extends Event {

    private Integer time;
    private Long qq;
    private Long group;
    private String msg;

    private Boolean accept;

}
