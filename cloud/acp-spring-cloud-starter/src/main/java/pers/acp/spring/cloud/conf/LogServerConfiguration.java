package pers.acp.spring.cloud.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.cloud.log-server")
public class LogServerConfiguration {

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    private String groupId = "acp_cloud_log_server_group_id";

    private boolean enabled = false;

    private String destination = "acp_cloud_log_server_logmessage_topic";

}
