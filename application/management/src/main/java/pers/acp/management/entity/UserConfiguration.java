package pers.acp.management.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-17 16:23
 * @since JDK1.8
 */
@Entity
@Table(name = "t_user_configuration")
public class UserConfiguration {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public int getSettingsNavbar() {
        return settingsNavbar;
    }

    public void setSettingsNavbar(int settingsNavbar) {
        this.settingsNavbar = settingsNavbar;
    }

    public int getSettingsSidebar() {
        return settingsSidebar;
    }

    public void setSettingsSidebar(int settingsSidebar) {
        this.settingsSidebar = settingsSidebar;
    }

    public int getSettingsBreadcrumbs() {
        return settingsBreadcrumbs;
    }

    public void setSettingsBreadcrumbs(int settingsBreadcrumbs) {
        this.settingsBreadcrumbs = settingsBreadcrumbs;
    }

    public int getSettingsAddContainer() {
        return settingsAddContainer;
    }

    public void setSettingsAddContainer(int settingsAddContainer) {
        this.settingsAddContainer = settingsAddContainer;
    }

    public int getSettingsHover() {
        return settingsHover;
    }

    public void setSettingsHover(int settingsHover) {
        this.settingsHover = settingsHover;
    }

    public int getSettingsCompact() {
        return settingsCompact;
    }

    public void setSettingsCompact(int settingsCompact) {
        this.settingsCompact = settingsCompact;
    }

    public int getSettingsHighlight() {
        return settingsHighlight;
    }

    public void setSettingsHighlight(int settingsHighlight) {
        this.settingsHighlight = settingsHighlight;
    }

    public int getSettingsUseTabs() {
        return settingsUseTabs;
    }

    public void setSettingsUseTabs(int settingsUseTabs) {
        this.settingsUseTabs = settingsUseTabs;
    }

    public String getSkinColorpicker() {
        return skinColorpicker;
    }

    public void setSkinColorpicker(String skinColorpicker) {
        this.skinColorpicker = skinColorpicker;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    private String id;

    @Column(length = 36, nullable = false)
    private String userid;

    @Column(length = 36, nullable = false)
    private String appid;

    private String skinColorpicker;

    private int settingsNavbar;

    private int settingsSidebar;

    private int settingsBreadcrumbs;

    private int settingsAddContainer;

    private int settingsHover;

    private int settingsCompact;

    private int settingsHighlight;

    private int settingsUseTabs;

}
