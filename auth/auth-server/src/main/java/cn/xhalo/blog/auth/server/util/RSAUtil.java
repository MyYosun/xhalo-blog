package cn.xhalo.blog.auth.server.util;


import cn.xhalo.blog.auth.server.config.AuthServerProperties;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 12:17 下午
 * @Description:
 */
public class RSAUtil {

    private static AuthServerProperties authServerProperties;

    public static void init() {
        if (authServerProperties == null) {
            synchronized (authServerProperties) {
                authServerProperties = SpringBeanUtil.getBean(AuthServerProperties.class);
            }
        }
    }

    public static Key convertStringToPublicKey(String publicKey) throws Base64DecodingException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.decode(publicKey.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }


    public static Key convertStringToPrivateKey(String privateKey) throws Base64DecodingException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] clear = Base64.decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public static RSAPrivateKey getPrivateKey() {
        RSAPrivateKey privateKey = null;
        try {
            privateKey = (RSAPrivateKey) RSAUtil.convertStringToPrivateKey(authServerProperties.getTokenRSAPrivateKey());
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static RSAPublicKey getPublicKey() {
        RSAPublicKey publicKey = null;
        try {
            publicKey = (RSAPublicKey) RSAUtil.convertStringToPublicKey(authServerProperties.getTokenRSAPublicKey());
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;
    }

}
