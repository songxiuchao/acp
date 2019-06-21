package pers.acp.ftp;

import pers.acp.core.CommonTools;
import pers.acp.core.interfaces.IDaemonService;
import pers.acp.core.log.LogFactory;
import pers.acp.ftp.conf.SFTPConfig;
import pers.acp.ftp.server.SFTPServer;
import pers.acp.ftp.user.UserFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * 启动SFTP服务
 */
public class InitSFtpServer {

    /**
     * 日志对象
     */
    private static final LogFactory log = LogFactory.getInstance(InitSFtpServer.class);

    public static List<IDaemonService> startSFtpServer() {
        log.info("start sftp servers ...");
        List<IDaemonService> sftpServers = new ArrayList<>();
        try {
            SFTPConfig sftpConfig = SFTPConfig.getInstance();
            sftpServers = doStart(sftpConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("start sftp servers finished!");
        }
        return sftpServers;
    }

    public static List<IDaemonService> startSFtpServer(SFTPConfig sftpConfig) {
        log.info("start sftp servers ...");
        List<IDaemonService> sftpServers = new ArrayList<>();
        try {
            sftpServers = doStart(sftpConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("start sftp servers finished!");
        }
        return sftpServers;
    }

    private static List<IDaemonService> doStart(SFTPConfig sftpConfig) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<IDaemonService> sftpServers = new ArrayList<>();
        if (sftpConfig != null) {
            List<SFTPConfig.Listen> listens = sftpConfig.getListens();
            if (listens != null) {
                for (SFTPConfig.Listen listen : listens) {
                    if (listen.isEnabled()) {
                        String classname = listen.getUserFactoryClass();
                        if (!CommonTools.isNullStr(classname)) {
                            UserFactory userFactory = (UserFactory) Class.forName(classname).getDeclaredConstructor().newInstance();
                            SFTPServer sftpServer = new SFTPServer(userFactory.generateSFtpUserList(), listen);
                            Thread sub = new Thread(sftpServer);
                            sub.setDaemon(true);
                            sub.start();
                            sftpServers.add(sftpServer);
                            log.info("start sftp server success [" + listen.getName() + "] port:" + listen.getPort());
                        } else {
                            log.info("start sftp server failed [" + listen.getName() + "] : user factory class is null");
                        }
                    } else {
                        log.info("sftp server is disabled [" + listen.getName() + "]");
                    }
                }
            }
        }
        return sftpServers;
    }

}
