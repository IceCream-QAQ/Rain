package com.IceCreamQAQ.YuQ.event.events;

import lombok.Data;

@Data
public class GroupMemberEvent extends GroupEvent {

    private Integer time;
    private Long qq;


}
