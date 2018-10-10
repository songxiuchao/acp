package pers.acp.ftp.user;

import pers.acp.ftp.server.FTPServerUser;
import pers.acp.ftp.server.SFTPServerUser;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP/SFTP 服务用户接口
 */
public interface UserFactory {

    List<FTPServerUser> generateFtpUserList();

    List<SFTPServerUser> generateSFtpUserList();

}
