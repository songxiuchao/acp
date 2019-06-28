package pers.acp.spring.boot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller 切面日志配置
 *
 * @author zhangbin by 14/04/2018 00:36
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.controller-aspect")
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
     * 不进行日志记录的 url 正则表达式
     * no log uri regexes
     */
    private List<String> noLogUriRegexes = new ArrayList<>();

}
