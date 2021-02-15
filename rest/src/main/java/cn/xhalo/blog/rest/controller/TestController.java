package cn.xhalo.blog.rest.controller;

import cn.xhalo.blog.api.user.service.UserService;
import cn.xhalo.blog.common.constant.BaseConstant;
import cn.xhalo.blog.common.dto.PublicRequestParam;
import cn.xhalo.blog.common.dto.PublicResponseParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 11:25 下午
 * @Description:
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {
    @DubboReference(version = BaseConstant.DUBBO_DEFAULT_VERSION)
    private UserService userService;

    @PostMapping(value = "/demo")
    public PublicResponseParam demo(@RequestBody PublicRequestParam publicRequestParam) {
        return userService.demo(publicRequestParam);
    }
}
