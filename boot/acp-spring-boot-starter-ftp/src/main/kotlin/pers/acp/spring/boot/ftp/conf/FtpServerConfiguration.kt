package pers.acp.spring.boot.ftp.conf

import pers.acp.ftp.conf.FtpListener

/**
 * Ftp 服务端配置
 *
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class FtpServerConfiguration {

    /**
     * Ftp服务监听配置列表
     */
    var listeners: MutableList<FtpListener> = mutableListOf()

}
