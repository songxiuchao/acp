package pers.acp.core.security;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.core.tools.CommonUtils;

import javax.crypto.Mac;
import java.security.Key;

/**
 * @author zhangbin by 10/04/2018 16:21
 * @since JDK1.8
 */
public final class HMACUtils {

    /**
     * 加密算法
     * MAC算法可选以下多种算法
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     */
    public static final String CRYPT_TYPE = "HmacSHA256";

    private static String encode = CommonUtils.getDefaultCharset();

    /**
     * 加密 HmacSHA256
     *
     * @param plainText 待加密字符串
     * @param key       密钥
     * @return 明文
     */
    public static String encrypt(String plainText, Key key) throws Exception {
        return encrypt(plainText, key, CRYPT_TYPE);
    }

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param key       密钥
     * @param cryptType 加密类型，默认 HmacSHA256
     * @return 明文
     */
    public static String encrypt(String plainText, Key key, String cryptType) throws Exception {
        if (CommonUtils.isNullStr(cryptType)) {
            cryptType = CRYPT_TYPE;
        }
        String encryptText;
        byte[] encrypt;
        Mac mac = Mac.getInstance(cryptType);
        mac.init(key);
        encrypt = mac.doFinal(plainText.getBytes(encode));
        encryptText = ByteUtils.toHexString(encrypt).trim();
        return encryptText;
    }

}
