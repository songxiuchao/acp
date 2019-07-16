package pers.acp.ftp.server

import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.impl.DefaultConnectionConfig
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PasswordEncryptor
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission
import org.apache.ftpserver.usermanager.impl.TransferRatePermission
import org.apache.ftpserver.usermanager.impl.WritePermission
import pers.acp.core.CommonTools
import pers.acp.core.interfaces.IDaemonService
import pers.acp.core.log.LogFactory
import pers.acp.core.security.MD5Utils
import pers.acp.core.security.SHA1Utils
import pers.acp.core.security.SHA256Utils
import pers.acp.ftp.conf.FtpListener
import pers.acp.ftp.exceptions.FtpServerException
import java.util.ArrayList

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class FtpServer(private val userList: List<FtpServerUser>, private val listen: FtpListener) : Runnable, IDaemonService {

    private val log = LogFactory.getInstance(this.javaClass)

    private var ftpServerInstance: FtpServer? = null

    override fun getServiceName(): String {
        return "ftp service " + listen.name!!
    }

    override fun stopService() {
        ftpServerInstance?.stop()
    }

    override fun run() {
        try {
            if (CommonTools.isNullStr(listen.defaultHomeDirectory)) {
                throw Exception("defaultHomeDirectory is null")
            }
            val defaultHomeDirectory = CommonTools.getAbsPath(listen.defaultHomeDirectory!!)
            val serverFactory = FtpServerFactory()
            val factory = ListenerFactory()
            factory.port = listen.port
            serverFactory.addListener("default", factory.createListener())
            val connectionConfig = DefaultConnectionConfig(listen.anonymousLoginEnabled, listen.loginFailureDelay, listen.maxLogins, listen.maxAnonymousLogins, listen.maxLoginFailures, listen.maxThreads)
            serverFactory.connectionConfig = connectionConfig
            val pwdMode = listen.pwdEncryptMode

            val userManagerFactory = PropertiesUserManagerFactory()
            userManagerFactory.passwordEncryptor = object : PasswordEncryptor {

                override fun encrypt(pwd: String): String? {
                    val result: String?
                    when (pwdMode) {
                        "MD5" -> result = MD5Utils.encrypt(pwd)
                        "SHA1" -> result = SHA1Utils.encrypt(pwd)
                        "SHA256" -> result = SHA256Utils.encrypt(pwd)
                        else -> result = null
                    }
                    return result
                }

                override fun matches(passwordToCheck: String, storedPassword: String): Boolean {
                    return passwordToCheck == storedPassword
                }

            }
            val anonymous = BaseUser()
            anonymous.name = "anonymous"
            anonymous.enabled = listen.anonymousLoginEnabled
            anonymous.homeDirectory = defaultHomeDirectory
            if (listen.anonymousWritePermission) {
                val authorities = ArrayList<Authority>()
                authorities.add(WritePermission())
                anonymous.authorities = authorities
            }
            serverFactory.userManager.save(anonymous)
            if (userList.isNotEmpty()) {
                for (ftpServerUser in userList) {
                    val user = BaseUser()
                    user.name = ftpServerUser.username
                    user.password = ftpServerUser.password
                    user.enabled = ftpServerUser.isEnableFlag
                    var homeDirectory = ftpServerUser.homeDirectory
                    if (CommonTools.isNullStr(homeDirectory)) {
                        user.homeDirectory = defaultHomeDirectory
                    } else {
                        homeDirectory = homeDirectory.replace("\\", "/")
                        if (!homeDirectory.startsWith("/")) {
                            homeDirectory = "/$homeDirectory"
                        }
                        if (defaultHomeDirectory == "/") {
                            user.homeDirectory = homeDirectory
                        } else {
                            user.homeDirectory = defaultHomeDirectory + homeDirectory
                        }
                    }
                    user.maxIdleTime = ftpServerUser.idleTime
                    val authorities = ArrayList<Authority>()
                    if (ftpServerUser.writePermission) {
                        authorities.add(WritePermission())
                    }
                    authorities.add(TransferRatePermission(ftpServerUser.downloadRate, ftpServerUser.uploadRate))
                    authorities.add(ConcurrentLoginPermission(ftpServerUser.maxLoginNumber, ftpServerUser.maxLoginPerIp))
                    user.authorities = authorities
                    serverFactory.userManager.save(user)
                }
            } else {
                if (!listen.anonymousLoginEnabled) {
                    log.error("start ftp server failed [" + listen.name + "] : no user set!")
                    throw FtpServerException("no user set")
                }
            }
            ftpServerInstance = serverFactory.createServer()
            ftpServerInstance!!.start()
            log.info("ftp server [" + listen.name + "] is started, port : " + listen.port + ", path : " + defaultHomeDirectory)
        } catch (e: Exception) {
            log.error(e.message, e)
            log.error("start ftp server failed [" + listen.name + "] port:" + listen.port)
        }
    }
}