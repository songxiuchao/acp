package pers.acp.springcloud.common.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhang by 14/01/2019 17:30
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.cloud.oauth")
public class AcpOauthConfiguration {

    public boolean isOauthServer() {
        return oauthServer;
    }

    public void setOauthServer(boolean oauthServer) {
        this.oauthServer = oauthServer;
    }

    /**
     * is oauth server
     * default false
     */
    private boolean oauthServer = false;

}
