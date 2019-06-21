package pers.acp.ftp.conf;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.config.base.BaseXML;
import pers.acp.core.log.LogFactory;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * SFTP服务配置
 */
@XStreamAlias("sftp-config")
public class SFTPConfig extends BaseXML {

    private static final LogFactory log = LogFactory.getInstance(SFTPConfig.class);

    public static SFTPConfig getInstance() {
        try {
            return (SFTPConfig) Load(SFTPConfig.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Listen> getListens() {
        return listens;
    }

    public void setListens(List<Listen> listens) {
        this.listens = listens;
    }

    @XStreamImplicit(itemFieldName = "listen")
    private List<Listen> listens;

    public class Listen {

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

        public String getHostKeyPath() {
            return hostKeyPath;
        }

        public void setHostKeyPath(String hostKeyPath) {
            this.hostKeyPath = hostKeyPath;
        }

        public boolean isPasswordAuth() {
            return passwordAuth;
        }

        public void setPasswordAuth(boolean passwordAuth) {
            this.passwordAuth = passwordAuth;
        }

        public boolean isPublicKeyAuth() {
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
        private boolean enabled;

        @XStreamAsAttribute
        @XStreamAlias("port")
        private int port;

        @XStreamAsAttribute
        @XStreamAlias("hostKeyPath")
        private String hostKeyPath;

        @XStreamAsAttribute
        @XStreamAlias("passwordAuth")
        private boolean passwordAuth;

        @XStreamAsAttribute
        @XStreamAlias("publicKeyAuth")
        private boolean publicKeyAuth;

        @XStreamAsAttribute
        @XStreamAlias("keyAuthType")
        private String keyAuthType;

        @XStreamAsAttribute
        @XStreamAlias("keyAuthMode")
        private String keyAuthMode;

        @XStreamAsAttribute
        @XStreamAlias("defaultHomeDirectory")
        private String defaultHomeDirectory;

        @XStreamAsAttribute
        @XStreamAlias("userFactoryClass")
        private String userFactoryClass;

    }

}
