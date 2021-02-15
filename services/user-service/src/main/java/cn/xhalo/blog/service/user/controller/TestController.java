package cn.xhalo.blog.service.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xhalo
 * @Date: 2021/1/20 3:41 下午
 * @Description:
 */
@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping(value = "/01")
    public String test() {
        return "hello, i am user service";
    }


}
