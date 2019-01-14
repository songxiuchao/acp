package pers.acp.core.security;

import org.bouncycastle.util.encoders.Base64;
import pers.acp.core.tools.CommonUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * Create by zhangbin on 2017-11-1 15:57
 */
public class DESUtils {

    private static final byte[] ZERO_IVC = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    private static String encode = CommonUtils.getDefaultCharset();

    private static final String CRYPT_TYPE_3DES_CBC = "DESede/CBC/PKCS5Padding";

    private static final String CRYPT_TYPE_3DES_ECB = "DESede/ECB/PKCS5Padding";

    private static final String CRYPT_TYPE_DES_CBC = "DES/CBC/PKCS5Padding";

    private static final String CRYPT_TYPE_DES_ECB = "DES/ECB/PKCS5Padding";

    /**
     * 3DES加密cbc模式 DESede/CBC/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    public static String encryptBy3DesCbc(String plainText, Key key) throws Exception {
        return encrypt(plainText, key, CRYPT_TYPE_3DES_CBC);
    }

    /**
     * 3DES解密cbc模式 DESede/CBC/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    public static String decryptBy3DesCbc(String encryptedText, Key key) throws Exception {
        return decrypt(encryptedText, key, CRYPT_TYPE_3DES_CBC);
    }

    /**
     * 3DES加密ecb模式 DESede/ECB/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    public static String encryptBy3DesEcb(String plainText, Key key) throws Exception {
        return encrypt(plainText, key, CRYPT_TYPE_3DES_ECB);
    }

    /**
     * 3DES解密ecb模式 DESede/ECB/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    public static String decryptBy3DesEcb(String encryptedText, Key key) throws Exception {
        return decrypt(encryptedText, key, CRYPT_TYPE_3DES_ECB);
    }

    /**
     * DES加密cbc模式 DES/CBC/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    public static String encryptByDesCbc(String plainText, Key key) throws Exception {
        return encrypt(plainText, key, CRYPT_TYPE_DES_CBC);
    }

    /**
     * DES解密cbc模式 DES/CBC/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    public static String decryptByDesCbc(String encryptedText, Key key) throws Exception {
        return decrypt(encryptedText, key, CRYPT_TYPE_DES_CBC);
    }

    /**
     * DES加密ecb模式 DES/ECB/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    public static String encryptByDesEcb(String plainText, Key key) throws Exception {
        return encrypt(plainText, key, CRYPT_TYPE_DES_ECB);
    }

    /**
     * DES解密ecb模式 DES/ECB/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    public static String decryptByDesEcb(String encryptedText, Key key) throws Exception {
        return decrypt(encryptedText, key, CRYPT_TYPE_DES_ECB);
    }

    /**
     * 加密
     *
     * @param plainText  待加密数据
     * @param key        秘钥
     * @param cryptType 加密类型，默认 DESede/ECB/PKCS5Padding
     * @return 加密结果
     */
    public static String encrypt(String plainText, Key key, String cryptType) throws Exception {
        if (CommonUtils.isNullStr(cryptType)) {
            cryptType = CRYPT_TYPE_3DES_ECB;
        }
        Cipher cipher = Cipher.getInstance(cryptType);
        if (cryptType.toUpperCase().contains("ECB")) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            IvParameterSpec iv = new IvParameterSpec(ZERO_IVC);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        }
        String encryptText;
        byte[] encrypt;
        encrypt = cipher.doFinal(plainText.getBytes(encode));
        encryptText = Base64.toBase64String(encrypt).trim();
        return encryptText;
    }

    /**
     * 解密
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @param cryptType    加密类型，默认 DESede/ECB/PKCS5Padding
     * @return 解密结果
     */
    public static String decrypt(String encryptedText, Key key, String cryptType) throws Exception {
        if (CommonUtils.isNullStr(cryptType)) {
            cryptType = CRYPT_TYPE_3DES_ECB;
        }
        Cipher cipher = Cipher.getInstance(cryptType);
        if (cryptType.toUpperCase().contains("ECB")) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            IvParameterSpec iv = new IvParameterSpec(ZERO_IVC);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        }
        String decryptText;
        byte[] decrypt;
        decrypt = cipher.doFinal(Base64.decode(encryptedText));
        decryptText = new String(decrypt, encode).trim();
        return decryptText;
    }

}
