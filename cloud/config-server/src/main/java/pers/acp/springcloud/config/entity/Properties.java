package pers.acp.springcloud.config.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_properties")
public class Properties {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfigApplication() {
        return configApplication;
    }

    public void setConfigApplication(String configApplication) {
        this.configApplication = configApplication;
    }

    public String getConfigProfile() {
        return configProfile;
    }

    public void setConfigProfile(String configProfile) {
        this.configProfile = configProfile;
    }

    public String getConfigLabel() {
        return configLabel;
    }

    public void setConfigLabel(String configLabel) {
        this.configLabel = configLabel;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    private String id;

    /**
     * 对应 spring.application.name
     */
    @Column(nullable = false)
    private String configApplication;

    /**
     * 对应 spring.profiles.active
     */
    @Column(nullable = false)
    private String configProfile;

    /**
     * 分支标签
     */
    @Column(nullable = false)
    private String configLabel;

    /**
     * 配置项键
     */
    @Column(nullable = false)
    private String configKey;

    /**
     * 配置项值
     */
    @Column(nullable = false)
    private String configValue;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private boolean enabled;

}
