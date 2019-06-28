package pers.acp.spring.cloud.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pers.acp.spring.cloud.log.LogConstant;

/**
 * 日志服务客户端配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.cloud.log-server.client")
public class LogServerClientConfiguration {

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    private boolean enabled = false;

    /**
     * log type
     * default "ALL"
     */
    private String logType = LogConstant.DEFAULT_TYPE;

}
