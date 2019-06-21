package pers.acp.ftp.conf;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public class FTPListener {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAnonymousLoginEnabled() {
        return anonymousLoginEnabled;
    }

    public void setAnonymousLoginEnabled(boolean anonymousLoginEnabled) {
        this.anonymousLoginEnabled = anonymousLoginEnabled;
    }

    public String getPwdEncryptMode() {
        return pwdEncryptMode;
    }

    public void setPwdEncryptMode(String pwdEncryptMode) {
        this.pwdEncryptMode = pwdEncryptMode;
    }

    public int getLoginFailureDelay() {
        return loginFailureDelay;
    }

    public void setLoginFailureDelay(int loginFailureDelay) {
        this.loginFailureDelay = loginFailureDelay;
    }

    public int getMaxLoginFailures() {
        return maxLoginFailures;
    }

    public void setMaxLoginFailures(int maxLoginFailures) {
        this.maxLoginFailures = maxLoginFailures;
    }

    public int getMaxLogins() {
        return maxLogins;
    }

    public void setMaxLogins(int maxLogins) {
        this.maxLogins = maxLogins;
    }

    public int getMaxAnonymousLogins() {
        return maxAnonymousLogins;
    }

    public void setMaxAnonymousLogins(int maxAnonymousLogins) {
        this.maxAnonymousLogins = maxAnonymousLogins;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public String getDefaultHomeDirectory() {
        return defaultHomeDirectory;
    }

    public void setDefaultHomeDirectory(String defaultHomeDirectory) {
        this.defaultHomeDirectory = defaultHomeDirectory;
    }

    public boolean isAnonymousWritePermission() {
        return anonymousWritePermission;
    }

    public void setAnonymousWritePermission(boolean anonymousWritePermission) {
        this.anonymousWritePermission = anonymousWritePermission;
    }

    public String getUserFactoryClass() {
        return userFactoryClass;
    }

    public void setUserFactoryClass(String userFactoryClass) {
        this.userFactoryClass = userFactoryClass;
    }

    @XStreamAsAttribute
    @XStreamAlias("name")
    private String name;

    @XStreamAsAttribute
    @XStreamAlias("enabled")
    private boolean enabled = false;

    @XStreamAsAttribute
    @XStreamAlias("port")
    private int port;

    @XStreamAsAttribute
    @XStreamAlias("anonymousLoginEnabled")
    private boolean anonymousLoginEnabled = false;

    @XStreamAsAttribute
    @XStreamAlias("pwdEncryptMode")
    private String pwdEncryptMode = "MD5";

    @XStreamAsAttribute
    @XStreamAlias("loginFailureDelay")
    private int loginFailureDelay = 30;

    @XStreamAsAttribute
    @XStreamAlias("maxLoginFailures")
    private int maxLoginFailures = 20;

    @XStreamAsAttribute
    @XStreamAlias("maxLogins")
    private int maxLogins = 10;

    @XStreamAsAttribute
    @XStreamAlias("maxAnonymousLogins")
    private int maxAnonymousLogins = 20;

    @XStreamAsAttribute
    @XStreamAlias("maxThreads")
    private int maxThreads = 10;

    @XStreamAsAttribute
    @XStreamAlias("defaultHomeDirectory")
    private String defaultHomeDirectory;

    @XStreamAsAttribute
    @XStreamAlias("anonymousWritePermission")
    private boolean anonymousWritePermission = false;

    @XStreamAsAttribute
    @XStreamAlias("userFactoryClass")
    private String userFactoryClass;

}
