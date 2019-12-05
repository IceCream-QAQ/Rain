 - 路由映射
 - Bean管理
 - 依赖注入
 - 开发时动态重载
 - 跨 QQ 机器人平台 （未完成）
 - DB 支持 （未完成）

PS：以上特性部分未完成。

YuQ Framework 让你体验不一样的机器人开发。

（用过 Spring 或者 其他 Java MVC 框架的同学可能会看起来很熟悉）

开发文档
====

创建项目
----

创建一个空的 Maven 项目

添加项目继承（0.0.1.3版本）

    <parent>
        <artifactId>YuQ-All</artifactId>
        <groupId>com.IceCreamQAQ.YuQ</groupId>
        <version>0.0.1.3</version>
    </parent>

添加项目继承（0.0.1.4版本及以后）

    <parent>
        <groupId>com.IceCreamQAQ.YuQ</groupId>
        <artifactId>YuQFramework</artifactId>
        <version>0.0.1.5</version>
    </parent>

因为我目前还没有提交到 Maven 中央仓库，所以引入依赖的时候还得引入一个 Maven 仓库。<br />
从0.0.1.4版本开始转向支持 Jcq 1.3.0。<br />
因为 Jcq 更换了包名。<br />

    <repositories>
        <repository>
            <url>https://maven.IceCreamQAQ.com/repository/maven-public/</url>
            <id>withdata</id>
        </repository>
    </repositories>

新建 YuQ.properties 配置文件

    # 这是项目编译输出 Class 的路径，用来做运行时重载，不需要重载时只需要删除本行
    project.location=d:\\Project\\QQ robot\\YuQ\\DemoPlugin\\target\\classes
    # 这是要扫描的 Controller 包路径。会扫描子目录。
    project.package.controller=com.yuq.demo.controller

Controller
----

    @Path("a/b")
    @GroupController
    public class TestController {

    }
    
GroupController 注解 用来声明这是一个面向群聊的控制器，同理我们可以使用 PrivateController 来声明一个面向私聊的控制器。<br />
Path 注解用来声明多级路由。<br />
你可以选择使用多个 Path 注解来声明多级路由，也可以选择一个 Path 注解，使用 / 来分割路由。<br />

Action
------

        @Action("/菜单")
        public String menu(){
            return "菜单";
        }

        @Action("菜单2")
        public String menu2(Long qq,Long group,String[] texts){
            return "菜单2";
        }

Action 注解用来声明这是一个处理器，也是路由的最终路径，也可以在 Action 注解值的最左边输入一个 / 来代表他是脱离控制器路由表，直接映射在跟路由上的。<br />
如 menu 方法，收到群聊消息"菜单"就会被触发，而 menu2 则需要收到群聊消息"a b 菜单2"才能被触发。 <br />
Action 注解声明的方法支持无参或者多个参数，参数的内容会经由依赖注入处理。<br />
Action 注解声明的方法支持无返回值，String ，Message ，以及其他。如果方法有返回值则会由返回值构建一个 Message 对象并进行发送消息。<br />
如 menu 方法，它返回了一个文本"菜单"，最终这个菜单会被发送到群内。<br />

拦截器
---

    @Before
    public void before() throws Message {
        throw new Message.Builder("你没有使用该命令的权限！").build();
    }

在 Controller 内，使用 Before 注解声明的方法为拦截器方法。<br />
拦截器方法也支持无参或者多个参数，并由依赖注入处理参数内容。<br />
一个 Controller 内，可以有多个拦截器。<br />
在 Action 被调用前，所有拦截器都会被依次触发。<br />
当任何一个拦截器抛出异常，本次处理会直接中断，如果 throw 的是一个 Message 对象，这个对象还会被发送。<br />
拦截器接受任何返回值，返回值会被储存至上下文依赖注入器以备后用。<br />

事件系统
----

0.0.1.4版本开始支持事件。

###事件监听###

**事件监听方法**

一个事件监听器是由 EventHandler 声明的，并带有一个或多个事件监听方法的类。<br />

一个标准的事件监听方法，是由 com.IceCreamQAQ.YuQ.annotation.Event 注解声明的方法。<br />
他应该具有一个 继承自 com.IceCreamQAQ.YuQ.event.events.Event 类的参数。<br />

    @Event
    public void onGroupMessage(OnGroupMessageEvent event){
        System.out.println("收到了群聊消息！");
    }

这是一个最简单的事件监听方法，他监听了所有的 OnGroupMessageEvent 。<br />
一个事件监听器会监听他本身，与所有继承他的事件。<br />
例如：OnGroupMessageEvent OnPrivateMessageEvent 都继承自 OnMessageEvent ，所以监听 OnMessageEvent 就等同于同时监听 OnGroupMessageEvent 与 OnPrivateMessageEvent 。

**事件监听优先级**

有时候我们可能需要多个监听方法监听同一事件，所以 YuQ 提供了三个不同的事件优先等级。<br />

    Event.Weight.height                   //别问，为啥是height不是high，我不知道！
    Event.Weight.normal
    Event.Weight.low

自上而下优先级分别是 高，中，低。<br />
优先级越高的会越早监听到事件。<br />

**事件的取消**

YuQ 框架标准，所有实现了 CancelEvent 接口（ cancelAble 方法返回 true ）的事件允许被取消。<br />
取消事件的方法也很简单，只需要将 Event 类的 cancel 成员变量的值设置为 true 。


###新增自定义事件###

**新增自定义事件**

一个事件的要求是继承自 com.IceCreamQAQ.YuQ.event.events.Event 类。<br />

    public class TestEvent extends Event {
    }

一个最基础的自定义事件。<br />
自定义事件可以带有多个成员变量，以备监听器取得所需参数进行处理。<br />

