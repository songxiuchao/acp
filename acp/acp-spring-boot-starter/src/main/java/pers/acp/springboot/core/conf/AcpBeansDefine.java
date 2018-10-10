package pers.acp.springboot.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author zhangbin by 2018-1-17 14:01
 * @since JDK1.8
 */
@Component
public class AcpBeansDefine {

    /**
     * 注册定时任务容器实例
     *
     * @return 线程池调度实例
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}
