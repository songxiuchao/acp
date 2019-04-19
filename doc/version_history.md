## 版本更新记录
##### v5.1.4.2
> - [Upgrade] 文件压缩解压公共方法从 acp-file 迁移到 acp-core，使用 CommonTools 类
> - [Upgrade] 升级 gradle 至 5.4
##### v5.1.4.1
> - [Upgrade] 升级 docker 镜像版本
> - [Upgrade] 优化 testspringboot 中多数据源配置写法
> - [Upgrade] 更新文档
> - [Upgrade] 修复通过模板文件生成 excel 时，偶尔出现 NullPointException 的问题
##### v5.1.4
> - [Upgrade] 升级 Spring Boot 至 2.1.4.RELEASE
> - [Upgrade] 更新依赖包版本：
>   - jaxb_runtime (2.3.2)
>   - httpclient (4.5.8)
##### v5.1.3.2
> - [Upgrade] 升级 Spring Cloud 至 Greenwich.SR1
> - [Upgrade] 升级 gradle 至 5.3.1
> - [Fix] 修复定时任务线程池使用，兼容最新版本
##### v5.1.3.1
> - [Upgrade] 升级 gradle 至 5.3
> - [Upgrade] 优化重写 acp-spring-cloud-starter-common 中日志消息处理
> - [Upgrade] 增加日志服务相关配置
> - [Upgrade] 修改 demo 中日志服务相关配置
> - [Upgrade] jdk11 版本中移除 scala 和 kotlin 相关测试代码
> - [Upgrade] 更新文档
##### v5.1.3
> - [Upgrade] kotlin 更新至 1.3.21
> - [Upgrade] tcp/udp server 相关配置移入 yml 中
> - [Upgrade] 增加资源服务器开关配置，默认为 true
> - [Upgrade] 增加资源服务器不进行认证的url配置
> - [Upgrade] core:acp-file gradle脚本中删除冗余的依赖
> - [Upgrade] gradle 更新至 5.2.1
> - [Upgrade] 更新 SpringBoot 至 2.1.3.RELEASE
> - [Upgrade] 更新依赖包版本：
>   - spring boot admin (2.1.3)
>   - httpclient (4.5.7)
>   - jaxws-rt (2.3.2)
>   - mysql-connector-java (8.0.15)
> - [Upgrade] gradle 公共脚本中增加部署至 maven 仓库任务配置（包含本地和远程）
> - [Upgrade] cloud 下新增配置中心demo，其余服务连接配置中心获取配置信息，并通过bus进行动态刷新
> - [Upgrade] acp-spring-cloud-starter-common 添加自定义 PropertySourceLocator，供 config-client 获取配置信息
> - [Upgrade] 自定义的 RestTemplate 使用 HttpClient，且 feign 使用 httpclient 配置
> - [Upgrade] 规范各 demo 的 yml 配置文件书写
> - [Upgrade] 优化 acp-client 中 HttpClientBuilder
> - [Upgrade] 更新文档
> - [Upgrade] 优化线程池、任务代码，最少开销的情况下保证原子性
> - [Upgrade] 优化线程池使用
> - [Upgrade] acp-client 中 tcp 和 udp client 使用 netty 替换 mina
> - [Upgrade] acp-spring-boot-starter 中 tcp 和 udp 服务端 使用 netty 替换 mina
> - [Upgrade] acp-spring-cloud-starter-common 增加自定义 token 异常和权限异常响应配置
> - [Upgrade] 优化代码
> - [Delete] 删除无用测试代码
> - [Fix] 规范全局异常处理，调整对应异常响应的 HttpStatus
##### v5.1.2
> - 更新 SpringBoot 至 2.1.2.RELEASE
> - 更新 SpringCloud 至 Greenwich.RELEASE
> - 更新依赖包版本：
>   - spring boot admin (2.1.2)
>   - commons_text (1.6)
>   - xstream (1.4.11.1)
>   - jackson (2.9.8)
>   - poi (4.0.1)
>   - flying-saucer-pdf-itext5 (9.1.16)
> - 更新 gradle 至 5.1.1
> - 更新 kotlin 至 1.3.11
> - cloud 模块下 docker-compose-base.yml 文件修改，修改 zookeeper 端口号，增加 kafka-manager
> - 更新 spring-data-jpa 数据库连接配置及多数据源写法
> - 修改 spring boot 中 logback 配置
> - 优化定时器配置
> - acp-file 去除 jxl 相关依赖，不再封装 jxl 相关操作
> - 剥离订制工程 [management](https://github.com/zhangbin1010/acp-ace-php-back)
> - 修改核心模块名称
> - cloud 模块下 gateway-server 支持跨域
> - cloud 模块下，oauth-server 优化 token 服务配置，方便自定义扩展
> - 优化全局异常处理，增加 ErrorVO
> - 优化 oauth2 demo
> - cloud helloworld 中增加使用 RestTemplate 通过服务名调用远程服务的例子
> - 优化 REST 日志切片
> - 修改 jdbc mysql 链接字符串，支持 utf8mb4 字符集
> - 修改 DockerFile 内容，通过 kafka 传递信息给 zipkin server，gateway-server 去除 zipkin 相关依赖
> - 增加 swagger 开关配置
> - jackson 工具类增加 propertyNamingStrategy 参数
> - 自定义 spring boot start 中使用内置 jackson 进行 json 操作
> - 去除 Http Request 和 Response 的包裹封装，重写文件下载 controller
> - 核心工具类增加驼峰和下划线命名规则之间互相转化的方法
> - 规范配置项 acp.*
> - cloud 模块增加日志服务配置项
> - acp-spring-cloud-starter-common 增加 acp.cloud.oauth.oauth-server 配置项，oauth-server 可直接引用并修改配置项，不再需要单独编写自己的 ResourceServerConfiguration
> - 去除无用的依赖
> - cloud:oauth-server 中增加 authorization_code 方式配置 demo
> - 更新 cloud 下所有服务的 eureka 相关配置
> - 更新 spring boot admin 相关配置
> - 更新 docker-compose-base.yml 文件，集成 ELK 收集、监控日志
> - acp-spring-boot-starter 中去除 /download endpoint
> - 优化 CommonTools 工具类中文件删除方法的逻辑
> - Feign 传递 Authorization 时，Authorization 为空优化
> - acp-file 修复zip压缩和解压文件夹时的bug
> - 优化代码，减少冗余计算
> - 优化全局异常捕获
> - 优化文件下载 FileDownLoadHandle
> - 优化文件下载正则表达式匹配
##### v5.0.1
> - 更新 SpringBoot 至 2.0.6.RELEASE
> - 更新 SpringCloud 至 Finchley.SR2
> - 更新依赖包版本
> - cloud 模块下 docker-compose-base.yml 文件修改，注释暂时无用又占资源的服务
> - gateway 去除自定义 HiddenHttpMethodFilter ，官方已解决“Only one connection receive subscriber allowed” 的问题
> - 更新 cloud 中资源服务器配置，负载均衡直连 oauth2 进行认证，不经过 gateway
##### v5.0.0
> - 更新 gradle 至 4.10.2
> - 更新 kotlin 至 1.2.71
> - 优化系统停止时的过程，不使用单独的线程释放资源
> - 升级至 java 11，各依赖关系、兼容性、过时api改写
> - 剥离 webservice 服务端和客户端为单独的模块 acp-webservice，可选依赖
> - 剥离 ftp/sftp 服务端和客户端为单独的模块 acp-ftp，可选依赖
> - 删除 acp-spring-boot-starter-common 模块，代码合并入 acp-spring-boot-starter 模块
> - 增加链路监控，基于 zipkin 和 elasticsearch
> - cloud 模块下增加 dokerfile 文件夹
> - 更新依赖包版本
> - 优化 gateway 断路器
> - 更新 SpringBoot 至 2.0.5.RELEASE
> - 优化 gateway，增加 HiddenHttpMethodFilter，解决升级 SpringBoot 后 WebFlux 导致 “Only one connection receive subscriber allowed” 的问题
##### v4.0.8
> - 优化完善 acp:acp-spring-boot-starter 中的工具类
   pers/acp/springboot/core/tools/IpTools.java
> - BaseXML.java 中增加转换为 String 的方法
> - 优化 XStream 调用方式
> - 优化改造 socket 服务端和客户端
> - 优化代码书写
> - 优化线程池，支持自定义工作线程
> - 增加单机 SpringBoot 应用启停脚本
> - Cloud 完善监控服务，使用 Spring Boot Admin 2.0.2
> - 更新 gradle 至 4.10.1
> - 更新 scala 至 2.12.6
> - 更新 kotlin 至 1.2.70
##### v4.0.7
> - SpringCloud 升级至 Finchley.SR1
> - gradle 构建工具更新至 4.9
        gradlew wrapper --gradle-version=4.9 --distribution-type=all
> - 更新依赖包版本
> - kotlin 更新至 1.2.60
##### v4.0.6
> - SpringBoot 升级至 2.0.4.RELEASE
> - 更新依赖包版本
##### v4.0.5
> - kotlin 更新至 1.2.51
> - cloud 模块下新增 log-server，统一记录日志服务，从 kafka 消费记录日志的消息
> - cloud 模块下的 acp-spring-cloud-starter-common 新增记录日志实例，向 kafka 发送记录日志的消息
##### v4.0.4
> - SpringCloud 升级至 Finchley.RELEASE
> - gateway-server 增加限流配置
> - 更新 TODO.md 文档
##### v4.0.3
> - SpringBoot 升级至 2.0.3.RELEASE
> - 更新 jupiter 版本为 5.2.0
> - 修改 httpSecurity 策略配置
> - 优化 Feign 客户端访问资源服务器的认证配置
##### v4.0.2
> - SpringBoot 升级至 2.0.2.RELEASE
> - 修改数据源配置，连接池改用 HikariCP，去除 tomcat 连接池依赖
> - 升级 spring-cloud 版本为 Finchley.RC2
> - 增加相关注释
> - 修复 acp-client 中 webservice client 调用异常的bug，增加 webservice client 用法 demo
> - oauth-server 中，token 持久化到 redis
> - 修改 bouncycastle 依赖版本
##### v4.0.0
> - 优化 gradle 脚本，spring cloud 版本号写入 dependencies.gradle；删除 cloud 模块下的 build.gradle
> - 升级 spring-cloud 版本为 Finchley.RC1
> - 优化 hystrix 断路监控
> - 优化 feign oauth 验证
> - 升级 spring-boot 版本为 2.0.1.RELEASE
> - 更新各依赖包版本
> - gateway 增加自定义断路器
> - 修改 Eureka Server 及 Client 配置，优化服务注册/发现感知度
> - 优化 Controller 切片日志，重命名切片类
> - kotlin 版本升级至 1.2.41，增加 kotlin demo，test:testkotlin
> - 集成 junit5 单元测试，并增加测试用例编写，test:testspringboot、test:testkotlin
> - 取消 specification-arg-resolver 集成
> - 增加 scala demo, test:testscala
##### v3.9.0
> - 升级 spring-cloud 版本为 Finchley.M9
> - spring cloud 网关服务由 zuul 更换为 spring-cloud-gateway
> - gateway-server 增加路由配置
> - docker 打包脚本 demo，test:testspringboot
> - 修改 logback.xml 配置，输出到文件改为异步输出
> - acp-core 新增 hmac 加密工具类
> - 修改 acp-spring-boot-starter 包名
> - 修改 acp-spring-cloud-starter-common 包名
> - 资源服务器修改认证策略，改为 http 进行远程调用认证服务认证
> - acp-spring-cloud-starter-common 增加 feign 自定义并发策略，用以传递 ThreadLocal 中的信息，实现单点登录统一认证
> - cloud 原子服务、gateway 服务增加断路器超时和 feign 超时设置
> - cloud 原子服务、gateway 服务开启懒加载
> - 优化 gradle 构建脚本
> - kotlin 版本升级为 1.2.31
> - acp-client 优化 httpclient 写法
> - 增加 controller-aspect 配置项
> - 拆分 acp-core 中的 orm 自定义部分，新增 acp-core-orm 模块
> - 增加文档
##### v3.8.0
> - cloud 下新增 acp-spring-cloud-starter-common 模块
> - 优化 httpclient 封装
> - 升级 spring-cloud 版本为 Finchley.M8，完美兼容 spring-boot 2.0.0.RELEASE
> - 去除 zipkin-server 模块
> - 更新数据库驱动包版本
##### v3.7.0
> - PackageTools 从 acp-common-spring-boot-starter 模块移入 acp-spring-boot-starter 模块中
> - gateway 增加路由熔断逻辑
> - 增加熔断聚合监控
> - systemControl.initialization() 移入 acp-common-spring-boot-starter 模块，自动执行
> - 删除 turbine-server 模块，聚合监控合并入 admin-server 模块
> - 完善 admin-server 模块，监控各节点服务的健康状态
> - 增加 TODO.md 内容
> - 优化 acp-core 配置文件加载、读取
> - acp-common-spring-boot-starter 名称变更为 acp-spring-boot-starter-common
> - 优化 acp-spring-boot-starter-common 模块初始化预加载逻辑，找不到配置文件的情况下日志提醒，并且不开启其他服务，开发任何springboot应用都可直接引用
##### v3.6.0
> - test:testspringboot 模块增加 docker 镜像打包 demo
> - 优化cloud下各组建 demo
##### v3.5.0
> - cloud 增加 admin-server 模块
> - 统一设置 cloud 模块下各子模块依赖的 spring-boot 版本号
> - 调整acp下各模块依赖关系和代码
##### v3.0.0
> - 升级 spring-boot 版本至 1.5.9.RELEASE
> - 增加模块 acp-common-spring-boot-starter
##### v2.0.0
> - 整合spring-boot