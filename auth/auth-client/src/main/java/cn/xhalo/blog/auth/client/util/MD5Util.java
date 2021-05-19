package cn.xhalo.blog.auth.client.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author: xhalo
 * @Date: 2021/5/19 3:23 下午
 * @Description:
 */
@Slf4j
public class MD5Util {
    /**
     * @param text 明文
     * @param key 密钥
     * @return 密文
     */
    public static String md5(String text, String key) {
        String md5str = DigestUtils.md5Hex(text + key);
        System.out.println("MD5加密后的字符串为:" + md5str);
        log.info("MD5加密后的字符串为:{}", md5str);
        return md5str;
    }

    public static String md52(String text) {
        String md5str = DigestUtils.md5Hex(text);
        log.info("MD52加密后的字符串为:{}", md5str);
        return md5str;
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key 密钥
     * @param md5 密文
     */
    public static boolean verify(String text, String key, String md5) {
        String md5str = md5(text, key);
        if (md5str.equalsIgnoreCase(md5)) {
            return true;
        }
        return false;
    }
}
