package cn.xhalo.blog.auth.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 10:49 上午
 * @Description:
 */
@ConfigurationProperties(prefix = "cn.xhalo.auth.server")
@Data
public class AuthServerProperties {
    private Boolean openSingleUserSinglePlace;
    /**
     *   请求token的code的过期时间，单位seconds
     */
    private Long codeExpiredSeconds;
    /**
     *   授权接入客户端token的过期时间，单位seconds
     */
    private Long clientTokenExpireSeconds;
    /**
     *   刷新客户端token的过期时间，单位minutes
     */
    private Long refreshClientTokenExpireMinutes;


    /**
     *   授权token的过期时间，单位seconds
     */
    private Long tokenExpireSeconds;
    /**
     *   刷新token的过期时间，单位minutes
     */
    private Long refreshTokenExpireMinutes;
    /**
     * token在redis中存储的时间
     */
    private Long tokenCacheSeconds;
    /**
     *   操作后是否刷新token
     */
    private Boolean refreshTokenAfterOperate;
    /**
     *   用于鉴权的rsaPrivateKey
     */
    private String tokenRSAPrivateKey;
    /**
     *   用于鉴权的rsaPublicKey
     */
    private String tokenRSAPublicKey;

    /**
     * 用来存储token的redisTemplate的bean名字
     */
    private String tokenRedisTemplateName;

    private Boolean useExistRedisTemplate;

}
