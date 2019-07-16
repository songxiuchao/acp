package pers.acp.core.security

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import pers.acp.core.log.LogFactory
import pers.acp.core.tools.CommonUtils

import java.security.MessageDigest

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object SignatureUtils {

    private val log = LogFactory.getInstance(SignatureUtils::class.java)

    private const val ENCODE = CommonUtils.defaultCharset

    internal var MD5 = "MD5"

    internal var SHA1 = "SHA-1"

    internal var SHA256 = "SHA-256"

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param algorithm 使用的算法
     * @return 密文
     */
    fun encrypt(plainText: String, algorithm: String): String =
            try {
                ByteUtils.toHexString(MessageDigest.getInstance(algorithm).digest(plainText.toByteArray(charset(ENCODE))))
            } catch (e: Exception) {
                log.error(e.message, e)
                ""
            }

}
