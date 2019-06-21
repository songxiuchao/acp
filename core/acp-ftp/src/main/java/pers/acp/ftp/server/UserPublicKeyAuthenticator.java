package pers.acp.ftp.server;

import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import pers.acp.core.log.LogFactory;
import pers.acp.core.security.key.KeyManagement;

import java.security.PublicKey;
import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * 用户认证类
 */
class UserPublicKeyAuthenticator implements PublickeyAuthenticator {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private List<SFTPServerUser> userList;

    private boolean needAuth;

    private String keyAuthMode;

    private String keyAuthType;

    UserPublicKeyAuthenticator(List<SFTPServerUser> userList, boolean needAuth, String keyAuthMode, String keyAuthType) {
        this.userList = userList;
        this.needAuth = needAuth;
        this.keyAuthMode = keyAuthMode;
        this.keyAuthType = keyAuthType;
    }

    @Override
    public boolean authenticate(String username, PublicKey publicKey, ServerSession serverSession) {
        boolean result = false;
        if (needAuth) {
            boolean isexist = false;
            for (SFTPServerUser sftpServerUser : userList) {
                if (sftpServerUser.isEnableFlag()) {
                    if (sftpServerUser.getUsername().equals(username)) {
                        isexist = true;
                        try {
                            PublicKey userPublicKey = getUserPublicKey(sftpServerUser.getPublicKey());
                            if (publicKey.equals(userPublicKey)) {
                                result = true;
                                log.info("sftp user [" + username + "] certificate authentication successFull");
                            } else {
                                result = false;
                                log.error("sftp user [" + username + "] certificate authentication failed : certificate is invalid");
                            }
                        } catch (Exception e) {
                            log.error("certificate authentication exception : " + e.getMessage(), e);
                            result = false;
                        }
                        break;
                    }
                }
            }
            if (!isexist) {
                log.error("sftp user [" + username + "] certificate authentication failed : user is not existence");
            }
        } else {
            log.error("sftp server certificate authentication is not available");
        }
        return result;
    }

    private PublicKey getUserPublicKey(String publicKey) throws Exception {
        PublicKey userPublicKey;
        switch (keyAuthType) {
            case "der":
                if (keyAuthMode.equals("DSA")) {
                    userPublicKey = KeyManagement.getDSAPublicKeyForDER(publicKey);
                } else {
                    userPublicKey = KeyManagement.getRSAPublicKeyForDER(publicKey);
                }
                break;
            case "pem":
                if (keyAuthMode.equals("DSA")) {
                    userPublicKey = KeyManagement.getDSAPublicKeyForPEM(publicKey);
                } else {
                    userPublicKey = KeyManagement.getRSAPublicKeyForPEM(publicKey);
                }
                break;
            case "ssh":
                if (keyAuthMode.equals("DSA")) {
                    userPublicKey = KeyManagement.getDSAPublicKeyForSSH(publicKey);
                } else {
                    userPublicKey = KeyManagement.getRSAPublicKeyForSSH(publicKey);
                }
                break;
            default:
                userPublicKey = null;
        }
        return userPublicKey;
    }

}