**自定义事件的触发**

触发一个事件要求需要注入 EventBus 实例，并实例化相应事件实例，调用 EventBus 的 post 方法进行触发。<br />
post 方法会返回一个 boolean 值来确定事件是否被取消。<br />
如果事件支持取消则应该进行相应判断并取消后续执行。<br />

###内置事件列表###

所有内置事件均位于 com.IceCreamQAQ.YuQ.QQ.events 包内。<br />

PS: 触发条件无代表事件不会直接被触发，但可能被子事件间接触发。<br />
PS: 父事件无代表直接继承 Event 类。<br />
PS: OnMessageEvent 及其子事件会在 Controller 之前执行，取消事件执行会取消 Controller 的后续处理。<br />

事件名称|事件介绍|触发条件|父事件|可否取消
---|---|---|---|---
AppStatusChangedEvent|应用状态被改变事件|无|无|否
AppStartEvent|应用初始化事件|应用在初次载入或被重载时触发|AppStatusChangedEvent|否
AppStopEvent|应用停止事件|应用在停止触发|AppStatusChangedEvent|否
AppEnableEvent|应用启用事件|应用在被启用时触发|AppStatusChangedEvent|否
AppDisableEvent|应用停用事件|应用在被停用时触发|AppStatusChangedEvent|否
---|---|---|---|---
OnMessageEvent|收到消息事件|无|无|是
OnPrivateMessageEvent|收到私聊消息|当QQ收到私聊消息时|OnMessageEvent|是
OnGroupMessageEvent|收到群聊消息|当QQ收到群聊消息时|OnMessageEvent|是
---|---|---|---|---
GroupEvent|群事件|无|无|否
GroupAdminEvent|群管理员事件|无|GroupEvent|否
GroupAdminAddEvent|群管理员增加事件|某群某成员成为管理员后触发|GroupAdminEvent|否
GroupAdminDelEvent|群管理员减少时间|某群某管理员被降职后触发|GroupAdminEvent|否
GroupMemberEvent|群成员变动事件|无|GroupEvent|否
GroupMemberAddEvent|群成员新增事件|有新人进群后触发|GroupMemberEvent|否
GroupInviteMemberEvent|群成员被邀请进群事件|有新人被邀请进群后触发|GroupMemberAddEvent|否
GroupMemberDecreaseEvent|群成员减少事件|某人离开本群后触发|GroupMemberEvent|否
groupKickMemberEvent|群成员被移出事件|某人被踢出群后触发|GroupMemberDecreaseEvent|否
---|---|---|---|---
FriendAddEvent|新增好友事件|在与某人成为好友后触发|无|否
FriendRequestEvent|好友申请事件|在收到好友申请时触发|无|否
---|---|---|---|---
JoinGroupRequestEvent|申请进群事件|某人申请加入某群时触发|无|否
GroupRequestEvent|群邀请事件|收到某人邀请进入某群时触发|无|否
依赖注入
----
依赖注入分为两部分：
1. 全局依赖注入
2. ActionContext 依赖注入

###全局依赖注入###

全局依赖注入指在新建 Bean 的时候对 Class 实例内的成员变量进行值注入。<br />
支持变量注入（通过 Inject 注解声明）和配置注入（通过 Config 注解声明）

    public @interface Inject {
        //可为空，为空时按照成员变量的类型注入，不为空时按照指定的类型注入。
        Class value() default Inject.class;
        //依赖注入 Bean 名。
        String name() default "";
    }

    public @interface Config {
        //需要注入的配置名称。
        String value();
        //如果配置为空时的默认值。
        String defaultValue() default "";
    }


### ActionContext 依赖注入###

ActionContext 依赖注入指在消息处理路径上的所有方法的参数的依赖注入。<br />
支持无注解，Inject 注解，PathVar 注解

    public @interface Inject {
        //可为空，为空时按照成员变量的类型注入，不为空时按照指定的类型注入。
        Class value() default Inject.class;
        //依赖注入 Bean 名。
        String name() default "";
    }

    public @interface PathVar {
        //映射第几级消息
        int value();
        //映射方式
        Type type() default Type.string;

        public enum Type{
            qq,//映射为Long
            group,//映射为Long
            flag,//会将开关之类的话映射为Boolean
            string,//映射为String
            number//映射为Integer
        }
    }



开发时重载
-----
只需要在配置文件内配置好路径，当程序检测到文件变动时会自动重载（可能会有几秒延迟）。<br />
在重载时会向CooQ 发送一个 Error 的 Log 作为提醒，但问题不大。

打包运行
-----
框架提供了一中非常方便的打 jar 包方式，会把依赖直接打进 jar 包中。<br />
执行 mvn assembly:assembly 即可 获得一个 jar 包。<br />
位于 target 目录下的 ${groupId}.${artifactId}-jar-with-dependencies.jar<br />
直接将本 jar 拷贝至 JCQ 应用目录即可。<br />
（需要相关 JSON ）

后续计划
----
目前 Action 方法调用使用的是反射调用，在后续的更新中会被优化为原生调用。<br />
可能会开发 WeChat Telegram 等平台<br />
完善数据库支持。

Demo程序
-----
YuQ 插件 Demo 版本0.0.1.3：[DemoPlugin.zip][2]<br />
YuQ 插件 Demo 版本0.0.1.5：[DemoPlugin.zip][1]

开放源代码
-----

Github：https://github.com/IceCream-Open/YuQ<br />
Gitee：https://gitee.com/IceCreamQAQ/YuQ<br />

请给我一颗星星，谢谢。


  [1]: https://www.IceCreamQAQ.com/usr/uploads/2019/10/666865152.zip
  [2]: https://www.IceCreamQAQ.com/usr/uploads/2019/09/863716666.zip