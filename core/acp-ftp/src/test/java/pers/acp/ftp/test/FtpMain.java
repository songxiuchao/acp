package pers.acp.ftp.test;

import pers.acp.ftp.InitFtpServer;
import pers.acp.ftp.InitSFtpServer;

/**
 * @author zhangbin by 28/09/2018 16:36
 * @since JDK 11
 */
public class FtpMain {

    public static void main(String[] args) {
        InitFtpServer.startFtpServer();
        InitSFtpServer.startSFtpServer();
    }

}
