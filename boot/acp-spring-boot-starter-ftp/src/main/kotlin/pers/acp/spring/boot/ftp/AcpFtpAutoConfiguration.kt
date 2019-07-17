package pers.acp.spring.boot.ftp

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import pers.acp.ftp.InitFtpServer
import pers.acp.ftp.InitSftpServer
import pers.acp.spring.boot.ftp.conf.SftpServerConfiguration
import pers.acp.spring.boot.ftp.conf.FtpServerConfiguration

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.spring.boot.ftp")
class AcpFtpAutoConfiguration {

    @Bean
    @ConditionalOnClass(InitFtpServer::class)
    @ConfigurationProperties(prefix = "acp.ftp-server")
    fun acpFtpServerConfiguration() = FtpServerConfiguration()

    @Bean
    @ConditionalOnClass(InitSftpServer::class)
    @ConfigurationProperties(prefix = "acp.sftp-server")
    fun acpSftpServerConfiguration() = SftpServerConfiguration()

}
