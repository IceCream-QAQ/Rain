package com.IceCreamQAQ.YuQ.platform.JCQ;

import com.IceCreamQAQ.YuQ.App;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import lombok.val;
import org.meowy.cqp.jcq.entity.*;
import org.meowy.cqp.jcq.event.JcqApp;

public abstract class JCQStartBase extends JcqApp implements ICQVer, IMsg, IRequest, ReloadAble {

    App app;

    public JCQStartBase(CoolQ CQ) {
        super(CQ);
    }

    public void reload() {
        try {
            val app = new JCQApp(this, CQ,this.getClass().getClassLoader());
            app.enable();
            this.app = app;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String appInfo() {
        return CQAPIVER + "," + appId();
    }

    public abstract String appId();

    public int startup() {
        try {
            System.out.println(this.getClass().getName());
            app = new JCQApp(this, CQ,this.getClass().getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int exit() {
        app.stop();
        return 0;
    }

    public int enable() {
        app.enable();
        return 0;
    }

    public int disable() {
        app.disable();
        return 0;
    }

    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        try {
            return app.onPrivateMessage(msgId, fromQQ, msg, font);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        Anonymous noName = null;
        if (fromQQ == 80000000L && !fromAnonymous.equals("")) {
            noName = CQ.getAnonymous(fromAnonymous);
        }

        try {
            return app.onGroupMessage(msgId, fromGroup, fromQQ, noName, msg, font);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return MSG_IGNORE;
    }

    public int discussMsg(int subtype, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
        return MSG_IGNORE;
    }

    public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
        GroupFile groupFile = CQ.getGroupFile(file);
        if (groupFile == null) {
            return MSG_IGNORE;
        }
        return MSG_IGNORE;
    }

    public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
        if (subtype == 1) {
            return app.groupAdminDel(sendTime, fromGroup, beingOperateQQ);
        }
        return app.groupAdminAdd(sendTime, fromGroup, beingOperateQQ);
    }

    public int groupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        if (subtype == 1) {
            return app.groupMemberDecrease(sendTime, fromGroup, fromQQ);
        }
        return app.groupKickMemberEvent(sendTime, fromGroup, fromQQ, beingOperateQQ);
    }

    public int groupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        if (subtype == 1) {
            return app.groupMemberAddEvent(sendTime, fromGroup, fromQQ);
        }
        return app.groupInviteMemberEvent(sendTime, fromGroup, fromQQ, beingOperateQQ);
    }

    public int friendAdd(int subtype, int sendTime, long fromQQ) {
        return app.friendAdd(sendTime, fromQQ);
    }

    public int requestAddFriend(int subtype, int sendTime, long fromQQ, String msg, String responseFlag) {

        return app.friendRequest(sendTime, fromQQ, msg, responseFlag);
    }

    public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {

        if (subtype == 1) {
            return app.joinGroupRequest(sendTime, fromGroup, fromQQ, msg, responseFlag);
        }else {
            return app.groupRequest(sendTime, fromGroup, fromQQ, msg, responseFlag);
        }
    }

}
