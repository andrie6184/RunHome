package com.runnerfun.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
     * 加密
     */
    public static byte[] rsaEncrypt(PublicKey publicKey, byte[] srcBytes) {
        if (publicKey != null) {
            //Cipher负责完成加密或解密工作，基于RSA
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("RSA");
                //根据公钥，对Cipher对象进行初始化
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                return cipher.doFinal(srcBytes);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解密
     */
    public static byte[] rsaDecrypt(PrivateKey privateKey, byte[] srcBytes) {
        if (privateKey != null) {
            //Cipher负责完成加密或解密工作，基于RSA
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("RSA");
                //根据公钥，对Cipher对象进行初始化
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                return cipher.doFinal(srcBytes);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return null;
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

}
