package pers.acp.spring.cloud.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 鉴权相关配置
 *
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

    public List<String> getResourceServerSecurityPath() {
        return resourceServerSecurityPath;
    }

    public void setResourceServerSecurityPath(List<String> resourceServerSecurityPath) {
        this.resourceServerSecurityPath = resourceServerSecurityPath;
    }

    public String getAccessDeniedHandler() {
        return accessDeniedHandler;
    }

    public void setAccessDeniedHandler(String accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    public String getAuthExceptionEntryPoint() {
        return authExceptionEntryPoint;
    }

    public void setAuthExceptionEntryPoint(String authExceptionEntryPoint) {
        this.authExceptionEntryPoint = authExceptionEntryPoint;
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

    /**
     * customer auth exception entry point bean name
     */
    private String authExceptionEntryPoint;

    /**
     * customer access denied handler
     */
    private String accessDeniedHandler;

    /**
     * permitAll path, effective when resourceServer=true
     */
    private List<String> resourceServerPermitAllPath = new ArrayList<>();

    /**
     * security path, effective when resourceServer=true
     */
    private List<String> resourceServerSecurityPath = new ArrayList<>();

}
