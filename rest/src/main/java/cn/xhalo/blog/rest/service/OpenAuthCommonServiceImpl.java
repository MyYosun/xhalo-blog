package cn.xhalo.blog.rest.service;

import cn.xhalo.blog.api.user.dto.GlobalUserDTO;
import cn.xhalo.blog.api.user.service.AuthCommonService;
import cn.xhalo.blog.auth.client.service.IAuthCommonService;
import cn.xhalo.blog.common.auth.dto.ClientTokenRes;
import cn.xhalo.blog.common.constant.BaseConstant;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @Author: xhalo
 * @Date: 2021/5/20 3:13 下午
 * @Description:
 */
@Service(value = "authCommonService")
public class OpenAuthCommonServiceImpl implements IAuthCommonService<GlobalUserDTO> {
    @DubboReference(version = BaseConstant.DUBBO_DEFAULT_VERSION, check = false, interfaceClass = AuthCommonService.class)
    private AuthCommonService authCommonService;

    @Override
    public String requestLoginIdentity(String userId, String userIp, String clientId, String clientSecret) {
        return authCommonService.requestLoginIdentity(userId, userIp, clientId, clientSecret);
    }

    @Override
    public String getGlobalToken(String userIp, String identityCode) {
        return authCommonService.getGlobalToken(userIp, identityCode);
    }

    @Override
    public String getCodeEnhance(String clientId, String globalToken, String requestIp) {
        return authCommonService.getCodeEnhance(clientId, globalToken, requestIp);
    }

    @Override
    public ClientTokenRes getClientToken(String clientId, String clientSecret, String codeEnhance) {
        return authCommonService.getClientToken(clientId, clientSecret, codeEnhance);
    }

    @Override
    public ClientTokenRes refreshClientToken(String clientId, String clientSecret, String refreshToken) {
        return authCommonService.refreshClientToken(clientId, clientSecret, refreshToken);
    }

    @Override
    public String refreshServerToken(String clientId, String clientSecret, String token, String requestIp) {
        return authCommonService.refreshServerToken(clientId, clientSecret, token, requestIp);
    }

    @Override
    public boolean checkClientToken(String token, String clientSecret, String requestIp) {
        return authCommonService.checkClientToken(token, clientSecret, requestIp);
    }

    @Override
    public void logout(String serverToken) {
        authCommonService.logout(serverToken);
    }

    @Override
    public GlobalUserDTO getBaseUserByClientToken(String clientToken) {
        return (GlobalUserDTO) authCommonService.getBaseUserByClientToken(clientToken);
    }

    @Override
    public GlobalUserDTO getDetailUserByClientToken(String clientToken) {
        return (GlobalUserDTO) authCommonService.getDetailUserByClientToken(clientToken);
    }

    @Override
    public boolean ifTokenNeedRefreshed(String token) {
        return authCommonService.ifTokenNeedRefreshed(token);
    }

    @Override
    public boolean ifClientTokenNeedRefreshed(String token) {
        return authCommonService.ifClientTokenNeedRefreshed(token);
    }
}
