package pers.acp.spring.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 核心配置信息
 *
 * @author zhang by 20/06/2019
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.core")
class AcpCoreConfiguration {

    /**
     * 延迟删除文件等待时间（单位毫秒）
     */
    var deleteFileWaitTime: Long = 1200000

    /**
     * 绝对路径前缀
     */
    var absPathPrefix = "abs:"

    /**
     * 用户路径前缀
     */
    var userPathPrefix = "user:"

    /**
     * 字体文件夹路径
     */
    var fontPath = "files/resource/font"

}
