# acp 
###### v4.0.7
Application Construction Platform 应用构建平台。该项目是本人在日常工作中不断总结经验并结合最新的技术而封装的脚手架。本人会密切关注业界最新动态，并持续更新优化。使用该脚手架可快速搭建普通java应用、SpringBoot应用和SpringCloud应用。
## 相关组件版本及官方文档
- [Spring Boot 2.0.4.RELEASE](https://projects.spring.io/spring-boot)
- [Spring Cloud Finchley.SR1](http://projects.spring.io/spring-cloud)
## 一、环境要求
- jdk 1.8 及以上
- gradle 4.9

## 二、gralde 配置
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
- dockerPluginsVersion：docker插件版本号
- javaVersion：jdk版本号
- kotlinVersion：kotlin版本号
- scalaVersion：scala版本号
    
##### 4.build.gradle
统一构建脚本
    
##### 5.模块根路径/build.gradle
单个模块特有的构建脚本

### （二）自定义任务
- clearPj 清理所有输出文件
- release 编译打包并输出

### （三）升级命令
    gradlew wrapper --gradle-version=4.9 --distribution-type=all

## 三、工程说明
- doc目录下的files文件夹，当需要用到时放到打包后的jar同目录下即可
- 工程全局默认使用 UTF-8 字符集
- swagger url : /swagger-ui.html

## 四、开发 SpringBoot 应用
引入 acp 下相应模块包
### （一）模块说明，具体API文档请查看各模块 javadoc
##### 1. acp:acp-core 
    核心包，封装了常用工具类，包括jdbc、配置文件解析（xml、properties）、加解密算法、线程池、定时任务、四则运算、json处理、字符串处理
##### 2. acp:acp-core-orm
    核心扩展包，自定义 ORM 组建
##### 3. acp:acp-packet
    通讯打解包组建，依赖 acp-core；封装了http数据包、iso8583报文、xml报文的打解包操作
##### 4. acp:acp-client
    客户端组建包，依赖 acp-packet；封装了http、https、tcp、udp、ftp、sftp协议的客户端
##### 5. acp:acp-spring-boot-starter
    springboot 应用扩展组建，依赖 acp-client；扩展支持 tcp、udp、soap、ftp、sftp 等协议服务端开发
##### 6. acp:acp-spring-boot-starter-common
    springboot 应用扩展组建，依赖 acp-spring-boot-starter；增加配置文件预加载、springboot定时任务支持
##### 7. acp:acp-file
    文件处理组件包，依赖 acp-core；封装了excel、pdf、word、html、fremark模板文件的读写转换等操作
##### 8. acp:acp-message
    消息处理组建包，依赖 acp-core；封装了发送email
### （二）快速开发 springboot 应用
    （1）参考 test:testspringboot
    （2）依赖 acp:acp-spring-boot-starter-common
    （3）src/main/java/resources 中增加配置文件（测试配置文件在 src/test/resources）
        config 下的文件和 acp.properties 为选用
    （4）yml配置文件中增加数据源配置（单数据源或多数据源），数据库操作遵循 spring-data-jpa 标准，使用 hibernate 进行实例化
    （5）单数据源应用的话无需增加额外配置类，正常编写domain、repo、entity即可
    （6）多数据源应用需要增加对应每个数据源的 Jpa 配置类，并创建对应数据源的 repo、entity 包，之后再在对应包中编写 repo 和 entity
    （7）定时任务参考 pers.acp.test.application.task.Task1，继承 pers.acp.springboot.core.base.BaseSpringBootScheduledTask 类，并在 yml 配置文件中增加对应执行规则
    （8）自定义系统初始化任务，新增任务类，继承 pers.acp.springboot.core.base.BaseInitialization 类
    （9）自定义可控制监听器，新增监听器类，实现 pers.acp.springboot.common.interfaces.IListener 接口
    （10）pers.acp.test.application.test 包中有 soap/webservice、tcp 服务端开发demo，并在 resources/config 中增加相应配置
    （11）udp 同 tcp 的开发
    （12）如有需要，可选择引入 acp-file、acp-message 等包
## 五、开发 SpringCloud 应用
引入 acp 下相应模块包，demo 位于 cloud 下
### （一）模块说明
cloud 模块下的 build.gradle 文件内定义了 SpringCloud 版本号
##### 1. cloud:acp-spring-cloud-starter-common
    原子服务公共模块：
    （1）自定义程序入口注解
    （2）oauth2.0资源服务配置、客户端服务配置及远程单点认证机制
    （3）自定义 feign 并发策略、自定义 feign 请求拦截
##### 2. cloud:admin-server 
    可视化监控，监控服务状态、信息聚合
##### 3. cloud:eureka-server 
    服务注册发现
##### 4. cloud:gateway-server 
    网关服务
##### 5. cloud:oauth-server 
    统一认证服务：

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
### （二）组建开发
##### 1. 可视化监控
    cloud:admin-server
    （1）需定置化开发一整套 UI 界面及相关接口
    （2）需定置化开发收集 cloud 中各服务的信息，并提供接口给 UI 界面
    （3）修改 yml 配置
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
    （4）需定制 token 持久化方式（默认内存），配置进 AuthorizationServerConfiguration
##### 6. 日志服务
    （1）修改 yml kafka 相关配置
##### 7. 原子服务
    （1）引入 cloud:acp-spring-cloud-starter-common
    （2）参考 四、开发 SpringBoot 应用
    （3）原子服务即 SpringBoot 应用，引入额外的 spring-cloud 包，并在 yml 中增加相应配置
    （4）参考 cloud:hello、cloud:world、cloud:helloworld，入口类增加注解 @AcpCloudAtomApplication
## 六、打包为 docker 镜像
    （1）参考 test:testspringboot 模块中的 gradle.build
    （2）执行 dockerBuilder 任务，在 build/docker 目录下自动生成 Dockerfile 文件和 项目打包源文件
    （3）执行（1）之后镜像会直接在本地 docker 中生成，可直接使用
    （4）也可将生成好的 Dockerfile 和 xxx.jar 拷贝至有 docker 的环境中生成镜像
## 七、版本记录
[点击前往](doc/version_history.md)
## 八、待完善内容
[详情](doc/TODO.md)