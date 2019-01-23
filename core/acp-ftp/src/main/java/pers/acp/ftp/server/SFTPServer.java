package pers.acp.ftp.server;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.util.SecurityUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.PEMGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.sftp.subsystem.SftpSubsystem;
import pers.acp.core.CommonTools;
import pers.acp.core.interfaces.IDaemonService;
import pers.acp.core.log.LogFactory;
import pers.acp.ftp.config.SFTPConfig;
import pers.acp.ftp.exceptions.SFTPServerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * SFTP服务
 */
public class SFTPServer implements Runnable, IDaemonService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private List<SFTPServerUser> userList;

    private SFTPConfig.Listen listen;

    private SshServer sshServer = null;

    public SFTPServer(List<SFTPServerUser> userList, SFTPConfig.Listen listen) {
        this.userList = userList;
        this.listen = listen;
    }

    @Override
    public String getServiceName() {
        return "sftp service " + listen.getName();
    }

    @Override
    public void stopService() {
        try {
            if (sshServer != null) {
                sshServer.stop(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        try {
            if (CommonTools.isNullStr(listen.getDefaultHomeDirectory())) {
                throw new SFTPServerException("defaultHomeDirectory is null");
            }
            String defaultHomeDirectory = CommonTools.getAbsPath(listen.getDefaultHomeDirectory());
            if (userList == null || userList.isEmpty()) {
                log.error("start sftp server failed [" + listen.getName() + "] : no user set!");
                throw new SFTPServerException("no user set");
            }

            String keyAuthType = listen.getKeyAuthType();
            if (listen.isPublicKeyAuth() && CommonTools.isNullStr(keyAuthType)) {
                log.error("start sftp server failed [" + listen.getName() + "] : keyAuthType is not support [" + keyAuthType + "] !");
                throw new SFTPServerException("keyAuthType is not support : " + keyAuthType);
            } else {
                if (!keyAuthType.equals("der") && !keyAuthType.equals("pem") && !keyAuthType.equals("ssh")) {
                    log.error("start sftp server failed [" + listen.getName() + "] : keyAuthType is not support [" + keyAuthType + "] !");
                    throw new SFTPServerException("keyAuthType is not support : " + keyAuthType);
                }
            }

            String keyAuthMode = listen.getKeyAuthMode();
            if (CommonTools.isNullStr(keyAuthMode)) {
                log.error("start sftp server failed [" + listen.getName() + "] : keyAuthMode is not support [" + keyAuthMode + "] !");
                throw new SFTPServerException("keyAuthMode is not support : " + keyAuthMode);
            } else {
                if (!keyAuthMode.equals("DSA") && !keyAuthMode.equals("RSA")) {
                    log.error("start sftp server failed [" + listen.getName() + "] : keyAuthMode is not support [" + keyAuthMode + "] !");
                    throw new SFTPServerException("keyAuthMode is not support : " + keyAuthMode);
                }
            }

            sshServer = SshServer.setUpDefaultServer();
            sshServer.setPort(listen.getPort());
            String keyPath = CommonTools.getAbsPath(listen.getHostKeyPath());
            if (listen.isPublicKeyAuth()) {
                sshServer.getProperties().put(SshServer.AUTH_METHODS, "publickey");
                sshServer.setPublickeyAuthenticator(new UserPublicKeyAuthcator(userList, true, keyAuthMode, keyAuthType));
            } else {
                sshServer.setPublickeyAuthenticator(new UserPublicKeyAuthcator(userList, false, keyAuthMode, keyAuthType));
            }
            sshServer.setCommandFactory(new ScpCommandFactory());
            sshServer.setShellFactory(new ProcessShellFactory());
            List<NamedFactory<Command>> namedFactoryList = new ArrayList<>();
            namedFactoryList.add(new SftpSubsystem.Factory());
            sshServer.setSubsystemFactories(namedFactoryList);
            if (!listen.isPublicKeyAuth() && listen.isPasswordAuth()) {
                sshServer.setPasswordAuthenticator(new UserPasswordAuthcator(userList, true));
            } else {
                sshServer.setPasswordAuthenticator(new UserPasswordAuthcator(userList, false));
            }
            if (SecurityUtils.isBouncyCastleRegistered()) {
                sshServer.setKeyPairProvider(new PEMGeneratorHostKeyProvider(keyPath + ".pem", keyAuthMode));
            } else {
                sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(keyPath + ".ser", keyAuthMode));
            }
            VirtualFileSystemFactory virtualFileSystemFactory = new VirtualFileSystemFactory(defaultHomeDirectory);
            for (SFTPServerUser sftpServerUser : userList) {
                String homeDirectory = sftpServerUser.getHomedirectory();
                if (CommonTools.isNullStr(homeDirectory)) {
                    virtualFileSystemFactory.setUserHomeDir(sftpServerUser.getUsername(), defaultHomeDirectory);
                } else {
                    homeDirectory = homeDirectory.replace("\\", "/");
                    if (!homeDirectory.startsWith("/")) {
                        homeDirectory = "/" + homeDirectory;
                    }
                    if (defaultHomeDirectory.equals("/")) {
                        virtualFileSystemFactory.setUserHomeDir(sftpServerUser.getUsername(), homeDirectory);
                    } else {
                        virtualFileSystemFactory.setUserHomeDir(sftpServerUser.getUsername(), defaultHomeDirectory + homeDirectory);
                    }
                }
            }
            sshServer.setFileSystemFactory(virtualFileSystemFactory);
            sshServer.start();
            log.info("sftp server [" + listen.getName() + "] is started , path : " + defaultHomeDirectory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("start sftp server failed [" + listen.getName() + "] port:" + listen.getPort());
        }
    }

}
