package pers.acp.springboot.core.file.user;

import pers.acp.springboot.core.file.ftp.FTPServerUser;
import pers.acp.springboot.core.file.sftp.SFTPServerUser;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP/SFTP 服务用户接口
 */
public interface UserFactory {

    List<FTPServerUser> generateFtpUserList();

    List<SFTPServerUser> generateSFtpUserList();

}
