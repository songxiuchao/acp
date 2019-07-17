package pers.acp.spring.cloud.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 日志服务服务端配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.cloud.log-server")
class LogServerConfiguration {

    /**
     * 消费日志消息的组id，多个日志服务使用相同的组id，能够保证日志消息不被重复消费
     */
    var groupId = "acp_cloud_log_server_group_id"

    /**
     * 当前服务是否是日志服务
     */
    var enabled = false

    /**
     * 日志消息的topic名称（队列名称）
     */
    var destination = "acp_cloud_log_server_message_topic"

}
