package com.IceCreamQAQ.YuQ.event.events;

import lombok.Data;

@Data
public class GroupKickMemberEvent extends GroupMemberDecreaseEvent {

    private Long operater;

}
