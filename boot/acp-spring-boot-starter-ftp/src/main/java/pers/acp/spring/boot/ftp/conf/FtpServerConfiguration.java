package pers.acp.spring.boot.ftp.conf;

import pers.acp.ftp.conf.FTPListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public class FtpServerConfiguration {

    public List<FTPListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<FTPListener> listeners) {
        this.listeners = listeners;
    }

    private List<FTPListener> listeners = new ArrayList<>();

}
