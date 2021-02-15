package cn.xhalo.blog.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 10:55 下午
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RestApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(RestApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
