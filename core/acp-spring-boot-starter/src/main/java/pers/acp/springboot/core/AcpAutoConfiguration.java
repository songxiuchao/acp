package pers.acp.springboot.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
    @ConditionalOnMissingBean(ThreadPoolTaskScheduler.class)
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Primary
    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    @ConditionalOnBean(Jackson2ObjectMapperBuilder.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build();
    }

}
