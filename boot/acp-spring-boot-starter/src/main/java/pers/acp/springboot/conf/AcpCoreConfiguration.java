package pers.acp.springboot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhang by 20/06/2019
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.core")
public class AcpCoreConfiguration {

    public long getDeleteFileWaitTime() {
        return deleteFileWaitTime;
    }

    public void setDeleteFileWaitTime(long deleteFileWaitTime) {
        this.deleteFileWaitTime = deleteFileWaitTime;
    }

    public String getAbsPathPrefix() {
        return absPathPrefix;
    }

    public void setAbsPathPrefix(String absPathPrefix) {
        this.absPathPrefix = absPathPrefix;
    }

    public String getUserPathPrefix() {
        return userPathPrefix;
    }

    public void setUserPathPrefix(String userPathPrefix) {
        this.userPathPrefix = userPathPrefix;
    }

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }

    private long deleteFileWaitTime = 1200000;

    private String absPathPrefix = "abs:";

    private String userPathPrefix = "user:";

    private String fontPath = "files/resource/font";

}
