package pers.acp.ftp.test;

import pers.acp.ftp.client.SftpClient;

import java.io.File;

/**
 * @author zhang by 05/03/2019
 * @since JDK 11
 */
public class TestClient {

    public static void main(String[] args) {
        SftpClient client = new SftpClient("115.159.227.180", 22, "root", "Pa88wordxstar");
        client.setRemotePath("/usr");
        client.setFileName("测试.txt");
        client.doUploadForSFTP(new File("C:\\WorkFile\\工作资料\\区块链\\服务器信息.txt"));
    }

}
