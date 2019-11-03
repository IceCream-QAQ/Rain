package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.event.EventBus;
import com.IceCreamQAQ.YuQ.event.events.*;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import com.IceCreamQAQ.YuQ.loader.AppLoader;
import com.IceCreamQAQ.YuQ.controller.route.RouteInvoker;
import lombok.val;
import lombok.var;
import org.meowy.cqp.jcq.entity.Anonymous;

public abstract class App {

    public YuQInject inject;

    @Inject
    private YuQ yuq;
    @Inject(name = "group")
    private RouteInvoker groupRouter;
    @Inject(name = "priv")
    private RouteInvoker privateRouter;
    @Inject
    private EventBus eventBus;

    private AppLoader loader;

    private boolean enable;

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger) throws Exception {
        inject = new YuQInject();
        inject.putInjectObj(inject.getClass().getName(), null, inject);
        inject.putInjectObj(YuQ.class.getName(), "", yu);
        inject.putInjectObj(AppLogger.class.getName(), "", logger);

        inject.injectObject(yu);
        inject.injectObject(logger);

        if (reloadAble != null) inject.putInjectObj(ReloadAble.class.getName(), "", reloadAble);
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader) throws Exception {
        inject = new YuQInject(appClassLoader);
        inject.putInjectObj(inject.getClass().getName(), null, inject);
        inject.putInjectObj(YuQ.class.getName(), "", yu);
        inject.putInjectObj(AppLogger.class.getName(), "", logger);
        inject.putInjectObj(ClassLoader.class.getName(), "appClassLoader", appClassLoader);

        inject.injectObject(yu);
        inject.injectObject(logger);

        if (reloadAble != null) inject.putInjectObj(ReloadAble.class.getName(), "", reloadAble);
    }

    public void start() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        loader = inject.spawnInstance(AppLoader.class);
        loader.load();
        inject.injectObject(this);

        eventBus.post(new AppStartEvent());
    }

    public void stop() {
        eventBus.post(new AppStopEvent());
    }

    public void enable() {
        eventBus.post(new AppEnableEvent());
        this.enable = true;
    }

    public void disable() {
        eventBus.post(new AppDisableEvent());
        this.enable = false;
    }


    public int onGroupMessage(int id, long group, long qq, Anonymous noName, String text, int font) throws Exception {
        val texts = text.split(" ");
        val message = Message.buildMessage(id, qq, group, noName, texts, text);

        val context = new ActionContext(message);
        inject.injectObject(context);

        val onGroupMessageEvent = new OnGroupMessageEvent();
        onGroupMessageEvent.setContext(context);
        if (eventBus.post(onGroupMessageEvent)) return 1;

        groupRouter.invoke(texts[0], context);

        val reMsg = context.getReMessage();
        if (reMsg != null) yuq.sendMessage(context);

        return context.getIntercept();
    }

    public int onPrivateMessage(int id, long qq, String text, int font) throws Exception {
        val texts = text.split(" ");
        val message = Message.buildMessage(id, qq, null, null, texts, text);

        val context = new ActionContext(message);
        inject.injectObject(context);

        val onPrivateMessageEvent = new OnPrivateMessageEvent();
        onPrivateMessageEvent.setContext(context);
        if (eventBus.post(onPrivateMessageEvent)) return 1;

        privateRouter.invoke(texts[0], context);

        val reMsg = context.getReMessage();
        if (reMsg != null) yuq.sendMessage(context);

        return context.getIntercept();
    }

    public int groupAdminAdd(int time, long group, long qq) {
        val event = new GroupAdminAddEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);

        if (eventBus.post(event)) return 1;
        return 0;
    }

    public int groupAdminDel(int time, long group, long qq) {
        val event = new GroupAdminDelEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);

        if (eventBus.post(event)) return 1;
        return 0;
    }

    public int groupMemberDecrease(int time, Long group, Long qq) {
        val event = new GroupMemberDecreaseEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);

        if (eventBus.post(event)) return 1;
        return 0;
    }

    public int groupKickMemberEvent(int time, Long group, Long qq, Long operater) {
        val event = new GroupKickMemberEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);
        event.setOperater(operater);

        if (eventBus.post(event)) return 1;
        return 0;
    }

    public int groupMemberAddEvent(int time, Long group, Long qq) {
        val event = new GroupMemberAddEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);

        if (eventBus.post(event)) return 1;
        return 0;
    }

    public int groupInviteMemberEvent(int time, Long group, Long qq, Long operater) {
        val event = new GroupInviteMemberEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);
        event.setOperater(operater);

        if (eventBus.post(event)) return 1;
        return 0;
    }

    public int friendAdd(int time, long qq) {
        val event = new FriendAddEvent();
        event.setTime(time);
        event.setQq(qq);

        if (eventBus.post(event)) return 1;
        return 0;
    }

    public int friendRequest(int time, long qq, String msg, String requestId) {
        val event = new FriendRequestEvent();
        event.setTime(time);
        event.setQq(qq);
        event.setMsg(msg);

        var flag = 0;
        if (eventBus.post(event)) flag = 1;

        if (event.cancel) {
            if (event.getAccept() != null)
                if (event.getAccept()) {
                    yuq.acceptFriendRequest(requestId, null);
                } else {
                    yuq.refuseFriendRequest(requestId);
                }
        }

        return flag;
    }

    public int joinGroupRequest(int time, long group, long qq, String msg, String requestId) {
        val event = new JoinGroupRequestEvent();
        event.setTime(time);
        event.setQq(qq);
        event.setGroup(group);
        event.setMsg(msg);

        var flag = 0;
        if (eventBus.post(event)) flag = 1;

        if (event.cancel) {
            if (event.getAccept() != null)
                if (event.getAccept()) {
                    yuq.acceptJoinGroupRequest(requestId);
                } else {
                    yuq.acceptJoinGroupRequest(requestId);
                }
        }

        return flag;
    }

    public int groupRequest(int time, long group, long qq, String msg, String requestId) {
        val event = new GroupRequestEvent();
        event.setTime(time);
        event.setQq(qq);
        event.setGroup(group);
        event.setMsg(msg);

        var flag = 0;
        if (eventBus.post(event)) flag = 1;

        if (event.cancel) {
            if (event.getAccept() != null)
                if (event.getAccept()) {
                    yuq.acceptGroupRequest(requestId);
                } else {
                    yuq.acceptGroupRequest(requestId);
                }

        }

        return flag;
    }

}
