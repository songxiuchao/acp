package pers.acp.springboot.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pers.acp.springboot.core.tools.PackageTools;

/**
 * @author zhangbin by 2018-1-15 0:37
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.springboot.core")
public class AcpAutoConfiguration {

    /**
     * 注册定时任务容器实例
     *
     * @return 线程池调度实例
     */
    @Primary
    @Bean("acpThreadPoolTaskScheduler")
    @ConditionalOnMissingBean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Primary
    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    @ConditionalOnBean({JacksonProperties.class})
    public ObjectMapper jacksonObjectMapper(JacksonProperties jacksonProperties) {
        return PackageTools.buildJacksonObjectMapper(jacksonProperties);
    }

}
