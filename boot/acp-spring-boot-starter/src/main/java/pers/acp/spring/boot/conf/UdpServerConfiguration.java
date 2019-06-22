package pers.acp.spring.boot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "acp.udp-server")
public class UdpServerConfiguration {

    public List<SocketListenerConfiguration> getListeners() {
        return listeners;
    }

    public void setListeners(List<SocketListenerConfiguration> listeners) {
        this.listeners = listeners;
    }

    private List<SocketListenerConfiguration> listeners = new ArrayList<>();

}
