package cn.xhalo.blog.auth.client.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 1:46 下午
 * @Description:
 */
@Data
@ConfigurationProperties(prefix = "cn.xhalo.auth.client.redis")
public class AuthClientRedisProperties {
    private String host;
    private Integer port;
    private String password;
    private Integer database;
    private Integer maxActive;
    private Integer maxWait;
    private Integer maxIdle;
    private Integer minIdle;

    public boolean check() {
        return StringUtils.isEmpty(host);
    }
}
