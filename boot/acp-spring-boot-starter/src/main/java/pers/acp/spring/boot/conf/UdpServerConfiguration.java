package pers.acp.spring.boot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Udp 服务端配置
 */
@Component
@ConfigurationProperties(prefix = "acp.udp-server")
public class UdpServerConfiguration {

    public List<SocketListenerConfiguration> getListeners() {
        return listeners;
    }

    public void setListeners(List<SocketListenerConfiguration> listeners) {
        this.listeners = listeners;
    }

    /**
     * Socket 监听列表
     */
    private List<SocketListenerConfiguration> listeners = new ArrayList<>();

}
