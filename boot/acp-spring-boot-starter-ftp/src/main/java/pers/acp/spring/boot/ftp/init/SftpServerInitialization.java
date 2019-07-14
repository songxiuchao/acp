package pers.acp.spring.boot.ftp.init;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import pers.acp.ftp.InitSftpServer;
import pers.acp.ftp.conf.SftpConfig;
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
@ConditionalOnClass(InitSftpServer.class)
public class SftpServerInitialization extends BaseInitialization {

    private final SftpServerConfiguration sftpServerConfiguration;

    private final List<UserFactory> userFactoryList;

    public SftpServerInitialization(SftpServerConfiguration sftpServerConfiguration, List<UserFactory> userFactoryList) {
        this.sftpServerConfiguration = sftpServerConfiguration;
        this.userFactoryList = userFactoryList;
    }

    @NotNull
    @Override
    public String getName() {
        return "sftp server setup server";
    }

    /**
     * 启动sftp服务
     */
    @Override
    public void start() {
        SftpConfig sftpConfig = new SftpConfig();
        sftpConfig.setListens(sftpServerConfiguration.getListeners());
        DaemonServiceManager.addAllService(InitSftpServer.startSftpServer(sftpConfig, userFactoryList));
    }

    @Override
    public void stop() {

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
