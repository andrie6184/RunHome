package com.runnerfun.tools;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;

import static android.R.attr.key;

public class RSATools {

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
     * 使用公钥加密
     *
     * @param content
     * @return
     */
    public static String encryptByPublic(String content) throws Exception {
        try {

            PublicKey key = getPublicKey(publicKey);
           // RSAPublicKey pubkey = genRSAPublicKey(modulus, exponent);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] bytes = content.getBytes();
            int len = bytes.length;
            int blockSize = cipher.getBlockSize();

            for (int i = 0; i < len; i += blockSize) {
                int n = Math.min(blockSize, len - i);
                byte[] outBytes = cipher.doFinal(bytes, i, n);
                out.write(outBytes);
            }

            return  new String(Base64.encode(out.toByteArray(), Base64.DEFAULT));

        } catch (Exception e) {
            throw e;
        }
    }

    private static String decode(String s) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PrivateKey key = getPrivateKey(privateKey);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[]deBytes = cipher.doFinal(s.getBytes());
        return new String(deBytes);
    }

    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }


    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

}
