package pers.acp.ftp.config;


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

    @XStreamImplicit(itemFieldName = "listen")
    private List<Listen> listens;

    public class Listen {

        public String getName() {
            return name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getPort() {
            return port;
        }

        public boolean isAnonymousLoginEnabled() {
            return anonymousLoginEnabled;
        }

        public String getPwdEncryptMode() {
            return pwdEncryptMode.toUpperCase();
        }

        public int getLoginFailureDelay() {
            return loginFailureDelay;
        }

        public int getMaxLoginFailures() {
            return maxLoginFailures;
        }

        public int getMaxLogins() {
            return maxLogins;
        }

        public int getMaxAnonymousLogins() {
            return maxAnonymousLogins;
        }

        public int getMaxThreads() {
            return maxThreads;
        }

        public String getDefaultHomeDirectory() {
            return defaultHomeDirectory;
        }

        public boolean isAnonymousWritePermission() {
            return anonymousWritePermission;
        }

        public String getUserFactoryClass() {
            return userFactoryClass;
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
