package com.IceCreamQAQ.YuQ.platform.software.QQ;

import com.IceCreamQAQ.YuQ.App;
import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.YuQ;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.route.RouteInvoker;
import com.IceCreamQAQ.YuQ.event.EventBus;
import com.IceCreamQAQ.YuQ.event.events.*;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.loader.AppLoader;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import lombok.val;
import lombok.var;
import org.meowy.cqp.jcq.entity.Anonymous;

import java.util.List;
import java.util.Map;

public abstract class QQApp extends App {

    @Inject
    private YuQQ yuq;

    @Inject(name = "group")
    private RouteInvoker groupRouter;
    @Inject(name = "priv")
    private RouteInvoker privateRouter;

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger) throws Exception {
        super(reloadAble, yu, logger);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader) throws Exception {
        super(reloadAble, yu, logger, appClassLoader);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, paras);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, List<Object> list) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, list);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object[] objects) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, objects);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object object) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, object);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, List<Object> list, Map<String, Object> paras) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, list, paras);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object[] objects, Map<String, Object> paras) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, objects, paras);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object object, Map<String, Object> paras) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, object, paras);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras, List<Object> list) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, paras, list);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras, Object[] objects) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, paras, objects);
    }

    public QQApp(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras, Object object) throws Exception {
        super(reloadAble, yu, logger, appClassLoader, paras, object);
    }

    @Override
    public void init(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader) throws Exception {
        if (!(yu instanceof YuQQ)) throw new RuntimeException("Platform init Exception: Illegal parameter !");
        super.init(reloadAble, yu, logger, appClassLoader);
        inject.putInjectObj(YuQQ.class.getName(), "", yu);
    }

    public int onGroupMessage(int id, long group, long qq, Anonymous noName, String text, int font) throws Exception {
        val texts = text.split(" ");
        val message = QQMessage.buildMessage(id, qq, group, noName, texts, text);

        val context = new QQMessageActionContext(message);
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
        val message = QQMessage.buildMessage(id, qq, null, null, texts, text);

        val context = new QQMessageActionContext(message);
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
