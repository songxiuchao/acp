package pers.acp.springcloud.common.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 14/01/2019 17:30
 * @since JDK 11
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "acp.cloud.oauth")
public class AcpOauthConfiguration {

    public boolean isOauthServer() {
        return oauthServer;
    }

    public void setOauthServer(boolean oauthServer) {
        this.oauthServer = oauthServer;
    }

    public List<String> getResourceServerPermitAllPath() {
        return resourceServerPermitAllPath;
    }

    public void setResourceServerPermitAllPath(List<String> resourceServerPermitAllPath) {
        this.resourceServerPermitAllPath = resourceServerPermitAllPath;
    }

    /**
     * is oauth server
     * default false
     */
    private boolean oauthServer = false;

    private List<String> resourceServerPermitAllPath = new ArrayList<>();

}
