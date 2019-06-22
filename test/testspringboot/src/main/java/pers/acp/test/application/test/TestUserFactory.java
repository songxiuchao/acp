package pers.acp.test.application.test;

import org.springframework.stereotype.Component;
import pers.acp.ftp.server.FTPServerUser;
import pers.acp.ftp.server.SFTPServerUser;
import pers.acp.ftp.user.UserFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Component
public class TestUserFactory implements UserFactory {

    @Override
    public List<FTPServerUser> generateFtpUserList() {
        List<FTPServerUser> result = new ArrayList<>();
        FTPServerUser ftpServerUser = new FTPServerUser();
        ftpServerUser.setUsername("ftp");
        ftpServerUser.setPassword("1");
        ftpServerUser.setWritepermission(true);
        ftpServerUser.setMaxloginnumber(10);
        ftpServerUser.setMaxloginperip(10);
        ftpServerUser.setHomeDirectory("/");
        ftpServerUser.setEnableFlag(true);
        result.add(ftpServerUser);
        return result;
    }

    @Override
    public List<SFTPServerUser> generateSFtpUserList() {
        List<SFTPServerUser> result = new ArrayList<>();
        SFTPServerUser sftpServerUser = new SFTPServerUser();
        sftpServerUser.setUsername("ftp");
        sftpServerUser.setPassword("1");
        sftpServerUser.setHomeDirectory("/");
        sftpServerUser.setEnableFlag(true);
        result.add(sftpServerUser);
        return result;
    }

}
