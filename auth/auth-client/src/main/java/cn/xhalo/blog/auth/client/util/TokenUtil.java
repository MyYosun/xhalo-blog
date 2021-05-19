package cn.xhalo.blog.auth.client.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * @Author: xhalo
 * @Date: 2021/5/19 3:57 下午
 * @Description:
 */
public class TokenUtil {

    public static String getTokenClaim(String token, String key) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Claim claim = jwt.getClaim(key);
            if (claim == null) {
                return null;
            }
            return claim.asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getTokenExpiresAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isTokenExpire(String token) {
        try {
            Long expireTime = getTokenExpiresAt(token).getTime();
            Long nowTime = System.currentTimeMillis();
            if (expireTime < nowTime) {
                return false;
            }
            return true;
        }  catch (Exception e) {
            return false;
        }
    }
}
