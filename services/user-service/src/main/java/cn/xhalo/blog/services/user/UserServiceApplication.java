package cn.xhalo.blog.services.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: xhalo
 * @Date: 2021/1/20 3:40 下午
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.xhalo.blog.services.*.mapper")
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
