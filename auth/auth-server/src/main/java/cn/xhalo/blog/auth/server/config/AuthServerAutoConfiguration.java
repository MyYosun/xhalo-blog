package cn.xhalo.blog.auth.server.config;

import cn.xhalo.blog.auth.server.exception.AuthServerException;
import cn.xhalo.blog.auth.server.enums.ErrorInfoEnum;
import cn.xhalo.blog.auth.server.service.AuthClientService;
import cn.xhalo.blog.auth.server.service.AuthRedisService;
import cn.xhalo.blog.auth.server.service.IAuthClientProvider;
import cn.xhalo.blog.auth.server.util.SpringBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 10:43 上午
 * @Description:
 */
@EnableConfigurationProperties({AuthServerProperties.class, AuthServerRedisProperties.class})
@Configuration
public class AuthServerAutoConfiguration {
    private final AuthServerProperties authServerProperties;
    private final AuthServerRedisProperties authServerRedisProperties;

    public AuthServerAutoConfiguration(AuthServerProperties authServerProperties,
                                       AuthServerRedisProperties authServerRedisProperties) {
        this.authServerProperties = authServerProperties;
        this.authServerRedisProperties = authServerRedisProperties;
    }

    @Bean(name = "authServerJedisPoolConfig")
    @ConditionalOnProperty(prefix = "cn.xhalo.auth.server", name = "useExistRedisTemplate", havingValue = "false", matchIfMissing = true)
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(authServerRedisProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(authServerRedisProperties.getMinIdle());
        jedisPoolConfig.setMaxTotal(authServerRedisProperties.getMaxActive());
        jedisPoolConfig.setMaxWaitMillis(authServerRedisProperties.getMaxWait());
        return jedisPoolConfig;
    }

    @Bean(name = "authServerRedisConnectionFactory")
    @ConditionalOnBean(name = "authServerJedisPoolConfig")
    @Resource(name = "authServerJedisPoolConfig")
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(authServerRedisProperties.getHost());
        redisConfiguration.setPort(authServerRedisProperties.getPort());
        redisConfiguration.setPassword(authServerRedisProperties.getPassword());
        redisConfiguration.setDatabase(authServerRedisProperties.getDatabase());
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder clientConfigurationBuilder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder();
        clientConfigurationBuilder.poolConfig(jedisPoolConfig);
        JedisConnectionFactory factory = new JedisConnectionFactory(redisConfiguration, clientConfigurationBuilder.build());
        return factory;
    }

    @Bean(name = "authServerRedisTemplate")
    @ConditionalOnBean(name = "authServerRedisConnectionFactory")
    @Resource(name = "authServerRedisConnectionFactory")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        //JdkSerializationRedisSerializer序列化方式;
        JdkSerializationRedisSerializer jdkRedisSerializer=new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(jdkRedisSerializer);
        redisTemplate.setHashValueSerializer(jdkRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "authServerRedisService")
    @ConditionalOnProperty(prefix = "cn.xhalo.auth.server", name = "useExistRedisTemplate", havingValue = "true")
    public AuthRedisService authRedisServiceFromExist() {
        AuthRedisService authRedisService;
        if (StringUtils.isEmpty(authServerProperties.getTokenRedisTemplateName())) {
            throw new AuthServerException(ErrorInfoEnum.INIT_REDIS_ERROR.getCode(), ErrorInfoEnum.INIT_REDIS_ERROR.getMessage());
        }
        authRedisService = new AuthRedisService(
                SpringBeanUtil.getBean(authServerProperties.getTokenRedisTemplateName(), RedisTemplate.class)
        );
        return authRedisService;
    }

    @Bean(name = "authServerRedisService")
    @ConditionalOnBean(name = "authServerRedisTemplate")
    @Resource(name = "authServerRedisTemplate")
    public AuthRedisService authRedisServiceFromProp(RedisTemplate redisTemplate) {
        AuthRedisService authRedisService = new AuthRedisService(redisTemplate);
        return authRedisService;
    }

    @Bean(name = "authClientService")
    public AuthClientService authClientService(IAuthClientProvider authClientProvider, AuthRedisService authServerRedisService) {
        return new AuthClientService(authClientProvider, authServerRedisService);
    }
}
