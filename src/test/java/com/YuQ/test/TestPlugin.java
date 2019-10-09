package com.YuQ.test;

import com.IceCreamQAQ.YuQ.platform.JCQ.JCQStartBase;
import com.IceCreamQAQ.YuQ.platform.debug.DebugApp;
import lombok.val;
import org.meowy.cqp.jcq.entity.CQDebug;
import org.meowy.cqp.jcq.entity.CoolQ;

public class TestPlugin extends JCQStartBase {

    public TestPlugin(CoolQ CQ) {
        super(CQ);
    }

    public static void main(String... args) throws Exception {
// CQ此变量为特殊变量，在JCQ启动时实例化赋值给每个插件，而在测试中可以用CQDebug类来代替他
//        CoolQ cq = new CQDebug();//new CQDebug("应用目录","应用名称") 可以用此构造器初始化应用的目录
//        cq.logInfo("[JCQ] TEST Yu", "测试启动");// 现在就可以用CQ变量来执行任何想要的操作了
        // 要测试主类就先实例化一个主类对象
        val test = new TestPlugin(new CQDebug());
        // 下面对主类进行各方法测试,按照JCQ运行过程，模拟实际情况
        test.startup();// 程序运行开始 调用应用初始化方法
        test.enable();// 程序初始化完成后，启用应用，让应用正常工作
        // 开始模拟发送消息
        // 模拟私聊消息
//        // 开始模拟QQ用户发送消息，以下QQ全部编造，请勿添加
//        demo.privateMsg(0, 10001, 2234567819L, "小姐姐约吗", 0);
//        demo.privateMsg(0, 10002, 2222222224L, "喵呜喵呜喵呜", 0);
//        demo.privateMsg(0, 10003, 2111111334L, "可以给我你的微信吗", 0);
//        demo.privateMsg(0, 10004, 3111111114L, "今天天气真好", 0);
//        demo.privateMsg(0, 10005, 3333333334L, "你好坏，都不理我QAQ", 0);
//        // 模拟群聊消息
//        // 开始模拟群聊消息
//        demo.groupMsg(0, 10006, 3456789012L, 3333333334L, "", "菜单", 0);
//        demo.groupMsg(0, 10008, 3456789012L, 11111111114L, "", "小喵呢，出来玩玩呀", 0);
//        demo.groupMsg(0, 10009, 427984429L, 3333333334L, "", "[CQ:at,qq=2222222224] 来一起玩游戏，开车开车", 0);
//        demo.groupMsg(0, 10010, 427984429L, 3333333334L, "", "好久不见啦 [CQ:at,qq=11111111114]", 0);
//        demo.groupMsg(0, 10011, 427984429L, 11111111114L, "", "qwq 有没有一起开的\n[CQ:at,qq=3333333334]你玩嘛", 0);
//        demo.privateMsg(1,10086,2667497585L,"菜单",0);
//
//        demo.groupMsg(1, 10006, 123123L, 2667497585L, null, "菜单", 0);
//        demo.groupMsg(1, 10006, 123123L, 2667497585L, null, "登录 你好 测试", 0);
//        demo.groupMsg(1, 10006, 123123L, 2667497585L, null, "设置 广告拦截 启用", 0);
//        demo.groupMsg(1, 10006, 123123L, 2667497585L, null, "测试1 测试12 测试Action", 0);
//
        test.privateMsg(1,10006,2667497585L,"菜单",0);

        test.groupMsg(1,10006, 101471173, 2667497585L, null, "菜单", 0);
        test.groupMsg(1,10006, 101471173, 2667497585L, null, "a b c 菜单2", 0);

        test.groupAdmin(1,10006,101471173,2667497585L);
//        demo.groupMsg(1, 10006, 123123L, 2667497585L, null, "测试2 测试22 测试", 0);
//
//        demo.groupMsg(1, 10006, 123123L, 2667497585L, null, "测试Action", 0);
        // ......
        // 依次类推，可以根据实际情况修改参数，和方法测试效果
        // 以下是收尾触发函数
        // demo.disable();// 实际过程中程序结束不会触发disable，只有用户关闭了此插件才会触发
        test.exit();// 最后程序运行结束，调用exit方法
    }

    @Override
    public String appId() {
        return null;
    }
}
