package cn.xhalo.blog.auth.client.service;

import cn.xhalo.blog.auth.client.config.AuthClientProperties;
import cn.xhalo.blog.auth.client.enums.ErrorInfoEnum;
import cn.xhalo.blog.auth.client.enums.RequestHeaderKeyEnum;
import cn.xhalo.blog.auth.client.exception.AuthClientException;
import cn.xhalo.blog.auth.client.util.MD5Util;
import cn.xhalo.blog.auth.client.util.RequestUtil;
import cn.xhalo.blog.auth.client.util.TokenUtil;
import cn.xhalo.blog.common.auth.dto.ClientTokenRes;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @Author: xhalo
 * @Date: 2021/5/19 2:56 下午
 * @Description:
 */
public class AuthClientService<U> {
    private final IAuthCommonService<U> authCommonService;
    private final AuthRedisService authRedisService;

    private final AuthClientProperties authClientProperties;

    private static String md5salt;
    private static String clientId;
    private static String clientSecret;

    public AuthClientService(IAuthCommonService<U> authCommonService, AuthRedisService authRedisService, AuthClientProperties authClientProperties) {
        this.authCommonService = authCommonService;
        this.authRedisService = authRedisService;
        this.authClientProperties = authClientProperties;

        md5salt = authClientProperties.getMd5Salt();
        clientId = authClientProperties.getClientId();
        clientSecret = authClientProperties.getClientSecret();
    }

