package com.runnerfun.network;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;

/**
 * RSAUtils
 * Created by andrie on 16/12/9.
 */

public class RSAUtils {

    public final static String privateKey = "MIICWwIBAAKBgQDFq18hiIx4TNsl+ASQO0rsYufnbP74II82Bdk/nUJlbZDoaAkk" +
            "O6FqkOdxgZPatoK/3n+fCLebeTCYdiiUvjJir7Iqr/twx2V9CNYsmvZIdsS7DW4R" +
            "BR+6h5CzgmA1Tw3fviCIE+0jmR5PIjShJFgU9tDGk/agwv0g+FGlVoLrowIDAQAB" +
            "AoGAEwafrXabLqkXHtx0c/2BBKewcpIHKJLZtfVyJ7FXRrP5EqWQ26/xpXQ1ErPK" +
            "K28NvrIHzqLkKU7M4p/c4BQ+fR2mVdfAb1Qjmk+06cq644/UwJc5qF7u1aW2XBCu" +
            "r5FmKHdSwsOLedg6B81tTerK++NV1ZL4GzvFehuzzU/jrcECQQD6b2d5xmkCJqzm" +
            "T3Tz4on+jH38EghSxtUaYC6Lr6r4tCg2NAnx+I6ji9urqo3i6SaWh+3bWv1CglMh" +
            "JCXHcYnbAkEAyg/PduLDuryvNYz80jrvi9lrK+QV++ql7pGvXx7qwo6P3IfsbQZh" +
            "1hWdh4oaEdVBPAOn03y0ttUlxwd4l06D2QJACmi/AEzC6Jf678e1sL1lxvLH+cY2" +
            "GlqxWNtOk/fFP3kdgsM+9pPCOgICK2x3YUsXk4Iq3Mc3Z6BLHEGIYHXTqQJAJMkK" +
            "EO+piGZvU0VuhWtbTs9vFld4tRr7yFnXXm7HeFHx2MkZ/qMpzoqy2gkHQ/XZ4W4c" +
            "8ICEecCGFxzCFHGjyQJAOSzgaA5I/yAa2czlt/WQ6tuP+2j4KGhP/Ggk4lywhIqx" +
            "/oIwdeaNN1jEB772AXASqlhjsQeVKU8ACfCWXgWQ7w==";

    public final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFq18hiIx4TNsl+ASQO0rsYufn" +
            "bP74II82Bdk/nUJlbZDoaAkkO6FqkOdxgZPatoK/3n+fCLebeTCYdiiUvjJir7Iq" +
            "r/twx2V9CNYsmvZIdsS7DW4RBR+6h5CzgmA1Tw3fviCIE+0jmR5PIjShJFgU9tDG" +
            "k/agwv0g+FGlVoLrowIDAQAB";

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, PublicKey publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 模长
        // int key_len = publicKey.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, 20);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, PrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //模长
        // int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        System.err.println(bcd.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, 20);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码
     */
    public static PublicKey getPublicKey(String key) {
        byte[] keyBytes;
        try {
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码
     */
    public static PrivateKey getPrivateKey(String key) {
        byte[] keyBytes;
        try {
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ASCII码转BCD码
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
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
    public static byte[][] splitArray(byte[] data, int len) {
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
