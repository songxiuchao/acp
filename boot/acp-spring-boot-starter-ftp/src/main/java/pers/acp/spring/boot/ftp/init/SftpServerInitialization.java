package pers.acp.spring.boot.ftp.init;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import pers.acp.ftp.InitSFtpServer;
import pers.acp.ftp.conf.SFTPConfig;
import pers.acp.ftp.user.UserFactory;
import pers.acp.spring.boot.base.BaseInitialization;
import pers.acp.spring.boot.daemon.DaemonServiceManager;
import pers.acp.spring.boot.ftp.conf.SftpServerConfiguration;

import java.util.List;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Component
@ConditionalOnClass(InitSFtpServer.class)
public class SftpServerInitialization extends BaseInitialization {

    private final SftpServerConfiguration sftpServerConfiguration;

    private final List<UserFactory> userFactoryList;

    public SftpServerInitialization(SftpServerConfiguration sftpServerConfiguration, List<UserFactory> userFactoryList) {
        this.sftpServerConfiguration = sftpServerConfiguration;
        this.userFactoryList = userFactoryList;
    }

    @Override
    public String getName() {
        return "sftp server setup server";
    }

    @Override
    public void start() {
        SFTPConfig sftpConfig = new SFTPConfig();
        sftpConfig.setListens(sftpServerConfiguration.getListeners());
        DaemonServiceManager.addAllService(InitSFtpServer.startSFtpServer(sftpConfig, userFactoryList));
    }

    @Override
    public void stop() {

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
