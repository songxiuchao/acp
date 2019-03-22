package pers.acp.core.security;

import pers.acp.core.tools.CommonUtils;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.security.Key;

public final class AESUtils {

    private static final String CRYPT_TYPE = "AES/ECB/PKCS5Padding";

    private static String encode = CommonUtils.getDefaultCharset();

    /**
     * 加密 AES/ECB/PKCS5Padding
     *
     * @param plainText 待加密字符串
     * @param key       密钥
     * @return 密文
     */
    public static String encrypt(String plainText, Key key) throws Exception {
        return encrypt(plainText, key, CRYPT_TYPE);
    }

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param key       密钥
     * @param cryptType 加密类型，默认 AES/ECB/PKCS5Padding
     * @return 密文
     */
    public static String encrypt(String plainText, Key key, String cryptType) throws Exception {
        if (CommonUtils.isNullStr(cryptType)) {
            cryptType = CRYPT_TYPE;
        }
        Cipher cipher = Cipher.getInstance(cryptType);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.toBase64String(cipher.doFinal(plainText.getBytes(encode))).strip();
    }

    /**
     * 解密 AES/ECB/PKCS5Padding
     *
     * @param encryptedText 加密字符串
     * @param key           密钥
     * @return 明文
     */
    public static String decrypt(String encryptedText, Key key) throws Exception {
        return decrypt(encryptedText, key, CRYPT_TYPE);
    }

    /**
     * 解密
     *
     * @param encryptedText 加密字符串
     * @param key           密钥
     * @param cryptType     加密类型，默认 AES/ECB/PKCS5Padding
     * @return 明文
     */
    public static String decrypt(String encryptedText, Key key, String cryptType) throws Exception {
        if (CommonUtils.isNullStr(cryptType)) {
            cryptType = CRYPT_TYPE;
        }
        Cipher cipher = Cipher.getInstance(cryptType);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decode(encryptedText)), encode).strip();
    }

}
