package cn.xhalo.blog.auth.server.util;

import cn.xhalo.blog.auth.server.config.AuthServerProperties;
import cn.xhalo.blog.auth.server.enums.ErrorInfoEnum;
import cn.xhalo.blog.auth.server.enums.TokenKeyEnum;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;


/**
 * @Author: xhalo
 * @Date: 2021/5/18 11:26 上午
 * @Description:
 */
@Slf4j
public class TokenUtil {

    private static AuthServerProperties authServerProperties;

    public static void init() {
        if (authServerProperties == null) {
            synchronized (authServerProperties) {
                authServerProperties = SpringBeanUtil.getBean(AuthServerProperties.class);
            }
        }
    }

    /**
     * 获取过期时间
     *
     * @return
     */
    public static Date getExpiresAt(Long expireSeconds) {
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(expireSeconds);
        return DateUtil.localDateTimeToDate(localDateTime);
    }

    public static String generateServerToken(String uid) {
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey();
        RSAPublicKey publicKey = RSAUtil.getPublicKey();
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        return JWT.create()
                .withExpiresAt(getExpiresAt(authServerProperties.getTokenExpireSeconds()))
                .withClaim(TokenKeyEnum.USER_ID.name(), uid)
                .withClaim(TokenKeyEnum.TIMESTAMP.name(), String.valueOf(new Date().getTime()))
                .sign(algorithm);
    }

    public static String generateClientToken(String clientId, String scope, String uid) {
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey();
        RSAPublicKey publicKey = RSAUtil.getPublicKey();
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        String jti = UUID.randomUUID().toString();
        return JWT.create()
                .withJWTId(jti)
                .withExpiresAt(getExpiresAt(authServerProperties.getClientTokenExpireSeconds()))
                .withClaim(TokenKeyEnum.CLIENT_ID.name(), clientId)
                .withClaim(TokenKeyEnum.SCOPE.name(), scope)
                .withClaim(TokenKeyEnum.USER_ID.name(), uid)
                .withClaim(TokenKeyEnum.TIMESTAMP.name(), String.valueOf(new Date().getTime()))
                .sign(algorithm);
    }

    public static boolean verifyServerToken(String token) {
        RSAPublicKey publicKey = RSAUtil.getPublicKey();
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey();
        boolean rs = false;
        try {
            //检查jwt有效性
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            verifier.verify(token);
            rs = true;
        } catch (InvalidClaimException e) {
            log.error(ErrorInfoEnum.TOKEN_EXPIRE.getMessage(), e);
        } catch (JWTDecodeException e) {
            log.error(ErrorInfoEnum.TOKEN_INVALID.getMessage(), e);
        }
        return rs;
    }

    public static String getTokenClaim(String token, String key) {
        DecodedJWT jwt = JWT.decode(token);
        Claim claim = jwt.getClaim(key);
        if (claim == null) {
            return null;
        }
        return claim.asString();
    }

    public static Date getTokenExpiresAt(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt();
    }

    public static boolean isTokenExpire(String token) {
        Long expireTime = getTokenExpiresAt(token).getTime();
        Long nowTime = System.currentTimeMillis();
        if (expireTime < nowTime) {
            return false;
        }
        return true;
    }

}
