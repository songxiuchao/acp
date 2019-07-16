package pers.acp.core.security

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object SHA1Utils {

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @return 密文
     */
    @JvmStatic
    fun encrypt(plainText: String): String = SignatureUtils.encrypt(plainText, SignatureUtils.SHA1)

}