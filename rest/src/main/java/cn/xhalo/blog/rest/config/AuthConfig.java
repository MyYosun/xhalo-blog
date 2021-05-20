package cn.xhalo.blog.rest.config;

import cn.xhalo.blog.api.user.dto.GlobalUserDTO;
import cn.xhalo.blog.auth.client.config.AuthClientProperties;
import cn.xhalo.blog.auth.client.interceptor.AuthClientInterceptor;
import cn.xhalo.blog.auth.client.service.AuthClientService;
import cn.xhalo.blog.auth.client.service.AuthRedisService;
import cn.xhalo.blog.auth.client.service.IAuthCommonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xhalo
 * @Date: 2021/5/20 3:16 下午
 * @Description:
 */
@Configuration
public class AuthConfig {
    @Bean
    public AuthClientService<GlobalUserDTO> authClientService(IAuthCommonService<GlobalUserDTO> authCommonService,
                                                              AuthRedisService authClientRedisService, AuthClientProperties authClientProperties) {
        return new AuthClientService<>(authCommonService, authClientRedisService, authClientProperties);
    }

    @Bean
    public AuthClientInterceptor<GlobalUserDTO> authClientInterceptor(AuthClientProperties authClientProperties,
                                                                      AuthClientService<GlobalUserDTO> authClientService) {
        return new AuthClientInterceptor<>(authClientProperties, authClientService);
    }
}
