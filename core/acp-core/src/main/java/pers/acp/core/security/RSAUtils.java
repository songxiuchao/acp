package pers.acp.core.security;

import pers.acp.core.tools.CommonUtils;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public final class RSAUtils {

    private static final String CRYPT_TYPE = "RSA/ECB/PKCS1Padding";

    private static String encode = CommonUtils.getDefaultCharset();

    /**
     * 公钥加密 RSA/ECB/PKCS1Padding
     *
     * @param data      待加密字符串
     * @param publicKey 公钥
     * @return 密文
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        return encryptByPublicKey(data, publicKey, CRYPT_TYPE);
    }

    /**
     * 公钥加密
     *
     * @param data       待加密字符串
     * @param publicKey  公钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 密文
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey, String cryptType) throws Exception {
        return doEncrypt(data, publicKey, cryptType);
    }

    /**
     * 私钥解密 RSA/ECB/PKCS1Padding
     *
     * @param data       加密字符串
     * @param privateKey 私钥
     * @return 明文
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        return decryptByPrivateKey(data, privateKey, CRYPT_TYPE);
    }

    /**
     * 私钥解密
     *
     * @param data       加密字符串
     * @param privateKey 私钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 明文
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey, String cryptType) throws Exception {
        return doDecrypt(data, privateKey, cryptType);
    }

    /**
     * 私钥加密 RSA/ECB/PKCS1Padding
     *
     * @param data       待加密字符串
     * @param privateKey 私钥
     * @return 密文
     */
    public static String encryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        return encryptByPrivateKey(data, privateKey, CRYPT_TYPE);
    }

    /**
     * 私钥加密
     *
     * @param data       待加密字符串
     * @param privateKey 私钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 密文
     */
    public static String encryptByPrivateKey(String data, RSAPrivateKey privateKey, String cryptType) throws Exception {
        return doEncrypt(data, privateKey, cryptType);
    }

    /**
     * 公钥解密 RSA/ECB/PKCS1Padding
     *
     * @param data      加密字符串
     * @param publicKey 公钥
     * @return 明文
     */
    public static String decryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        return decryptByPublicKey(data, publicKey, CRYPT_TYPE);
    }

    /**
     * 公钥解密
     *
     * @param data       加密字符串
     * @param publicKey  公钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 明文
     */
    public static String decryptByPublicKey(String data, RSAPublicKey publicKey, String cryptType) throws Exception {
        return doDecrypt(data, publicKey, cryptType);
    }

    private static String doEncrypt(String data, RSAKey key, String cryptType) throws Exception {
        if (CommonUtils.isNullStr(cryptType)) {
            cryptType = CRYPT_TYPE;
        }
        Cipher cipher = Cipher.getInstance(cryptType);
        cipher.init(Cipher.ENCRYPT_MODE, (Key) key);
        // 模长
        int key_len = key.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        StringBuilder mi = new StringBuilder();
        // 如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi.append(Base64.toBase64String(cipher.doFinal(s.getBytes(encode))));
        }
        return mi.toString();
    }

    private static String doDecrypt(String data, RSAKey key, String cryptType) throws Exception {
        if (CommonUtils.isNullStr(cryptType)) {
            cryptType = CRYPT_TYPE;
        }
        Cipher cipher = Cipher.getInstance(cryptType);
        cipher.init(Cipher.DECRYPT_MODE, (Key) key);
        // 模长
        int key_len = key.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = Base64.decode(bytes);
        // 如果密文长度大于模长则要分组解密
        StringBuilder ming = new StringBuilder();
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming.append(new String(cipher.doFinal(arr), encode));
        }
        return ming.toString();
    }

    /**
     * 拆分字符串
     */
    private static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str;
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * 拆分数组
     */
    private static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }
}
