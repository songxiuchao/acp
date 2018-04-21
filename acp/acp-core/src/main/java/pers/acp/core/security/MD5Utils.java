package pers.acp.core.security;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

import java.security.MessageDigest;

public class MD5Utils {

    private static final LogFactory log = LogFactory.getInstance(MD5Utils.class);

    private static String encode = CommonUtils.getDefaultCharset();

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @return 密文
     */
    public static String encrypt(String plainText) {
        String encryptText;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = plainText.getBytes(encode);
            byte[] md5Bytes = md5.digest(byteArray);
            encryptText = ByteUtils.toHexString(md5Bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            encryptText = "";
        }
        return encryptText;
    }
}
