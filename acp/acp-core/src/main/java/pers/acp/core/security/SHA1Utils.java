package pers.acp.core.security;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

import java.security.MessageDigest;

public class SHA1Utils {

    private static final LogFactory log = LogFactory.getInstance(SHA1Utils.class);

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
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] byteArray = plainText.getBytes(encode);
            byte[] sha1Bytes = sha1.digest(byteArray);
            encryptText = ByteUtils.toHexString(sha1Bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            encryptText = "";
        }
        return encryptText;
    }
}
