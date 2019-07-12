package pers.acp.ftp.conf

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class SftpListener {

    @XStreamAsAttribute
    @XStreamAlias("name")
    var name: String? = null

    @XStreamAsAttribute
    @XStreamAlias("enabled")
    var isEnabled = false

    @XStreamAsAttribute
    @XStreamAlias("port")
    var port: Int = 0

    @XStreamAsAttribute
    @XStreamAlias("hostKeyPath")
    var hostKeyPath: String? = null

    @XStreamAsAttribute
    @XStreamAlias("passwordAuth")
    var isPasswordAuth = true

    @XStreamAsAttribute
    @XStreamAlias("publicKeyAuth")
    var isPublicKeyAuth = false

    @XStreamAsAttribute
    @XStreamAlias("keyAuthType")
    var keyAuthType = "pem"

    @XStreamAsAttribute
    @XStreamAlias("keyAuthMode")
    var keyAuthMode = "RSA"

    @XStreamAsAttribute
    @XStreamAlias("defaultHomeDirectory")
    var defaultHomeDirectory: String? = null

    @XStreamAsAttribute
    @XStreamAlias("userFactoryClass")
    var userFactoryClass: String? = null

}