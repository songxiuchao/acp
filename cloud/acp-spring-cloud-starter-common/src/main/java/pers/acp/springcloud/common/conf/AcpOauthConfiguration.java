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

    public boolean isResourceServer() {
        return resourceServer;
    }

    public void setResourceServer(boolean resourceServer) {
        this.resourceServer = resourceServer;
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

    /**
     * is resource server
     * default true
     */
    private boolean resourceServer = true;

    private List<String> resourceServerPermitAllPath = new ArrayList<>();

}
