# Controller 
Controller 模块，通用的 MVC 功能模块。

## 文件内容
- Controller 主包内为通用 Controller 框架，下游应用可以考虑直接通过其集成。
- Simple 子包内为相对通用的 Controller 附加实现，可考虑直接使用。
- Dss 子包内为一个用于技术验证的动静分离路由及其 Controller 实现，功能可以保证，如果业务条件合适，下游应用可以直接在此基础上开发集成。
- Special 子包内为为了一些业务设计的通用编程接口，附加应用可能会使用相关内容，如果业务允许，请对其进行实现。