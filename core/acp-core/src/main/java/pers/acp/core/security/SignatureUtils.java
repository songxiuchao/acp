package pers.acp.core.security;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

import java.security.MessageDigest;

/**
 * @author zhang by 18/12/2018 20:54
 * @since JDK 11
 */
public class SignatureUtils {

    private static final LogFactory log = LogFactory.getInstance(SignatureUtils.class);

    private static String encode = CommonUtils.getDefaultCharset();

    public static String MD5 = "MD5";

    public static String SHA1 = "SHA-1";

    public static String SHA256 = "SHA-256";

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param algorithm 使用的算法
     * @return 密文
     */
    public static String encrypt(String plainText, String algorithm) {
        String encryptText;
        try {
            encryptText = ByteUtils.toHexString(MessageDigest.getInstance(algorithm).digest(plainText.getBytes(encode)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            encryptText = "";
        }
        return encryptText;
    }

}
