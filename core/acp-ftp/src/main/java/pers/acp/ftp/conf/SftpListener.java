package pers.acp.ftp.conf;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public class SftpListener {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getEnabled() {
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

    public String getHostKeyPath() {
        return hostKeyPath;
    }

    public void setHostKeyPath(String hostKeyPath) {
        this.hostKeyPath = hostKeyPath;
    }

    public boolean getPasswordAuth() {
        return passwordAuth;
    }

    public void setPasswordAuth(boolean passwordAuth) {
        this.passwordAuth = passwordAuth;
    }

    public boolean getPublicKeyAuth() {
        return publicKeyAuth;
    }

    public void setPublicKeyAuth(boolean publicKeyAuth) {
        this.publicKeyAuth = publicKeyAuth;
    }

    public String getKeyAuthType() {
        return keyAuthType;
    }

    public void setKeyAuthType(String keyAuthType) {
        this.keyAuthType = keyAuthType;
    }

    public String getKeyAuthMode() {
        return keyAuthMode;
    }

    public void setKeyAuthMode(String keyAuthMode) {
        this.keyAuthMode = keyAuthMode;
    }

    public String getDefaultHomeDirectory() {
        return defaultHomeDirectory;
    }

    public void setDefaultHomeDirectory(String defaultHomeDirectory) {
        this.defaultHomeDirectory = defaultHomeDirectory;
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
    @XStreamAlias("hostKeyPath")
    private String hostKeyPath;

    @XStreamAsAttribute
    @XStreamAlias("passwordAuth")
    private boolean passwordAuth = true;

    @XStreamAsAttribute
    @XStreamAlias("publicKeyAuth")
    private boolean publicKeyAuth = false;

    @XStreamAsAttribute
    @XStreamAlias("keyAuthType")
    private String keyAuthType = "pem";

    @XStreamAsAttribute
    @XStreamAlias("keyAuthMode")
    private String keyAuthMode = "RSA";

    @XStreamAsAttribute
    @XStreamAlias("defaultHomeDirectory")
    private String defaultHomeDirectory;

    @XStreamAsAttribute
    @XStreamAlias("userFactoryClass")
    private String userFactoryClass;

}
