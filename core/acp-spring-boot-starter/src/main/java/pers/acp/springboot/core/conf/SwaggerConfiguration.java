package pers.acp.springboot.core.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhang by 14/01/2019 15:07
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.swagger")
public class SwaggerConfiguration {

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private boolean enabled = false;

}
