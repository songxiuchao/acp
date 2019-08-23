# acp 
###### v6.1.4 [版本更新日志](doc/version_history.md)
- Application Construction Platform 应用构建平台
- 该项目是用Kotlin和Java语言混编封装的脚手架。本人会密切关注业界最新动态，并使用最新技术持续更新优化。
- 使用该脚手架可快速搭建基于Kotlin或Java语言的普通应用、SpringBoot应用和SpringCloud应用。

## 相关组件版本及官方文档
- [Spring Boot 2.1.7.RELEASE](https://projects.spring.io/spring-boot)
- [Spring Cloud Greenwich.SR2](http://projects.spring.io/spring-cloud)

## 技术栈
- joda-time
- okhttp
- netty
- xstream
- hibernate
- jackson
- poi
- freemarker
- flying-saucer-pdf-itext5
- swagger2
- junit5
- spring-cloud
    - spring-boot
        - spring-aop
        - spring-data-jpa
        - spring-security
        - spring-security-oauth2
        - spring-data-redis
        - spring-boot-actuator
    - spring-data-redis-reactive
    - spring-boot-admin-server
    - spring-cloud-netflix-eureka-server
    - spring-cloud-netflix-eureka-client
    - spring-cloud-netflix-hystrix
    - spring-cloud-netflix-dashboard
    - spring-cloud-netflix-turbine
    - spring-cloud-gateway
    - spring-cloud-stream-binder-kafka
    - spring-cloud-openfeign
    - spring-cloud-sleuth-zipkin
    - spring-cloud-config-server
    - spring-cloud-bus-kafka
    
## 一、环境要求及开发语言
- java 11
- gradle 5.5.1
- kotlin 1.3.41

## 二、gradle 脚本配置及使用
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
    
##### 4.build.gradle
公共构建脚本
    
##### 5.模块根路径/build.gradle
单个模块特有的构建脚本

### （二）自定义任务（在公共构建脚本中定义）
- clearPj 清理所有输出文件
- release 编译、打包并输出
- install 打包部署至本地 maven 仓库
- uploadArchives 打包部署至远程 maven 仓库，需自行创建文件 gradle/deploy.gradle，配置对应的参数，文件内容参考如下：
```groovy
ext {
    mavenUploadUrl = "http://localhost:8081/nexus/content/repositories/thirdparty"
    mavenUserName = "admin"
    mavenPassword = "admin123"
}
```

### （三）升级命令
``
    gradlew wrapper --gradle-version=5.5.1 --distribution-type=all
``

## 三、工程说明
- doc目录下的files文件夹，当需要用到时放到打包后的jar同目录下即可
- 工程全局默认使用 UTF-8 字符集
- acp 目录下为所有核心模块
- cloud 目录下为基于 Spring Cloud 的一整套组件模块
- gradle 目录下为相关配置文件
- test 目录下为测试工程
- swagger url : /swagger-ui.html

## 四、开发 SpringBoot 应用
引入 acp 下相应模块包
### （一）模块说明，具体API文档请查看各模块 javadoc
##### 1. acp:acp-core 
> - 核心包
> - 封装了常用工具类，包括jdbc、配置文件解析（xml、properties）、加解密算法、线程池、定时任务、四则运算、json处理、字符串处理
##### 2. acp:acp-core-orm
> - 核心扩展包
> - 依赖 acp:acp-core
> - 自定义 ORM 组件；以 hibernate 的思想自己封装的组件，仅供娱乐，建议还是使用hibernate
##### 3. acp:acp-packet
> - 通讯打解包组件
> - 依赖 acp-core
> - 封装了http数据包、iso8583报文、xml报文的打解包操作
##### 4. acp:acp-client
> - 客户端组件包
> - 依赖 acp-packet
> - 封装了http、https、tcp、udp的客户端
##### 5. acp:acp-ftp
> - 应用扩展组件
> - 依赖 acp-core
> - 封装支持 ftp/sftp 服务端及客户端
##### 6. acp:acp-webservice
> - 应用扩展组件
> - 依赖 acp-core
> - 封装支持 soap/webservice 服务端和客户端，以及soap消息的构建、解析
##### 7. acp:acp-file
> - 文件处理组件包
> - 依赖 acp-core
> - 封装了excel、pdf、word、html、freemarker模板文件的读写转换等操作
##### 8. acp:acp-message
> - 消息处理组件包
> - 依赖 acp-core
> - 封装了发送email
##### 9. boot:acp-spring-boot-starter
> - springboot 应用扩展组件
> - 依赖 acp-client
> - 在 springboot 基础上整合大量底层功能，扩展支持 tcp、udp 等协议服务端配置；同时封装定时任务
##### 10. boot:acp-spring-boot-starter-ftp
> - springboot 应用扩展组件
> - 依赖 acp-spring-boot-starter
> - 扩展支持 ftp、sftp 等协议服务端自动配置
### （二）快速开发 springboot 应用
##### 全局说明
> - 统一注入 pers.acp.spring.boot.interfaces.LogAdapter 进行日志记录
##### 1. 开发说明
- （1）参考 test:testspringboot
- （2）依赖 boot:acp-spring-boot-starter
- （3）src/main/java/resources 中增加配置文件（测试配置文件在 src/test/resources）config 下的文件和 acp.properties 为选用
- （4）yml配置文件中增加数据源配置（单数据源或多数据源），数据库操作遵循 spring-data-jpa 标准，使用 hibernate 进行实例化
- （5）单数据源应用的话无需增加额外配置类，正常编写domain、repo、entity即可
- （6）多数据源应用需要增加对应每个数据源的 Jpa 配置类，并创建对应数据源的 repo、entity 包，之后再在对应包中编写 repo 和 entity
- （7）定时任务参考 test:testspringboot 模块 pers.acp.test.application.task.Task1，继承 pers.acp.spring.boot.base.BaseSpringBootScheduledTask 类，并在 yml 配置文件中增加对应执行规则
- （8）自定义系统初始化任务，新增任务类，继承 pers.acp.spring.boot.base.BaseInitialization 类
- （9）自定义可控制监听器，新增监听器类，实现 pers.acp.spring.boot.interfaces.Listener 接口
- （10）参考 test:testspringboot 模块,pers.acp.test.application.test 包中有 tcp、udp 服务端开发demo，并在 application-dev.xml 中增加相应配置
- （11）如有需要，可选择引入 acp-file、acp-message、acp-spring-boot-starter-ftp 等包
##### 2. 配置说明
- 定制开发的 api 接口，开启 swagger 文档
```yaml
acp:
  swagger:
    enabled: true
```

- 配置定时任务
```yaml
acp:
  schedule:
    cron:
      task1: 0 0/1 * * * ?
```
key-value 形式（可配置多个），其中 task1 为任务的 beanName；0 0/1 * * * ? 为定时执行规则cron表达式。

- 输出 controller 日志
```yaml
acp:
  controller-aspect:
    enabled: true        #是否开启controller日志输出，默认true
    no-log-uri-regular:
      - /oauth/.*        #不进行日志输出的 url 正则表达式，可配置多个
```

- tcp 服务端
```yaml
acp:
  tcp-server:
    listeners:
      - name: testSocket                                                     #监听服务名称
        enabled: true                                                        #是否启用，默认false
        keepAlive: false                                                     #是否为长连接，默认false；TCP服务有效
        idletime: 10000                                                      #连接进入空闲状态的等待时间单位毫秒，默认10000；TCP服务有效
        messageDecoder: ""                                                   #粘包拆包解码器 Bean 的类名，默认不设置；TCP服务有效
        threadNumber: 100                                                    #接收报文处理的最大线程数，为0或不设置则使用系统默认线程数；TCP服务有效
        hex: false                                                           #接收报文是否是十六进制机器码，默认false
        port: 9999                                                           #监听端口号
        handleBean: pers.acp.test.application.test.TestTcpHandle             #报文接收处理 Bean 的类名
        responsable: true                                                    #报文是否需要进行原路响应，默认true
        charset: gbk                                                         #服务使用字符集，为空或不设置则系统默认字符集
```

- udp 服务端
```yaml
acp:
  udp-server:
    listeners: 
      - name: testSocket                                                     #监听服务名称
        enabled: true                                                        #是否启用，默认false
        hex: false                                                           #接收报文是否是十六进制机器码，默认false
        port: 9999                                                           #监听端口号
        handleBean: pers.acp.test.application.test.TestTcpHandle             #报文接收处理 Bean 的类名
        responsable: true                                                    #报文是否需要进行原路响应，默认true
        charset: gbk
```

- ftp 服务端
```yaml
acp:
  ftp-server:
    listeners:
      - name: "测试ftp服务器"                                                 #服务名车鞥
        enabled: true                                                        #可空，是否启用，默认false
        port: 221                                                            #服务端口号
        pwd-encrypt-mode: MD5                                                #可空，用户密码加密方式（支持MD5、SHA1、SHA256），默认MD5
        login-failure-delay: 30                                              #可空，默认30
        max-login-failures: 20                                               #可空，默认20
        max-logins: 10                                                       #可空，默认10
        max-anonymous-logins: 20                                             #可空，默认20
        max-threads: 10                                                      #可空，默认10
        default-home-directory: "abs:D:\\个人\\测试ftp"                       #默认根路径
        anonymous-login-enabled: false                                       #可空，是否允许匿名用户登录，默认false
        anonymous-write-permission: false                                    #可空，是否允许匿名用户写操作，默认false
        user-factory-class: pers.acp.test.application.test.TestUserFactory   #用户工厂类
```

- sftp 服务端
```yaml
acp:
  sftp-server:
    listeners:
      - name: "测试sftp服务器"                                                #服务名称
        enabled: true                                                        #可空，是否启用，默认false
        port: 4221                                                           #服务端口号
        host-key-path: "files/resource/key/hostkey"                          #服务器密钥文件路径
        password-auth: true                                                  #可空，是否进行密码登录，默认true
        public-key-auth: false                                               #可空，是否进行证书登录（开启后仅支持证书验证），用户证书只支持openssh生成的密钥，默认false
        key-auth-type: pem                                                   #可空，证书类型（der/pem/ssh），默认pem
        key-auth-mode: RSA                                                   #可空，证书验证模式（RSA/DSA），默认RSA
        default-home-directory: "abs:D:\\个人\\测试ftp"                       #默认根路径
        user-factory-class: pers.acp.test.application.test.TestUserFactory   #用户工厂类
```

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
##### 1. cloud:acp-spring-cloud-starter
原子服务公共模块：
- （1）自定义程序入口注解
- （2）oauth2.0 资源服务配置、客户端服务配置及远程单点认证机制
- （3）自定义 feign 并发策略、自定义 feign 请求拦截
- （4）hystrix 断路器
- （5）封装日志服务客户端，发送日志消息至 kafka
- （6）zipkin 链路追踪客户端
- （7）自定义 PropertySourceLocator，实现自定义配置中心客户端
##### 2. test:cloud:admin-server 
###### 2.1 可视化监控，监控服务状态、信息聚合
|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 后台监控管理首页        |
| /hystrix              | 断路信息监控            |
###### 2.2 zipkin 链路追踪
- 服务端
> 从SpringCloud2.0 以后，官方已经不支持自定义服务，官方只提供编译好的jar包供用户使用。可以自行使用多种方式部署zipkin服务，并采用elasticsearch作为zipkin的数据存储器。
- 客户端（cloud中其他需要监控链路的服务，admin-server、eureka-server、gateway-server 除外）
> - 依赖 cloud:acp-spring-cloud-starter
> - 增加 zipkin 相关配置
> ```yaml
> spring:
>   zipkin:
>     # base-url: http://localhost:9411/
>     sender:
>       type: kafka
>   sleuth:
>     sampler:
>       probability: 1 #样本采集量，默认为0.1，为了测试这里修改为1，正式环境一般使用默认值。
> ```
##### 3. test:cloud:eureka-server 
服务注册发现

|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 服务状态监控界面        |
##### 4. test:cloud:gateway-server 
网关服务
##### 5. test:cloud:oauth-server 
统一认证服务：token 存储于 Redis，user 及 client 信息可扩展配置

|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /oauth/authorize      | 申请授权，basic认证保护      |
| /oauth/token          | 获取token的服务，url中没有client_id和client_secret的，走basic认证保护 |
| /oauth/check_token    | 资源服务器用来校验token，basic认证保护 |
| /oauth/confirm_access | 授权确认，basic认证保护  |
| /oauth/error          | 认证失败，无认证保护     |

[查看认证过程](doc/oauth2.0认证.md)

> test:cloud:oauth-server 中增加 authorization_code 方式配置，详见 pers.acp.spring.cloud.oauth.conf.WebSecurityConfiguration 注释

> 注：使用 authorization_code 方式时，认证请求时需要直接访问 oauth-server 不能通过 gateway

##### 6. test:cloud:config-server
配置服务，统一配置中心，从数据库读取配置信息
##### 7. test:cloud:log-server
日志服务，使用 kafka 作为日志消息队列
##### 8. test:cloud:helloworld 
原子服务，分别调用 hello 和 world
##### 9. test:cloud:hello 
原子服务
##### 10. test:cloud:world 
原子服务
### （二）基础中间件环境搭建
基础中间件包括：redis、zookeeper、kafka、kafka-manager、elasticsearch、kibana、logstash、zipkin、zipkin-dependencies、zoonavigator-api、zoonavigator-web、prometheus、grafana、setup_grafana_datasource
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
> - docker-compose 文件：cloud/dockerfile/docker-compose-base.yml

##### 1. zipkin
http://127.0.0.1:9411
![Architecture diagram](doc/images/zipkin.png)
##### 2. kafka-manager
http://127.0.0.1:9000
![Architecture diagram](doc/images/kafka-manager.png)
##### 3. zoonavigator
http://127.0.0.1:8004
![Architecture diagram](doc/images/zoonavigator.png)
##### 4. prometheus
http://127.0.0.1:9090
![Architecture diagram](doc/images/prometheus.png)
##### 5. kibana
http://127.0.0.1:5601
![Architecture diagram](doc/images/kibana.png)
### （三）组件开发
##### 全局说明
> - 统一注入 pers.acp.spring.boot.interfaces.LogAdapter 进行日志记录
##### 1. 可视化监控
> - test:cloud:admin-server
> - （1）无需改动代码
> - （2）修改 yml 配置即可
![Architecture diagram](doc/images/admin-server.png)
##### 2. 服务注册发现（支持高可用eureka集群）
> - test:cloud:eureka-server
> - （1）无需改动代码
> - （2）修改 yml 配置即可
##### 3. 统一配置管理
> - test:cloud:config-server
> - （1）依赖 cloud:acp-spring-cloud-starter
> - （2）额外功能根据实际需求自定义
> - （3）修改 yml 配置即可
##### 4. 网关服务
> - test:cloud:gateway-server
> - （1）需自定义限流策略（需依赖 Redis）
> - （2）修改 yml 进行路由配置；若没有 Redis 请不要配置限流策略
##### 5. 认证服务
> - test:cloud:oauth-server
> - （1）依赖 cloud:acp-spring-cloud-starter，按需依赖 org.springframework.boot:spring-boot-starter-data-redis
> - （2）入口类增加注解 @AcpCloudOauthServerApplication
> - （3）配置中增加
>   ```yaml
>   acp:
>     cloud:
>       oauth:
>         oauth-server: true
>   ```
> - （3）需定制 UserPasswordEncoder 用户密码编码器，配置进 WebSecurityConfiguration
> - （4）需定制用户（信息、角色、权限）初始化和查询方式 SecurityUserDetailsService，配置进 AuthorizationServerConfiguration
> - （5）需定制客户端（信息）初始化和查询方式 SecurityClientDetailsService，配置进 AuthorizationServerConfiguration
> - （6）token 持久化方式为 Redis，配置在 AuthorizationServerConfiguration；若没有 Redis 可根据注释持久化到内存，也可自行开发其他持久化方式
##### 6. 日志服务（依赖 kafka）
> - （1）依赖 cloud:acp-spring-cloud-starter
> - （2）入口类增加注解 @AcpCloudAtomApplication
> - （3）如需自定义日志消息处理，新增Bean实现 pers.acp.spring.cloud.log.consumer.LogProcess 接口，并且增加 @Primary 注解
> - （4）根据各服务配置的日志类型（默认为"ALL"），在 logback-spring.xml 中参照 ALL 和 ALL-LOGSTASH 进行配置
> -     a. 配置两个 appender（一个输出到本地文件，一个输出到logstash；单独配置的目的是为了将不同类型的日志写入不同名称的文件并在elasticsearch中创建不同的索引）
> -     b. 之后再配置一个 logger （name属性为某个日志类型）,包含之前配置的两个 appender
> -     c. 强烈建议 logback-spring.xml 中配置的本地日志文件路径需与 yml 中的 logging.path 一致，方便统一管理
> - （5）增加配置
>   ```yaml
>   acp:
>     cloud:
>       log-server:
>         enabled: true #是否开启日志服务
>   ```
##### 7. 原子服务
> - （1）依赖 cloud:acp-spring-cloud-starter
> - （2）参考 四、开发 SpringBoot 应用
> - （3）原子服务即 SpringBoot 应用，引入额外的 spring-cloud 包，并在 yml 中增加相应配置
> - （4）参考 test:cloud:hello、test:cloud:world、test:cloud:helloworld，入口类增加注解 @AcpCloudAtomApplication
> - （5）如果存在日志服务，则需进行配置
>    ```yaml
>    acp:
>      cloud:
>        log-server:
>          client:
>            enabled: true #是否启用日志服务
>            log-type: ALL #当前服务的日志类型，默认ALL，也自定义；自定义的类型需要在日志服务中参照ALL配置appender和logger
>    ```
> - （6）如果不存在日志服务，需要排除依赖
>    ```groovy
>    exclude group: 'org.springframework.cloud', module: 'spring-cloud-starter-stream-kafka'
>    ```
> - （7）可自定义token验证异常（新建 Bean 实现 org.springframework.security.web.AuthenticationEntryPoint 接口）和权限异常（新建 Bean 实现 org.springframework.security.web.access.AccessDeniedHandler）返回信息，若有多个实现接口的 Bean，则需要通过如下配置显示指定
>    ```yaml    
>   acp:
>     cloud:
>       oauth:
>         auth-exception-entry-point: AuthExceptionEntryPoint         #自定义 token 异常 bean 名称
>         access-denied-handler: CustomAccessDeniedHandler            #自定义权限异常 bean 名称
>    ```
> - （8）如有特殊需要不进行认证的url（例如"/customer"），则增加resource-server-permit-all-path配置；如有需要进行认证的url（例如"/customer2"），则增加resource-server-security-path配置
>    ```yaml    
>   acp:
>     cloud:
>       oauth:
>         resource-server-permit-all-path: 
>           - /customer
>         resource-server-security-path:
>           - /customer2
>    ```
> - （9）如果原子服务不需要加入统一认证体系中，即不需要进行访问权限验证，则增加配置
>    ```yaml    
>   acp:
>     cloud:
>       oauth:
>         resource-server: false #是否是资源服务器
>    ```
> - （10）特别需要注意：
     即使不需要加入统一认证体系中，如果请求的 header 中包含 Authorization， oauth2 还是会进行身份认证！
     所以仍然需要正确进行如下配置，否则 oauth2 进行身份认证时将会抛出异常
     com.netflix.discovery.shared.transport.TransportException: Cannot execute request on any known server
     或调用端身份认证失败：status 401
>    ```yaml    
>   security:
>     oauth2:
>       client:
>         client-id: ***
>         client-secret: ***
>       resource:
>         token-info-uri: http://oauth2-server/oauth/check_token
>    ```
## 六、打包为 docker 镜像
自行编写 Dockerfile，使用命令单独执行或使用 docker-compose 批量执行，请自行百度