package pers.acp.springboot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 定时调度配置
 *
 * @author zhangbin by 2018-1-20 21:08
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.schedule")
public class ScheduleConfiguration {

    public Map<String, String> getCrons() {
        return crons;
    }

    public void setCrons(Map<String, String> crons) {
        this.crons = crons;
    }

    /**
     * crons expression list
     * key => bean name
     * value => coron
     */
    private Map<String, String> crons = new HashMap<>();

}
