package pers.acp.spring.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 定时调度配置
 *
 * @author zhangbin by 2018-1-20 21:08
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.schedule")
class ScheduleConfiguration {

    /**
     * cron expression list
     * key => bean name
     * value => cron
     */
    var cron: MutableMap<String, String> = mutableMapOf()

}
