package cn.xhalo.blog.auth.server.service;

/**
 * @Author: xhalo
 * @Date: 2021/5/19 2:20 下午
 * @Description:
 */
public interface IAuthUserProvider<T> {
    <T> T getBaseUserByUserId(String userId);
    <T> T getDetailUserByUserId(String userId);
}
