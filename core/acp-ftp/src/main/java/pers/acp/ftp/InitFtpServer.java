package pers.acp.ftp;

import pers.acp.core.CommonTools;
import pers.acp.core.interfaces.IDaemonService;
import pers.acp.core.log.LogFactory;
import pers.acp.ftp.base.InitServer;
import pers.acp.ftp.conf.FTPConfig;
import pers.acp.ftp.conf.FTPListener;
import pers.acp.ftp.server.FTPServer;
import pers.acp.ftp.user.UserFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * 初始化FTP服务器
 */
public class InitFtpServer extends InitServer {

    /**
     * 日志对象
     */
    private static final LogFactory log = LogFactory.getInstance(InitFtpServer.class);

    public static List<IDaemonService> startFtpServer() {
        log.info("start ftp servers ...");
        List<IDaemonService> ftpServers = new ArrayList<>();
        try {
            FTPConfig ftpConfig = FTPConfig.getInstance();
            ftpServers = doStart(ftpConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("start ftp servers exception: " + e.getMessage());
        }
        return ftpServers;
    }

    public static List<IDaemonService> startFtpServer(FTPConfig ftpConfig, List<UserFactory> userFactoryList) {
        log.info("start ftp servers ...");
        userFactoryList.forEach(InitServer::addUserFactory);
        List<IDaemonService> ftpServers = new ArrayList<>();
        try {
            ftpServers = doStart(ftpConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("start ftp servers exception: " + e.getMessage());
        }
        return ftpServers;
    }

    private static List<IDaemonService> doStart(FTPConfig ftpConfig) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<IDaemonService> ftpServers = new ArrayList<>();
        if (ftpConfig != null) {
            List<FTPListener> listens = ftpConfig.getListens();
            if (listens != null && !listens.isEmpty()) {
                for (FTPListener listen : listens) {
                    if (listen.isEnabled()) {
                        String classname = listen.getUserFactoryClass();
                        if (!CommonTools.isNullStr(classname)) {
                            UserFactory userFactory = getUserFactory(classname);
                            FTPServer ftpServer = new FTPServer(userFactory.generateFtpUserList(), listen);
                            Thread sub = new Thread(ftpServer);
                            sub.setDaemon(true);
                            sub.start();
                            ftpServers.add(ftpServer);
                        } else {
                            log.info("start ftp server failed [" + listen.getName() + "] : user factory class is null");
                        }
                    } else {
                        log.info("ftp server is disabled [" + listen.getName() + "]");
                    }
                }
            } else {
                log.info("No ftp service was found");
            }
        }
        return ftpServers;
    }

}
