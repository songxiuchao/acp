package pers.acp.springboot.common.component;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author zhangbin by 2018-1-17 14:01
 * @since JDK1.8
 */
@Component
public class AcpCommonBeansDefine {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}
