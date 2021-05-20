package cn.xhalo.blog.services.user.service.impl;

import cn.xhalo.blog.auth.server.entity.AuthClient;
import cn.xhalo.blog.auth.server.service.IAuthClientProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xhalo
 * @Date: 2021/5/20 2:33 下午
 * @Description:
 */
@Service
public class AuthClientProvider implements IAuthClientProvider {

    @Override
    public List<AuthClient> getAuthClients() {
        List<AuthClient> res = new ArrayList<>();
        res.add(AuthClient.builder()
                .clientId("df796b43dae248d59c1d25e5689e2c8e")
                .clientSecret("3d20fa575bd2d8243689a78c06139ff2")
                .clientName("blog-rest")
                .scope("blog")
                .build());
        return res;
    }
}