package pers.acp.ftp.test;

import pers.acp.ftp.client.SftpClient;

import java.io.File;

/**
 * @author zhang by 05/03/2019
 * @since JDK 11
 */
public class TestClient {

    public static void main(String[] args) {
        SftpClient client1 = new SftpClient("115.159.227.180", 22, "root", "password");
        SftpClient client2 = new SftpClient("", "", "115.159.227.180", 22, "root");
        client1.setRemotePath("/usr");
        client1.setFileName("测试.txt");
        client1.doUploadForSFTP(new File("C:\\WorkFile\\工作资料\\区块链\\服务器信息.txt"));
    }

}
