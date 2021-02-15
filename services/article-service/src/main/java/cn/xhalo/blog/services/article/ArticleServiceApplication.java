package cn.xhalo.blog.services.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: xhalo
 * @Date: 2021/1/20 3:50 下午
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.xhalo.blog.services.*.mapper")
public class ArticleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleServiceApplication.class, args);
    }
}
