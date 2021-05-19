package cn.xhalo.blog.auth.server.service;

import cn.xhalo.blog.auth.server.entity.AuthClient;

import java.util.List;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 1:31 下午
 * @Description: 需要实际的server实现
 */
public interface IAuthClientProvider {
    List<AuthClient> getAuthClients();
}
