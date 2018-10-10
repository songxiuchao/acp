package pers.acp.test.application.listener;

import org.springframework.stereotype.Component;
import pers.acp.ftp.InitFtpServer;
import pers.acp.ftp.InitSFtpServer;
import pers.acp.springboot.core.daemon.DaemonServiceManager;
import pers.acp.springboot.core.interfaces.IListener;
import pers.acp.webservice.InitWebService;

/**
 * @author zhangbin by 28/09/2018 14:41
 * @since JDK1.8
 */
@Component
public class StartUpListener implements IListener {

    @Override
    public void startListener() {
        InitWebService.publishWebService();
        DaemonServiceManager.addAllService(InitFtpServer.startFtpServer());
        DaemonServiceManager.addAllService(InitSFtpServer.startSFtpServer());
    }

    @Override
    public void stopListener() {

    }

}
