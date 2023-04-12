# Rain - JVM Application Development Framework
## 自我介绍
Rain，一个更加清爽，更加灵活的 JVM 应用开发框架。  

Rain 使用 Kotlin 开发，并且原生兼容 Java/Kotlin。  

Rain 最低支持到 Java 1.8。  
同时 Rain 将随时准备更新的 JVM。  
目前 Rain 正在为 无需配置/无损特性 进行 GraalVM Native-Image 前进着。

Rain 是一个功能相对复杂的开发框架，以 "后端开发" 为基础思路，对现代项目开发工作流程探索。
不只 "后端"，借由 Rain 模块化/插件化 的设计，基于 Rain 开发的应用也可以享受 Rain 带来的 模块化/插件化 的能力。  
助你在符合你的现代化 项目管理/开发 工作流上探索的一臂之力。

## 功能

* Di  
  * 依赖注入
  * 静态变量依赖注入支持
  * 单例与多例支持
  * BeanFactory 支持
  * Config 指定注入实例支持
* Config
  * 多配置文件支持
  * 多环境配置切换支持
  * 配置项依赖注入支持
* Controller  
  * 完整的路由映射支持
  * 路由上携带参数支持
  * 前置拦截器，后置拦截器，异常拦截器
  * 参数映射
* EventBus
  * 自定义事件
  * 父子事件支持
* Job
  * 异步与等待
  * 定时任务
  * 时钟任务
  * 动态添加与取消
* Class 动态增强
  * 针对特殊场景，对字节码进行优化，减少开发时代码量。
    * Controller 优化
  * 方法 Hook
    * 方法调用前响应
      * 修改参数
      * 取消执行
      * 抛出异常
    * 方法调用后响应
      * 修改返回值
      * 抛出异常
    * 方法异常时响应
      * 取消异常并正常返回
      * 修改异常类型
    * 支持静态与非静态方法
    * 支持 Hook final 类与 final 方法
  * AOP 支持

## 依赖内容
<details>
  <summary>引用依赖表</summary>

* [Kotlin](https://kotlinlang.org/) ([Apache-2.0 license](https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt))
* [Kotlin-stdlib](https://kotlinlang.org/) ([Apache-2.0 license](https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt))
* [Kotlin-reflect](https://kotlinlang.org/) ([Apache-2.0 license](https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt))
* [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) ([Apache-2.0 license](https://github.com/Kotlin/kotlinx.coroutines/blob/master/LICENSE.txt))
* [okhttp3](https://square.github.io/okhttp/) ([Apache-2.0 license](https://github.com/square/okhttp/blob/master/LICENSE.txt))
* [logback](http://logback.qos.ch/) ([Logback LICENSE](https://github.com/qos-ch/logback/blob/master/LICENSE.txt))
* [fastjson2](https://github.com/alibaba/fastjson2) ([Apache-2.0 license](https://github.com/alibaba/fastjson2/blob/main/LICENSE))
* [snakeyaml](https://bitbucket.org/snakeyaml/snakeyaml) ([Apache-2.0 license](https://bitbucket.org/snakeyaml/snakeyaml/src/master/LICENSE.txt))
* [ecj](http://www.eclipse.org/jdt/) ([Eclipse Public License 2.0](https://projects.eclipse.org/projects/eclipse.jdt))
* [ehcache3](https://github.com/ehcache/ehcache3) ([Apache-2.0 license](https://www.ehcache.org/about/license.html))
* [asm](https://asm.ow2.org/) ([License](https://asm.ow2.io/license.html))
* [javax.inject](https://github.com/javax-inject/javax-inject) ([Apache-2.0 license](https://github.com/javax-inject/javax-inject#license))
* [lombok](https://projectlombok.org/) ([license](https://github.com/projectlombok/lombok/blob/master/LICENSE))
</details>
