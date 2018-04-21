package pers.acp.springboot.common;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pers.acp.springboot.common.conf.ScheduleConfiguration;

/**
 * @author zhangbin by 2018-1-15 0:37
 * @since JDK1.8
 */
@Configuration
@ComponentScan("pers.acp.springboot.common")
@ServletComponentScan({"pers.acp.springboot.common"})
public class AutoConfiguration {

    @Bean
    public ScheduleConfiguration scheduleConfiguration() {
        return new ScheduleConfiguration();
    }

}
