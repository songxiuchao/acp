package pers.acp.spring.boot.ftp.conf;

import pers.acp.ftp.conf.SFTPListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Sftp 服务配置
 *
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public class SftpServerConfiguration {

    public List<SFTPListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<SFTPListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * Sftp 服务监听列表
     */
    private List<SFTPListener> listeners = new ArrayList<>();

}
