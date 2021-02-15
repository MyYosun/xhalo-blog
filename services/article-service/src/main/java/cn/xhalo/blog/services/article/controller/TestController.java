package cn.xhalo.blog.services.article.controller;

import cn.xhalo.blog.services.article.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: xhalo
 * @Date: 2021/1/20 3:52 下午
 * @Description:
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private UserAPI userAPI;

    @GetMapping("/01")
    public String test() {
        /*ServiceInstance serviceInstance = loadBalancerClient.choose("user-service");
        String path = String.format("http://%s:%s/test/%s", serviceInstance.getHost(), serviceInstance.getPort(), "01");
        return restTemplate.getForObject(path, String.class);*/
        return userAPI.test();
    }
}