    /**
     * 获取全局和客户端的token
     * @param request
     * @param response
     * @param userId
     * @throws AuthClientException
     */
    public void getTokenToHeader(HttpServletRequest request, HttpServletResponse response, String userId) throws AuthClientException {
        String requestIp = RequestUtil.getRemoteAddr(request);
        String identityCode = authCommonService.requestLoginIdentity(userId, requestIp,
                clientId, clientSecret);
        if (identityCode == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_GLOBAL_CODE_ERROR.getCode(), ErrorInfoEnum.GET_GLOBAL_CODE_ERROR.getMessage());
        }
        String globalToken = authCommonService.getGlobalToken(requestIp, identityCode);
        if (globalToken == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_GLOBAL_TOKEN_ERROR.getCode(), ErrorInfoEnum.GET_GLOBAL_TOKEN_ERROR.getMessage());
        }
        String codeEnhance = authCommonService.getCodeEnhance(clientId, globalToken, requestIp);
        if (codeEnhance == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_CLIENT_CODE_ERROR.getCode(), ErrorInfoEnum.GET_CLIENT_CODE_ERROR.getMessage());
        }
        ClientTokenRes clientToken = authCommonService.getClientToken(clientId, clientSecret, codeEnhance);
        if (clientToken == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_CLIENT_TOKEN_ERROR.getCode(), ErrorInfoEnum.GET_CLIENT_TOKEN_ERROR.getMessage());
        }
        setGlobalToken(response, globalToken);
        setClientToken(response, clientToken);
    }

    /**
     * 在token校验失败后处理
     * @param request
     * @param response
     * @throws AuthClientException
     */
    public void afterTokenCheckFalse(HttpServletRequest request, HttpServletResponse response) throws AuthClientException {
        String globalToken = getGlobalToken(request);
        if (StringUtils.isEmpty(globalToken)) {
            throw new AuthClientException(ErrorInfoEnum.TOKEN_INVALID.getCode(), ErrorInfoEnum.TOKEN_INVALID.getMessage());
        }
        String requestIp = RequestUtil.getRemoteAddr(request);
        String codeEnhance = authCommonService.getCodeEnhance(clientId, globalToken, requestIp);
        if (codeEnhance == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_CLIENT_CODE_ERROR.getCode(), ErrorInfoEnum.GET_CLIENT_CODE_ERROR.getMessage());
        }
        ClientTokenRes clientToken = authCommonService.getClientToken(clientId, clientSecret, codeEnhance);
        if (clientToken == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_CLIENT_TOKEN_ERROR.getCode(), ErrorInfoEnum.GET_CLIENT_TOKEN_ERROR.getMessage());
        }
        setClientToken(response, clientToken);
    }

    /**
     * 刷新全局token，在server端会自动判断是否需要刷新
     * @param request
     * @param response
     */
    public void refreshServerToken(HttpServletRequest request, HttpServletResponse response) {
        String requestIp = RequestUtil.getRemoteAddr(request);
        String globalToken = getGlobalToken(request);
        if (globalToken == null) {
            throw new AuthClientException(ErrorInfoEnum.TOKEN_EXPIRE.getCode(), ErrorInfoEnum.TOKEN_EXPIRE.getMessage());
        }
        String newGlobalToken = authCommonService.refreshServerToken(clientId, clientSecret, globalToken, requestIp);
        if (newGlobalToken == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_GLOBAL_TOKEN_ERROR.getCode(), ErrorInfoEnum.GET_GLOBAL_TOKEN_ERROR.getMessage());
        }
        setGlobalToken(response, newGlobalToken);
    }

    /**
     * 刷新客户端token，需手动判断是否需要更新
     * @param request
     * @param response
     */
    public void refreshClientToken(HttpServletRequest request, HttpServletResponse response) {
        String requestIp = RequestUtil.getRemoteAddr(request);
        ClientTokenRes clientToken = getClientToken(request);
        if (clientToken == null) {
            throw new AuthClientException(ErrorInfoEnum.TOKEN_EXPIRE.getCode(), ErrorInfoEnum.TOKEN_EXPIRE.getMessage());
        }
        ClientTokenRes newClientToken = authCommonService.refreshClientToken(clientId, clientSecret, clientToken.getRefreshToken());
        if (newClientToken == null) {
            throw new AuthClientException(ErrorInfoEnum.GET_CLIENT_TOKEN_ERROR.getCode(), ErrorInfoEnum.GET_CLIENT_TOKEN_ERROR.getMessage());
        }
        setClientToken(response, newClientToken);
    }

    public void processLogout(HttpServletRequest request) {
        String token = getGlobalToken(request);
        authCommonService.logout(token);
    }

    public boolean checkClientToken(HttpServletRequest request) {
        ClientTokenRes clientTokenRes = getClientToken(request);
        if (clientTokenRes == null) {
            return false;
        }
        String requestIp = RequestUtil.getRemoteAddr(request);
        return authCommonService.checkClientToken(clientTokenRes.getAccessToken(), clientSecret, requestIp);
    }

    public void checkAndProcessRefreshAllToken(HttpServletRequest request, HttpServletResponse response) {
        ClientTokenRes clientTokenRes = getClientToken(request);
        if (clientTokenRes != null) {
            if (authCommonService.ifClientTokenNeedRefreshed(clientTokenRes.getAccessToken())) {
                refreshClientToken(request, response);
            }
        }

        String globalToken = getGlobalToken(request);
        if (StringUtils.isNoneEmpty(globalToken)) {
            if (authCommonService.ifTokenNeedRefreshed(globalToken)) {
                refreshServerToken(request, response);
            }
        }
    }

    public U getBaseUserByClientToken(HttpServletRequest request) {
        ClientTokenRes clientTokenRes = getClientToken(request);
        if (clientTokenRes == null) {
            return null;
        }
        return authCommonService.getBaseUserByClientToken(clientTokenRes.getAccessToken());
    }

    public U getDetailUserByClientToken(HttpServletRequest request) {
        ClientTokenRes clientTokenRes = getClientToken(request);
        if (clientTokenRes == null) {
            return null;
        }
        return authCommonService.getDetailUserByClientToken(clientTokenRes.getAccessToken());
    }

    public void setGlobalToken(HttpServletResponse response, String globalToken) {
        Date expire = TokenUtil.getTokenExpiresAt(globalToken);
        Long expireSeconds = (expire.getTime() - System.currentTimeMillis()) / 1000;
        if (expireSeconds < 0) {
            return;
        }
        String encodedGlobalToken = MD5Util.md5(globalToken, md5salt);
        authRedisService.setEx(encodedGlobalToken, globalToken, expireSeconds);
        response.setHeader(RequestHeaderKeyEnum.GLOBAL_TOKEN.name(), encodedGlobalToken);
    }

    public void setClientToken(HttpServletResponse response, ClientTokenRes clientToken) {
        Date expire = TokenUtil.getTokenExpiresAt(clientToken.getAccessToken());
        Long expireSeconds = (expire.getTime() - System.currentTimeMillis()) / 1000;
        if (expireSeconds < 0) {
            return;
        }
        String clientTokenJSON = JSONObject.toJSONString(clientToken);
        String encodedClientToken = MD5Util.md5(clientTokenJSON, md5salt);
        authRedisService.setEx(encodedClientToken, clientTokenJSON, expireSeconds);
        response.setHeader(RequestHeaderKeyEnum.CLIENT_TOKEN.name(), encodedClientToken);
    }

    public String getGlobalToken(HttpServletRequest request) {
        String encodedGlobalToken = request.getHeader(RequestHeaderKeyEnum.GLOBAL_TOKEN.name());
        if (StringUtils.isEmpty(encodedGlobalToken)) {
            return null;
        }
        String globalToken = authRedisService.getString(encodedGlobalToken);
        return globalToken;
    }


    public ClientTokenRes getClientToken(HttpServletRequest request) {
        String encodedClientToken = request.getHeader(RequestHeaderKeyEnum.CLIENT_TOKEN.name());
        if (StringUtils.isEmpty(encodedClientToken)) {
            return null;
        }
        String clientToken = authRedisService.getString(encodedClientToken);
        if (StringUtils.isEmpty(clientToken)) {
            return null;
        }
        try {
            ClientTokenRes clientTokenRes = JSONObject.parseObject(clientToken, ClientTokenRes.class);
            return clientTokenRes;
        } catch (Exception e) {
            return null;
        }
    }
}
