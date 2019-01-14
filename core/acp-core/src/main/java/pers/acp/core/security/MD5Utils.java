package pers.acp.core.security;

public class MD5Utils {

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @return 密文
     */
    public static String encrypt(String plainText) {
        return SignatureUtils.encrypt(plainText, SignatureUtils.MD5);
    }
}
