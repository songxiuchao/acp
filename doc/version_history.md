## 版本更新记录
##### 4.1.0
    1. SpringBoot 升级至 2.0.2
    2. 修改数据源配置，连接池改用 HikariCP，去除 tomcat 连接池依赖
    3. 升级 spring-cloud 版本为 Finchley.RC2
    4. 增加相关注释
    5. 修复 acp-client 中 webservice client 调用异常的bug，增加 webservice client 用法 demo
    6. oauth-server 中，token 持久化到 redis
##### 4.0.0
    1. 优化 gradle 脚本，spring cloud 版本号写入 dependencies.gradle；删除 cloud 模块下的 build.gradle
    2. 升级 spring-cloud 版本为 Finchley.RC1
    3. 优化 hystrix 断路监控
    4. 优化 feign oauth 验证
    5. 升级 spring-boot 版本为 2.0.1.RELEASE
    6. 更新各依赖包版本
    7. gateway 增加自定义断路器
    8. 修改 Eureka Server 及 Client 配置，优化服务注册/发现感知度
    9. 优化 Controller 切片日志，重命名切片类
    10. kotlin 版本升级至 1.2.41，增加 kotlin demo，test:testkotlin
    11. 集成 junit5 单元测试，并增加测试用例编写，test:testspringboot、test:testkotlin
    12. 取消 specification-arg-resolver 集成
    13. 增加 scala demo, test:testscala
##### 3.9.0
    1. 升级 spring-cloud 版本为 Finchley.M9
    2. spring cloud 网关服务由 zuul 更换为 spring-cloud-gateway
    2. gateway-server 增加路由配置
    3. docker 打包脚本 demo，test:testspringboot
    4. 修改 logback.xml 配置，输出到文件改为异步输出
    5. acp-core 新增 hmac 加密工具类
    6. 修改 acp-spring-boot-starter 包名
    7. 修改 acp-spring-cloud-starter-common 包名
    8. 资源服务器修改认证策略，改为 http 进行远程调用认证服务认证
    9. acp-spring-cloud-starter-common 增加 feign 自定义并发策略，用以传递 ThreadLocal 中的信息，实现单点登录统一认证
    10. cloud 原子服务、gateway 服务增加断路器超时和 feign 超时设置
    11. cloud 原子服务、gateway 服务开启懒加载
    12. 优化 gradle 构建脚本
    13. kotlin 版本升级为 1.2.31
    14. acp-client 优化 httpclient 写法
    15. 增加 controller-aspect 配置项
    16. 拆分 acp-core 中的 orm 自定义部分，新增 acp-core-orm 模块
    17. 增加文档
##### 3.8.0
    1. cloud 下新增 acp-spring-cloud-starter-common 模块
    2. 优化 httpclient 封装
    3. 升级 spring-cloud 版本为 Finchley.M8，完美兼容 spring-boot 2.0.0.RELEASE
    4. 去除 zipkin-server 模块
    5. 更新数据库驱动包版本
##### 3.7.0
    1. PackageTools 从 acp-common-spring-boot-starter 模块移入 acp-spring-boot-starter 模块中
    2. gateway 增加路由熔断逻辑
    3. 增加熔断聚合监控
    4. systemControl.initialization() 移入 acp-common-spring-boot-starter 模块，自动执行
    5. 删除 turbine-server 模块，聚合监控合并入 admin-server 模块
    6. 完善 admin-server 模块，监控各节点服务的健康状态
    7. 增加 TODO.md 内容
    8. 优化 acp-core 配置文件加载、读取
    9. acp-common-spring-boot-starter 名称变更为 acp-spring-boot-starter-common
    10. 优化 acp-spring-boot-starter-common 模块初始化预加载逻辑，找不到配置文件的情况下日志提醒，并且不开启其他服务，开发任何springboot应用都可直接引用
##### 3.6.0
    1. test:testspringboot 模块增加 docker 镜像打包 demo
    2. 优化cloud下各组建 demo
##### 3.5.0
    1. cloud 增加 admin-server 模块
    2. 统一设置 cloud 模块下各子模块依赖的 spring-boot 版本号
    3. 调整acp下各模块依赖关系和代码
##### 3.0.0
    1. 升级 spring-boot 版本至 1.5.9.RELEASE
    2. 增加模块 acp-common-spring-boot-starter
##### 2.0.0
    1. 整合spring-boot