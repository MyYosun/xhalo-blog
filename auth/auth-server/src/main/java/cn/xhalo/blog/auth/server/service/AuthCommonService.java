package cn.xhalo.blog.auth.server.service;

import cn.xhalo.blog.auth.server.config.AuthServerProperties;
import cn.xhalo.blog.auth.server.constant.AuthServerGlobalConstant;
import cn.xhalo.blog.auth.server.dto.ClientTokenRes;
import cn.xhalo.blog.auth.server.entity.AuthClient;
import cn.xhalo.blog.auth.server.entity.ClientToken;
import cn.xhalo.blog.auth.server.enums.TokenKeyEnum;
import cn.xhalo.blog.auth.server.util.DateUtil;
import cn.xhalo.blog.auth.server.util.TokenUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 4:32 下午
 * @Description: 正常步骤，登录->获取server token->获取鉴权码codeEnhance->获取client token
 */
@Service
public class AuthCommonService<U> {

    private final AuthClientService authClientService;
    private final AuthRedisService authRedisService;

    private final IAuthUserProvider<U> authUserProvider;

    private final AuthServerProperties authServerProperties;

    public AuthCommonService(AuthClientService authClientService, AuthRedisService authRedisService,
                             IAuthUserProvider authUserProvider, AuthServerProperties authServerProperties) {
        this.authClientService = authClientService;
        this.authRedisService = authRedisService;
        this.authUserProvider = authUserProvider;
        this.authServerProperties = authServerProperties;
    }


    /**
     * 登录码，一般在登录成功后
     * @param userId
     * @param userIp
     * @param clientId
     * @param clientSecret
     * @return
     */
    public String requestLoginIdentity(String userId, String userIp, String clientId, String clientSecret) {
        if (StringUtils.isAnyEmpty(userId, userIp, clientId, clientSecret)) {
            return null;
        }
        AuthClient client = authClientService.validateAuthClient(clientId, clientSecret);
        if (client == null) {
            return null;
        }
        String identifyCode = UUID.randomUUID().toString().replace("-", "");
        JSONObject identifyData = new JSONObject();
        identifyData.put("userIp", userIp);
        identifyData.put("userId", userId);
        authRedisService.setEx(identifyCode, identifyData, AuthServerGlobalConstant.IDENTITY_CODE_EXPIRE);
        return identifyCode;
    }

    /**
     * 获取server token，一般在获取到登录码后
     * @param userIp
     * @param identityCode
     * @return
     */
    public String getGlobalToken(String userIp, String identityCode) {
        JSONObject identityData = authRedisService.getAndSet(identityCode, new JSONObject(), JSONObject.class);
        if (identityData == null || identityData.isEmpty()) {
            return null;
        }
        if (!StringUtils.equals(identityData.getString("userIp"), userIp)) {
            return null;
        }
        String userId = identityData.getString("userId");
        String globalToken = TokenUtil.generateServerToken(userId);
        authRedisService.setEx(userId, userIp, authServerProperties.getTokenExpireSeconds());
        return globalToken;
    }

    /**
     * 获取client用于获取client token的鉴权码，一般在获取到server token后
     * @param clientId
     * @param globalToken
     * @param requestIp
     * @return
     */
    public String getCodeEnhance(String clientId, String globalToken, String requestIp) {
        if (StringUtils.isAnyEmpty(clientId, globalToken)) {
            return null;
        }
        AuthClient authClient = authClientService.checkClientId(clientId);
        if (authClient == null) {
            return null;
        }

        if (StringUtils.isEmpty(globalToken) || !TokenUtil.verifyToken(globalToken)) {
            return null;
        }
        String userId = TokenUtil.getTokenClaim(globalToken, TokenKeyEnum.USER_ID.name());
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        String userIp = authRedisService.getString(userId);
        if ((!StringUtils.equals(userIp, requestIp)) && authServerProperties.getOpenSingleUserSinglePlace()) {
            return null;
        }
        if (authServerProperties.getRefreshTokenAfterOperate()) {
            authRedisService.setEx(userId, userIp, authServerProperties.getTokenExpireSeconds());
        }
        return generateAndStoreCodeEnhance(userId);
    }

    /**
     * 客户端获取token，一般在获取到鉴权码后
     * @param clientId
     * @param clientSecret
     * @param codeEnhance
     * @return
     */
    public ClientTokenRes getClientToken(String clientId, String clientSecret, String codeEnhance) {
        if (StringUtils.isAnyEmpty(clientId, clientSecret, codeEnhance)) {
            return null;
        }
        AuthClient authClient = authClientService.validateAuthClient(clientId, clientSecret);
        if (authClient == null) {
            return null;
        }
        String userId = validateCode(codeEnhance);

        if (StringUtils.isEmpty(userId)){
            return null;
        }
        String clientToken = TokenUtil.generateClientToken(authClient.getClientId(), authClient.getScope(), userId);
        String refreshToken = generateAndStoreRefreshToken(userId, authClient.getClientId(), authClient.getScope(), clientToken);
        return ClientTokenRes.builder()
                .accessToken(clientToken)
                .refreshToken(refreshToken)
                .expireAt(TokenUtil.getTokenExpiresAt(clientToken))
                .expireAtTimestamp(TokenUtil.getTokenExpiresAt(clientToken).getTime())
                .build();
    }

