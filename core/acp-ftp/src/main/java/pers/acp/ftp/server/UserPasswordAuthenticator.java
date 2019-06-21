package pers.acp.ftp.server;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import pers.acp.core.log.LogFactory;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * 用户认证类
 */
class UserPasswordAuthenticator implements PasswordAuthenticator {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private List<SFTPServerUser> userList;

    private boolean needAuth;

    UserPasswordAuthenticator(List<SFTPServerUser> userList, boolean needAuth) {
        this.userList = userList;
        this.needAuth = needAuth;
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession serverSession) {
        boolean result = false;
        if (needAuth) {
            boolean isexist = false;
            for (SFTPServerUser sftpServerUser : userList) {
                if (sftpServerUser.isEnableFlag()) {
                    if (sftpServerUser.getUsername().equals(username)) {
                        isexist = true;
                        if (sftpServerUser.getPassword().equals(password)) {
                            result = true;
                            log.info("sftp user [" + username + "] password authentication successFull");
                            break;
                        } else {
                            log.error("sftp user [" + username + "] password authentication failed : password error");
                        }
                    }
                }
            }
            if (!isexist) {
                log.error("sftp user [" + username + "] password authentication failed : user is not existence");
            }
        } else {
            log.error("sftp server password authentication is not available");
        }
        return result;
    }

}
