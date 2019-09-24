package com.IceCreamQAQ.YuQ.event.events;

import lombok.Data;

@Data
public class GroupInviteMemberEvent extends GroupMemberAddEvent {

    private Long operater;

}
