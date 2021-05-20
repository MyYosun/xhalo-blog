package cn.xhalo.blog.auth.client.config;

import cn.xhalo.blog.auth.client.enums.ErrorInfoEnum;
import cn.xhalo.blog.auth.client.exception.AuthClientException;
import cn.xhalo.blog.auth.client.interceptor.AuthClientInterceptor;
import cn.xhalo.blog.auth.client.service.AuthRedisService;
import cn.xhalo.blog.auth.client.util.SpringBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
@EnableConfigurationProperties({AuthClientProperties.class, AuthClientRedisProperties.class})
@ConditionalOnWebApplication
@Configuration
public class AuthClientAutoConfiguration {
    private final AuthClientProperties authClientProperties;
    private final AuthClientRedisProperties authClientRedisProperties;

    public AuthClientAutoConfiguration(AuthClientProperties authClientProperties, AuthClientRedisProperties authClientRedisProperties) {
        this.authClientProperties = authClientProperties;
        this.authClientRedisProperties = authClientRedisProperties;
    }


    @Bean(name = "authClientJedisPoolConfig")
    @ConditionalOnProperty(prefix = "cn.xhalo.auth.client", name = "useExistRedisTemplate", havingValue = "false", matchIfMissing = true)
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(authClientRedisProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(authClientRedisProperties.getMinIdle());
        jedisPoolConfig.setMaxTotal(authClientRedisProperties.getMaxActive());
        jedisPoolConfig.setMaxWaitMillis(authClientRedisProperties.getMaxWait());
        return jedisPoolConfig;
    }

    @Bean(name = "authClientRedisConnectionFactory")
    @ConditionalOnBean(name = "authClientJedisPoolConfig")
    @Resource(name = "authClientJedisPoolConfig")
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(authClientRedisProperties.getHost());
        redisConfiguration.setPort(authClientRedisProperties.getPort());
        redisConfiguration.setPassword(authClientRedisProperties.getPassword());
        redisConfiguration.setDatabase(authClientRedisProperties.getDatabase());
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder clientConfigurationBuilder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder();
        clientConfigurationBuilder.poolConfig(jedisPoolConfig);
        JedisConnectionFactory factory = new JedisConnectionFactory(redisConfiguration, clientConfigurationBuilder.build());
        return factory;
    }

    @Bean(name = "authClientRedisTemplate")
    @ConditionalOnBean(name = "authClientRedisConnectionFactory")
    @Resource(name = "authClientRedisConnectionFactory")
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

    @Bean(name = "authClientRedisService")
    @ConditionalOnProperty(prefix = "cn.xhalo.auth.client", name = "useExistRedisTemplate", havingValue = "true")
    public AuthRedisService authRedisServiceFromExist() {
        AuthRedisService authRedisService;
        if (StringUtils.isEmpty(authClientProperties.getTokenRedisTemplateName())) {
            throw new AuthClientException(ErrorInfoEnum.INIT_REDIS_ERROR.getCode(), ErrorInfoEnum.INIT_REDIS_ERROR.getMessage());
        }
        authRedisService = new AuthRedisService(
                SpringBeanUtil.getBean(authClientProperties.getTokenRedisTemplateName(), RedisTemplate.class)
        );
        return authRedisService;
    }

    @Bean(name = "authClientRedisService")
    @ConditionalOnBean(name = "authClientRedisTemplate")
    @Resource(name = "authClientRedisTemplate")
    public AuthRedisService authRedisServiceFromProp(RedisTemplate redisTemplate) {
        AuthRedisService authRedisService = new AuthRedisService(redisTemplate);
        return authRedisService;
    }
}
