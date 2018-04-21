package pers.acp.management.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-17 17:24
 * @since JDK1.8
 */
@Entity
@Table(name = "t_online_user", indexes = {
        @Index(columnList = "lastActiveTime,appid,userid"),
        @Index(columnList = "lastActiveTime,userid")
})
public class OnlineUser {

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

    public String getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(String lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    private String id;

    @Column(length = 36, nullable = false)
    private String userid = "";

    @Column(length = 36, nullable = false)
    private String appid = "";

    @Column(length = 19, nullable = false)
    private String lastActiveTime = "";

    @Column(nullable = false)
    private String lastLoginIp = "";

}
