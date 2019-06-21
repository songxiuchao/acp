package pers.acp.ftp;

import pers.acp.core.CommonTools;
import pers.acp.core.interfaces.IDaemonService;
import pers.acp.core.log.LogFactory;
import pers.acp.ftp.conf.FTPConfig;
import pers.acp.ftp.server.FTPServer;
import pers.acp.ftp.user.UserFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * 初始化FTP服务器
 */
public class InitFtpServer {

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
        } finally {
            log.info("start ftp servers finished!");
        }
        return ftpServers;
    }

    public static List<IDaemonService> startFtpServer(FTPConfig ftpConfig) {
        log.info("start ftp servers ...");
        List<IDaemonService> ftpServers = new ArrayList<>();
        try {
            ftpServers = doStart(ftpConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("start ftp servers finished!");
        }
        return ftpServers;
    }

    private static List<IDaemonService> doStart(FTPConfig ftpConfig) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<IDaemonService> ftpServers = new ArrayList<>();
        if (ftpConfig != null) {
            List<FTPConfig.Listen> listens = ftpConfig.getListens();
            if (listens != null) {
                for (FTPConfig.Listen listen : listens) {
                    if (listen.isEnabled()) {
                        String classname = listen.getUserFactoryClass();
                        if (!CommonTools.isNullStr(classname)) {
                            UserFactory userFactory = (UserFactory) Class.forName(classname).getDeclaredConstructor().newInstance();
                            FTPServer ftpServer = new FTPServer(userFactory.generateFtpUserList(), listen);
                            Thread sub = new Thread(ftpServer);
                            sub.setDaemon(true);
                            sub.start();
                            ftpServers.add(ftpServer);
                            log.info("start ftp server success [" + listen.getName() + "] port:" + listen.getPort());
                        } else {
                            log.info("start ftp server failed [" + listen.getName() + "] : user factory class is null");
                        }
                    } else {
                        log.info("ftp server is disabled [" + listen.getName() + "]");
                    }
                }
            }
        }
        return ftpServers;
    }

}
