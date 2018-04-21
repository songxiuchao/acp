package pers.acp.management.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-17 14:56
 * @since JDK1.8
 */
@Entity
@Table(name = "t_application")
public class Application {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebroot() {
        return webroot;
    }

    public void setWebroot(String webroot) {
        this.webroot = webroot;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public int getDbno() {
        return dbno;
    }

    public void setDbno(int dbno) {
        this.dbno = dbno;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCopyrightOwner() {
        return copyrightOwner;
    }

    public void setCopyrightOwner(String copyrightOwner) {
        this.copyrightOwner = copyrightOwner;
    }

    public String getCopyrightBegin() {
        return copyrightBegin;
    }

    public void setCopyrightBegin(String copyrightBegin) {
        this.copyrightBegin = copyrightBegin;
    }

    public String getCopyrightEnd() {
        return copyrightEnd;
    }

    public void setCopyrightEnd(String copyrightEnd) {
        this.copyrightEnd = copyrightEnd;
    }

    public int getDefaultapp() {
        return defaultapp;
    }

    public void setDefaultapp(int defaultapp) {
        this.defaultapp = defaultapp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false)
    private String webroot = "";

    @Column(nullable = false)
    private String appname = "";

    @Column(nullable = false)
    private int dbno;

    @Column(nullable = false)
    private String language = "";

    @Column(nullable = false)
    private String copyrightOwner = "";

    @Column(length = 4, nullable = false)
    private String copyrightBegin = "";

    @Column(length = 4)
    private String copyrightEnd = "";

    @Column(nullable = false)
    private int defaultapp = 0;

    @Column(nullable = false)
    private String version = "";

    @Column(nullable = false)
    private int type = 1;

    @Column(nullable = false)
    private int sort = 0;

}
