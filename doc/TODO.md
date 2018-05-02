## 待完善任务项
 - 整合统一后台管理：
    1. 后端分离架构（后端注册到eureka统一管理，前端彻底分离通过网关调用后端服务）
    2. 服务状态监控
    3. 服务链路监控
    4. 服务断路监控
    5. 动态维护网关路由
    6. oauth用户、客户端、角色、权限配置
    7. 日志监控
 - oauth-server 完善：
    1. AuthorizationServerConfig 中将 token 持久化到 redis 进行存储（待官方支持）
 - gateway-server 定制开发：
    1. 增加限流（使用 redis）
        1. 参考 112.7 RequestRateLimiter GatewayFilter Factory
 - 服务链路分析日志收集、监控elk（使用mq）
    1. https://www.cnblogs.com/shunyang/p/7011306.html
    2. https://www.jianshu.com/p/6b7f0488ddff
    3. http://blog.csdn.net/u010889990/article/details/78889730
 - 分布式调度服务
 - 增加 HTTP/2、WebSocket demo