package pers.acp.spring.boot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 核心配置信息
 *
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

    /**
     * 延迟删除文件等待时间（单位毫秒）
     */
    private long deleteFileWaitTime = 1200000;

    /**
     * 绝对路径前缀
     */
    private String absPathPrefix = "abs:";

    /**
     * 用户路径前缀
     */
    private String userPathPrefix = "user:";

    /**
     * 字体文件夹路径
     */
    private String fontPath = "files/resource/font";

}
