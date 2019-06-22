package pers.acp.spring.boot.ftp.init;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import pers.acp.ftp.InitFtpServer;
import pers.acp.ftp.conf.FTPConfig;
import pers.acp.ftp.user.UserFactory;
import pers.acp.spring.boot.base.BaseInitialization;
import pers.acp.spring.boot.daemon.DaemonServiceManager;
import pers.acp.spring.boot.ftp.conf.FtpServerConfiguration;

import java.util.List;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Component
@ConditionalOnClass(InitFtpServer.class)
public class FtpServerInitialization extends BaseInitialization {

    private final FtpServerConfiguration ftpServerConfiguration;

    private final List<UserFactory> userFactoryList;

    public FtpServerInitialization(FtpServerConfiguration ftpServerConfiguration, List<UserFactory> userFactoryList) {
        this.ftpServerConfiguration = ftpServerConfiguration;
        this.userFactoryList = userFactoryList;
    }

    @Override
    public String getName() {
        return "ftp server setup server";
    }

    @Override
    public void start() {
        FTPConfig ftpConfig = new FTPConfig();
        ftpConfig.setListens(ftpServerConfiguration.getListeners());
        DaemonServiceManager.addAllService(InitFtpServer.startFtpServer(ftpConfig, userFactoryList));
    }

    @Override
    public void stop() {

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
