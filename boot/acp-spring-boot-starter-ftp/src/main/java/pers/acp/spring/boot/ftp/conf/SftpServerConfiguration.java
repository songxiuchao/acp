package pers.acp.spring.boot.ftp.conf;

import pers.acp.ftp.conf.SftpListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Sftp 服务配置
 *
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public class SftpServerConfiguration {

    public List<SftpListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<SftpListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * Sftp 服务监听列表
     */
    private List<SftpListener> listeners = new ArrayList<>();

}
