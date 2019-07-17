package pers.acp.spring.cloud.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志服务服务端配置
 *
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

    /**
     * 消费日志消息的组id，多个日志服务使用相同的组id，能够保证日志消息不被重复消费
     */
    private String groupId = "acp_cloud_log_server_group_id";

    /**
     * 当前服务是否是日志服务
     */
    private boolean enabled = false;

    /**
     * 日志消息的topic名称（队列名称）
     */
    private String destination = "acp_cloud_log_server_message_topic";

}