    public ClientTokenRes refreshClientToken(String clientId, String clientSecret, String refreshToken) {
        if (StringUtils.isAnyEmpty(clientId, clientSecret, refreshToken)) {
            return null;
        }
        AuthClient authClient = authClientService.validateAuthClient(clientId, clientSecret);
        if (authClient == null) {
            return null;
        }
        ClientToken clientToken = authRedisService.get(refreshToken, ClientToken.class);
        if (clientToken == null) {
            return null;
        }
        if (!StringUtils.equals(clientId, clientToken.getClientId())) {
            return null;
        }
        authRedisService.delete(refreshToken);
        String oldToken = clientToken.getToken();
        if (TokenUtil.isTokenExpire(oldToken)) {
            return null;
        }
        String userId = TokenUtil.getTokenClaim(oldToken, TokenKeyEnum.USER_ID.name());
        /**
         * 验证server端是否还登录有效
         */
        if (!authRedisService.existKey(userId)) {
            return null;
        }
        String newToken = TokenUtil.generateClientToken(authClient.getClientId(), authClient.getScope(), userId);
        String newRefreshToken = generateAndStoreRefreshToken(userId, authClient.getClientId(), authClient.getScope(), newToken);
        return ClientTokenRes.builder()
                .accessToken(newToken)
                .refreshToken(newRefreshToken)
                .expireAt(TokenUtil.getTokenExpiresAt(newToken))
                .expireAtTimestamp(TokenUtil.getTokenExpiresAt(newToken).getTime())
                .build();

    }

    public boolean checkClientToken(String token, String clientSecret, String requestIp) {
        if (StringUtils.isAnyEmpty(token, clientSecret, requestIp)) {
            return false;
        }
        if (authClientService.validateAuthClient(
                TokenUtil.getTokenClaim(token, TokenKeyEnum.USER_ID.name()), clientSecret) == null) {
            return false;
        }
        if ((!TokenUtil.verifyToken(token)) || isTokenDeprecated(token, requestIp)) {
            return false;
        }
        return true;
    }

    /**
     * 通过serverToken登出，并清除redis中用户的登录信息，由于用户校验token的时候会检查redis中的用户登录，因此客户端也间接登出了
     * @param serverToken
     */
    public void logout(String serverToken) {
        if (StringUtils.isEmpty(serverToken)) {
            return;
        }
        if (!TokenUtil.verifyToken(serverToken)) {
            return;
        }
        String userId = TokenUtil.getTokenClaim(serverToken, TokenKeyEnum.USER_ID.name());
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        authRedisService.delete(userId);
    }

    /**
     * 通过client token获取用户基础信息
     * @param clientToken
     * @return
     */
    public U getBaseUserByClientToken(String clientToken) {
        if (StringUtils.isEmpty(clientToken)) {
            return null;
        }
        String userId = TokenUtil.getTokenClaim(clientToken, TokenKeyEnum.USER_ID.name());
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return authUserProvider.getBaseUserByUserId(userId);
    }

    /**
     * 通过client token获取用户详细信息
     * @param clientToken
     * @return
     */
    public U getDetailUserByClientToken(String clientToken) {
        if (StringUtils.isEmpty(clientToken)) {
            return null;
        }
        String userId = TokenUtil.getTokenClaim(clientToken, TokenKeyEnum.USER_ID.name());
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return authUserProvider.getBaseUserByUserId(userId);
    }


    /**
     * 检查token是否需要刷新
     *
     * @param token
     * @return
     */
    public boolean ifTokenNeedRefreshed(String token) {
        Date expiresAt = TokenUtil.getTokenExpiresAt(token);
        Date now = new Date();
        if (now.compareTo(expiresAt) >= 0 || DateUtil.getDiffMinutes(now, expiresAt) > authServerProperties.getRefreshClientTokenExpireMinutes()) {//已过期 或者 还没到刷新时间点阈值，就不用刷新
            return false;
        }
        return true;
    }

    public String refreshToken(String token) {
        String userId = TokenUtil.getTokenClaim(token, TokenKeyEnum.USER_ID.name());
        return TokenUtil.generateServerToken(userId);
    }

    public String generateAndStoreCodeEnhance(String userId) {
        String code = UUID.randomUUID().toString().replace("-", "");
        authRedisService.setEx(code, userId, authServerProperties.getCodeExpiredSeconds());
        return code;
    }

    public String validateCode(String code) {
        String userId = authRedisService.getString(code);
        return userId;
    }

    public String generateAndStoreRefreshToken(String userId, String clientId, String scope, String token) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        ClientToken clientToken = ClientToken.builder()
                .userId(userId)
                .clientId(clientId)
                .scope(scope)
                .token(token)
                .build();
        authRedisService.setEx(refreshToken, clientToken, authServerProperties.getRefreshClientTokenExpireMinutes(), TimeUnit.MINUTES);
        return refreshToken;
    }

    public boolean isTokenDeprecated(String token, String requestIp) {
        String userId = TokenUtil.getTokenClaim(token, TokenKeyEnum.USER_ID.name());
        if (StringUtils.isEmpty(userId)) {
            return true;
        }
        String userIp = authRedisService.getString(userId);
        if (StringUtils.isEmpty(userIp)) {
            return true;
        }
        if ((!StringUtils.equals(requestIp, userId)) && authServerProperties.getOpenSingleUserSinglePlace()) {
            return true;
        }
        if (authServerProperties.getRefreshTokenAfterOperate()) {
            authRedisService.setEx(userId, requestIp, authServerProperties.getTokenExpireSeconds());
        }
        return false;
    }

}
