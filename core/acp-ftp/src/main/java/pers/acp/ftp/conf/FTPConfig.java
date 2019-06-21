package pers.acp.ftp.conf;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.config.base.BaseXML;
import pers.acp.core.log.LogFactory;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP服务配置
 */
@XStreamAlias("ftp-config")
public class FTPConfig extends BaseXML {

    private static final LogFactory log = LogFactory.getInstance(FTPConfig.class);

    public static FTPConfig getInstance() {
        try {
            return (FTPConfig) Load(FTPConfig.class);
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
        private boolean enabled;

        @XStreamAsAttribute
        @XStreamAlias("port")
        private int port;

        @XStreamAsAttribute
        @XStreamAlias("anonymousLoginEnabled")
        private boolean anonymousLoginEnabled;

        @XStreamAsAttribute
        @XStreamAlias("pwdEncryptMode")
        private String pwdEncryptMode;

        @XStreamAsAttribute
        @XStreamAlias("loginFailureDelay")
        private int loginFailureDelay;

        @XStreamAsAttribute
        @XStreamAlias("maxLoginFailures")
        private int maxLoginFailures;

        @XStreamAsAttribute
        @XStreamAlias("maxLogins")
        private int maxLogins;

        @XStreamAsAttribute
        @XStreamAlias("maxAnonymousLogins")
        private int maxAnonymousLogins;

        @XStreamAsAttribute
        @XStreamAlias("maxThreads")
        private int maxThreads;

        @XStreamAsAttribute
        @XStreamAlias("defaultHomeDirectory")
        private String defaultHomeDirectory;

        @XStreamAsAttribute
        @XStreamAlias("anonymousWritePermission")
        private boolean anonymousWritePermission;

        @XStreamAsAttribute
        @XStreamAlias("userFactoryClass")
        private String userFactoryClass;

    }

}
