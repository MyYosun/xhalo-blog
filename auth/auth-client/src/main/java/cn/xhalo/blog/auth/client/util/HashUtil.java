package cn.xhalo.blog.auth.client.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by anai on 2017/5/6.
 */
public class HashUtil {
    public static void main(String[] args) throws Exception {
        String uuid = generateUUID();
        System.out.println("uuid =" + uuid);
        String originalPassword = "password";
        String generatedSecuredPasswordHash = generateStrongPasswordHash(originalPassword);
        System.out.println(generatedSecuredPasswordHash);
        boolean matched = validatePassword("password", generatedSecuredPasswordHash);
        System.out.println(matched);
        matched = validatePassword("password1", generatedSecuredPasswordHash);
        System.out.println(matched);
        originalPassword = "123456";
        generatedSecuredPasswordHash = generateStrongPasswordHash(originalPassword);
        System.out.println(generatedSecuredPasswordHash);
        matched = validatePassword("123456", generatedSecuredPasswordHash);
        System.out.println(matched);
        matched = validatePassword("1234567", generatedSecuredPasswordHash);
        System.out.println(matched);
    }

    private HashUtil() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static String generateUUID() {
        UUID id = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(id.getMostSignificantBits());
        bb.putLong(id.getLeastSignificantBits());
        return Base64.encodeBase64URLSafeString(bb.array());
    }

    public static String md5(String input) {
        String md5 = null;
        if(null == input) {
            return null;
        } else {
            try {
                MessageDigest e = MessageDigest.getInstance("MD5");
                e.update(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
                md5 = (new BigInteger(1, e.digest())).toString(16);
            } catch (NoSuchAlgorithmException var3) {
                var3.printStackTrace();
            }

            return md5;
        }
    }

    public static String hex(byte[] array) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString(array[i] & 255 | 256).substring(1, 3));
        }

        return sb.toString();
    }

    public static String md5Hex(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException var2) {
            ;
        } catch (UnsupportedEncodingException var3) {
            ;
        }

        return null;
    }

    public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        short iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt().getBytes(StandardCharsets.UTF_8);
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 512);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Arrays.toString(salt);
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = array.length * 2 - hex.length();
        return paddingLength > 0?String.format("%0" + paddingLength + "d", new Object[]{Integer.valueOf(0)}) + hex:hex;
    }

    public static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
        int diff = hash.length ^ testHash.length;

        for(int i = 0; i < hash.length && i < testHash.length; ++i) {
            diff |= hash[i] ^ testHash[i];
        }

        return diff == 0;
    }

    public static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

    public static String encryption(String key, String encode) {
        String pwdBase64Str = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(encode.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secret);
            byte[] digest = mac.doFinal(key.getBytes("UTF-8"));
            pwdBase64Str = Base64.encodeBase64String(digest).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pwdBase64Str;
    }
}
