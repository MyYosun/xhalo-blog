package cn.xhalo.blog.auth.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 10:49 上午
 * @Description:
 */
@ConfigurationProperties(prefix = "cn.xhalo.auth.client")
@Data
public class AuthClientProperties {
    private String clientId;
    private String clientSecret;
    private Boolean openFilter;
    private String md5Salt;

    /**
     * 用来存储token的redisTemplate的bean名字
     */
    private String tokenRedisTemplateName;

    private Boolean useExistRedisTemplate;

}
