# acp 
###### v5.1.0 [版本更新日志](doc/version_history.md)
Application Construction Platform 应用构建平台。该项目是本人在日常工作中不断总结经验并结合最新的技术而封装的脚手架。本人会密切关注业界最新动态，并持续更新优化。使用该脚手架可快速搭建普通java应用、SpringBoot应用和SpringCloud应用。
## 相关组件版本及官方文档
- [Spring Boot 2.1.0.RELEASE](https://projects.spring.io/spring-boot)
- [Spring Cloud Finchley.SR2](http://projects.spring.io/spring-cloud)
## 一、环境要求
- jdk 11
    - 注：kotlin 和 scala 目前仅支持 jdk 1.8
- gradle 4.10.2

## 二、gralde 配置及使用
### （一）配置文件
##### 1.gradle/dependencies.gradle
定义外部依赖版本号
    
##### 2.settings.gradle
定义项目/模块结构

##### 3.gradle.properties
gradle全局参数：
- gradleVersion：gradle版本号
- group：对应打包时的groupid
- version：工程版本号
- encoding：编译字符集
- mavenCentralUrl：maven中央仓库地址
- org.gradle.jvmargs：gradle执行时的jvm参数
- javaVersion：jdk版本号
- kotlinVersion：kotlin版本号
- scalaVersion：scala版本号
    
##### 4.build.gradle
公共构建脚本
    
##### 5.模块根路径/build.gradle
单个模块特有的构建脚本

### （二）自定义任务
- clearPj 清理所有输出文件
- release 编译、打包并输出

### （三）升级命令
``
    gradlew wrapper --gradle-version=4.10.2 --distribution-type=all
``

## 三、工程说明
- doc目录下的files文件夹，当需要用到时放到打包后的jar同目录下即可
- 工程全局默认使用 UTF-8 字符集
- acp 目录下为所有核心模块
- application 目录下为管理端的后台服务（已弃用，待 5.1.0 版本时从工程中剔除）
- cloud 目录下为基于 Spring Cloud 的一整套组建模块
- gradle 目录下为相关配置文件
- test 目录下为测试工程
- swagger url : /swagger-ui.html

## 四、开发 SpringBoot 应用
引入 acp 下相应模块包
### （一）模块说明，具体API文档请查看各模块 javadoc
##### 1. acp:acp-core 
    核心包，封装了常用工具类，包括jdbc、配置文件解析（xml、properties）、加解密算法、线程池、定时任务、四则运算、json处理、字符串处理
##### 2. acp:acp-core-orm
    核心扩展包，自定义 ORM 组建；以 hibernate 的思想自己封装的组建，仅供娱乐，建议还是使用hibernate
##### 3. acp:acp-packet
    通讯打解包组建，依赖 acp-core；封装了http数据包、iso8583报文、xml报文的打解包操作
##### 4. acp:acp-client
    客户端组建包，依赖 acp-packet；封装了http、https、tcp、udp、ftp、sftp协议的客户端
##### 5. acp:acp-spring-boot-starter
    springboot 应用扩展组建，依赖 acp-client；扩展支持 tcp、udp 等协议服务端开发；同时封装定时任务
##### 6. acp:acp-ftp
    应用扩展组建，依赖 acp-core；封装支持 ftp/sftp 服务端及客户端
##### 7. acp:acp-webservice
    应用扩展组建，依赖 acp-core；封装支持 soap/webservice 服务端和客户端，以及soap消息的构建、解析
##### 8. acp:acp-file
    文件处理组件包，依赖 acp-core；封装了excel、pdf、word、html、fremark模板文件的读写转换等操作
##### 9. acp:acp-message
    消息处理组建包，依赖 acp-core；封装了发送email
### （二）快速开发 springboot 应用
    （1）参考 test:testspringboot
    （2）依赖 acp:acp-spring-boot-starter
    （3）src/main/java/resources 中增加配置文件（测试配置文件在 src/test/resources）
        config 下的文件和 acp.properties 为选用
    （4）yml配置文件中增加数据源配置（单数据源或多数据源），数据库操作遵循 spring-data-jpa 标准，使用 hibernate 进行实例化
    （5）单数据源应用的话无需增加额外配置类，正常编写domain、repo、entity即可
    （6）多数据源应用需要增加对应每个数据源的 Jpa 配置类，并创建对应数据源的 repo、entity 包，之后再在对应包中编写 repo 和 entity
    （7）定时任务参考 pers.acp.test.application.task.Task1，继承 pers.acp.springboot.core.base.BaseSpringBootScheduledTask 类，并在 yml 配置文件中增加对应执行规则
    （8）自定义系统初始化任务，新增任务类，继承 pers.acp.springboot.core.base.BaseInitialization 类
    （9）自定义可控制监听器，新增监听器类，实现 pers.acp.springboot.core.interfaces.IListener 接口
    （10）pers.acp.test.application.test 包中有 soap/webservice、tcp 服务端开发demo，并在 resources/config 中增加相应配置
    （11）udp 同 tcp 的开发
    （12）如有需要，可选择引入 acp-file、acp-ftp、acp-message、acp-webservice 等包
### （三）启停 springboot 应用
- [jvm 参考参数](doc/jvm-params.txt)
- [启停脚本(Linux) server.sh](doc/script/server.sh)，根据实际情况修改第2行 APP_NAME 和第3行 JVM_PARAM 的值即可，和 SpringBoot 应用的 .jar 放在同一路径下
- [启停脚本(windows) server.bat](doc/script/server.bat)，根据实际情况修改第1行末尾需要执行的 jar 名称，和SpringBoot应用的 .jar 放在同一路径下
- Linux 命令：

|          命令         |           描述          |
| --------------------- | ----------------------- | 
| ./server.sh           | 查看可用参数            |
| ./server.sh status    | 查看系统运行状态        |
| ./server.sh start     | 启动应用                |
| ./server.sh stop      | 停止应用                |
| ./server.sh restart   | 重启应用                |
## 五、开发 SpringCloud 应用
引入 cloud 下相应模块包，demo 位于 cloud 下
### （一）模块说明
##### 1. cloud:acp-spring-cloud-starter-common
    原子服务公共模块：
    （1）自定义程序入口注解
    （2）oauth2.0 资源服务配置、客户端服务配置及远程单点认证机制
    （3）自定义 feign 并发策略、自定义 feign 请求拦截
    （4）hystrix 断路器
    （5）封装日志服务客户端，发送日志消息至 kafka
    （6）zipkin 链路追踪客户端
##### 2. cloud:admin-server 
###### 2.1 可视化监控，监控服务状态、信息聚合
|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 后台监控管理首页        |
| /hystrix              | 断路信息监控            |
###### 2.2 zipkin 链路追踪
- 服务端
> 从SpringCloud2.0 以后，官方已经不支持自定义服务，官方只提供编译好的jar包供用户使用。可以自行使用多种方式部署zipkin服务，并采用elasticsearch作为zipkin的数据存储器。
- 客户端
> - 依赖 cloud:acp-spring-cloud-starter-common
> - 增加 zipkin 相关配置
> ```yaml
> spring:
>   zipkin:
>     base-url: http://localhost:9411/
>   sleuth:
>     sampler:
>       probability: 1 #样本采集量，默认为0.1，为了测试这里修改为1，正式环境一般使用默认值。
> ```
##### 3. cloud:eureka-server 
服务注册发现

|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 服务状态监控界面        |
##### 4. cloud:gateway-server 
网关服务
##### 5. cloud:oauth-server 
统一认证服务：token 存储于 Redis，user 及 client 信息可扩展配置

|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /oauth/authorize      | 验证 basic认证保护      |
| /oauth/token          | 获取token的服务 url中没有client_id和client_secret的，走basic认证保护 |
| /oauth/check_token    | 资源服务器用来校验token basic认证保护 |
| /oauth/confirm_access | 用户授权 basic认证保护  |
| /oauth/error          | 认证失败 无认证保护     |

[查看认证过程](doc/oauth2.0认证.md)

##### 6. cloud:log-server
日志服务，使用 kafka 作为日志消息队列
##### 7. cloud:helloworld 
原子服务，分别调用 hello 和 world
##### 8. cloud:hello 
原子服务
##### 9. cloud:world 
原子服务
### （二）基础中间件环境搭建
基础中间件包括：redis、zookeeper、kafka、zoonavigator-api、zoonavigator-web、elasticsearch、zipkin、zipkin-dependencies、prometheus、grafana、setup_grafana_datasource
> - 启动服务
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yml up -d
> ```
> - 停止服务
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yml stop
> ```
> - 停止并删除容器实例
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yml down
> ```
### （三）组建开发
##### 1. 可视化监控
    cloud:admin-server
    （1）无需改动代码
    （2）修改 yml 配置即可
##### 2. 服务注册发现（支持高可用eureka集群）
    cloud:eureka-server
    （1）无需改动代码
    （2）修改 yml 配置即可
##### 3. 统一配置管理
    需依赖 git 环境，如有需要参照网上教程
##### 4. 网关服务
    cloud:gateway-server
    （1）需自定义限流策略
    （2）修改 yml 进行路由配置
##### 5. 认证服务
    cloud:oauth-server
    （1）需定制 UserPasswordEncoder 用户密码编码器，配置进 WebSecurityConfiguration
    （2）需定制用户（信息、角色、权限）初始化和查询方式 SecurityUserDetailsService，配置进 AuthorizationServerConfiguration
    （3）需定制客户端（信息）初始化和查询方式 SecurityClientDetailsService，配置进 AuthorizationServerConfiguration
    （4）token 持久化方式为 Redis，配置在 AuthorizationServerConfiguration
##### 6. 日志服务
    （1）修改 yml kafka 相关配置
##### 7. 原子服务
    （1）引入 cloud:acp-spring-cloud-starter-common
    （2）参考 四、开发 SpringBoot 应用
    （3）原子服务即 SpringBoot 应用，引入额外的 spring-cloud 包，并在 yml 中增加相应配置
    （4）参考 cloud:hello、cloud:world、cloud:helloworld，入口类增加注解 @AcpCloudAtomApplication
## 六、打包为 docker 镜像
自行编写 Dockerfile，使用命令单独执行或使用 docker-compose 批量执行，请自行百度
## 七、待完善内容
[详情](doc/TODO.md)