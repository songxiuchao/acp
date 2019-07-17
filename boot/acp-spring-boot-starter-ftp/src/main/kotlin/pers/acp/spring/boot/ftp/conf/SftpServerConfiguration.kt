package pers.acp.spring.boot.ftp.conf

import pers.acp.ftp.conf.SftpListener

/**
 * Sftp 服务配置
 *
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class SftpServerConfiguration {

    /**
     * Sftp 服务监听列表
     */
    var listeners: MutableList<SftpListener> = mutableListOf()

}
