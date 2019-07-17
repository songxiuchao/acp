package pers.acp.spring.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Udp 服务端配置
 */
@Component
@ConfigurationProperties(prefix = "acp.udp-server")
class UdpServerConfiguration {

    /**
     * Socket 监听列表
     */
    var listeners: MutableList<SocketListenerConfiguration> = mutableListOf()

}
