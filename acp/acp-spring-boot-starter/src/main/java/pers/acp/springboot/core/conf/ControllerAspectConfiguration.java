package pers.acp.springboot.core.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangbin by 14/04/2018 00:36
 * @since JDK1.8
 */
@ConfigurationProperties(prefix = "controller-aspect")
public class ControllerAspectConfiguration {

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getNoLogUriRegexes() {
        return noLogUriRegexes;
    }

    public void setNoLogUriRegexes(List<String> noLogUriRegexes) {
        this.noLogUriRegexes = noLogUriRegexes;
    }

    private boolean enabled = true;

    private List<String> noLogUriRegexes = new ArrayList<>();

}
