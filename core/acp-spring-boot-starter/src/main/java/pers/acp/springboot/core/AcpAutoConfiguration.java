package pers.acp.springboot.core;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author zhangbin by 2018-1-15 0:37
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.springboot.core")
@ServletComponentScan({"pers.acp.springboot.core"})
public class AcpAutoConfiguration {

    /**
     * 注册定时任务容器实例
     *
     * @return 线程池调度实例
     */
    @Primary
    @Bean("acpThreadPoolTaskScheduler")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
