package pers.acp.spring.boot.ftp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pers.acp.ftp.InitFtpServer;
import pers.acp.ftp.InitSFtpServer;
import pers.acp.spring.boot.ftp.conf.SftpServerConfiguration;
import pers.acp.spring.boot.ftp.conf.FtpServerConfiguration;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.spring.boot.ftp")
public class AcpFtpAutoConfiguration {

    @Bean
    @ConditionalOnClass(InitFtpServer.class)
    @ConfigurationProperties(prefix = "acp.ftp-server")
    public FtpServerConfiguration ftpServerConfiguration() {
        return new FtpServerConfiguration();
    }

    @Bean
    @ConditionalOnClass(InitSFtpServer.class)
    @ConfigurationProperties(prefix = "acp.sftp-server")
    public SftpServerConfiguration sftpServerConfiguration() {
        return new SftpServerConfiguration();
    }

}
