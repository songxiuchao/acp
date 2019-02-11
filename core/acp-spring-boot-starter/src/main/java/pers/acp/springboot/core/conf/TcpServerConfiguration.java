package pers.acp.springboot.core.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "acp.tcp-server")
public class TcpServerConfiguration {

    public List<SocketListenerConfiguration> getListeners() {
        return listeners;
    }

    public void setListeners(List<SocketListenerConfiguration> listeners) {
        this.listeners = listeners;
    }

    private List<SocketListenerConfiguration> listeners = new ArrayList<>();

}
