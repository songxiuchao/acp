package pers.acp.springboot.common.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangbin by 2018-1-20 21:08
 * @since JDK1.8
 */
@ConfigurationProperties(prefix = "schedule")
public class ScheduleConfiguration {

    public Map<String, String> getCrons() {
        return crons;
    }

    public void setCrons(Map<String, String> crons) {
        this.crons = crons;
    }

    private Map<String, String> crons = new HashMap<>();

}
