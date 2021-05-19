package cn.xhalo.blog.auth.client.service;

import cn.xhalo.blog.common.auth.dto.ClientTokenRes;

/**
 * @Author: xhalo
 * @Date: 2021/5/19 2:34 下午
 * @Description:
 */
public interface IAuthCommonService<U> {

    String requestLoginIdentity(String userId, String userIp, String clientId, String clientSecret);

    String getGlobalToken(String userIp, String identityCode);

    String getCodeEnhance(String clientId, String globalToken, String requestIp);

    ClientTokenRes getClientToken(String clientId, String clientSecret, String codeEnhance);

    ClientTokenRes refreshClientToken(String clientId, String clientSecret, String refreshToken);

    String refreshServerToken(String clientId, String clientSecret, String token, String requestIp);

    boolean checkClientToken(String token, String clientSecret, String requestIp);

    void logout(String serverToken);

    U getBaseUserByClientToken(String clientToken);

    U getDetailUserByClientToken(String clientToken);

    boolean ifTokenNeedRefreshed(String token);

    boolean ifClientTokenNeedRefreshed(String token);

}
