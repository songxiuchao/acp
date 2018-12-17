package pers.acp.springboot.core.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * controller 切面日志配置
 *
 * @author zhangbin by 14/04/2018 00:36
 * @since JDK 11
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

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 不记录日志的URL正则表达式
     */
    private List<String> noLogUriRegexes = new ArrayList<>();

}
