package cn.xhalo.blog.rest.controller;

import cn.xhalo.blog.api.user.dto.GlobalUserDTO;
import cn.xhalo.blog.auth.client.service.AuthClientService;
import cn.xhalo.blog.common.dto.PublicRequestParam;
import cn.xhalo.blog.common.dto.PublicResponseParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: xhalo
 * @Date: 2021/5/20 4:16 下午
 * @Description:
 */
@RestController
public class LoginController {

    private final AuthClientService<GlobalUserDTO> authClientService;

    public LoginController(AuthClientService<GlobalUserDTO> authClientService) {
        this.authClientService = authClientService;
    }

    @PostMapping(value = "/login")
    public PublicResponseParam login(@RequestBody PublicRequestParam requestParam,
                                     HttpServletRequest request, HttpServletResponse response) {
        authClientService.getTokenToHeader(request, response, "1");
        return PublicResponseParam.builder().build();
    }
}
