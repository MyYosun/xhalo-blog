package cn.xhalo.blog.services.article.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: xhalo
 * @Date: 2021/1/20 4:01 下午
 * @Description:
 */
@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
