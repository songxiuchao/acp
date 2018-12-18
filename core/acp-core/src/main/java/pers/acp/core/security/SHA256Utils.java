package pers.acp.core.security;

/**
 * @author zhang by 18/12/2018 22:47
 * @since JDK 11
 */
public class SHA256Utils {

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @return 密文
     */
    public static String encrypt(String plainText) {
        return SignatureUtils.encrypt(plainText, SignatureUtils.SHA256);
    }

}
