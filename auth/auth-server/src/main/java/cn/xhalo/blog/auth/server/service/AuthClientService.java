package cn.xhalo.blog.auth.server.service;

import cn.xhalo.blog.auth.server.entity.AuthClient;
import cn.xhalo.blog.auth.server.util.HashUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 1:33 下午
 * @Description: 接入客户端的服务
 */
@Service
public class AuthClientService {

    private final IAuthClientProvider authClientProvider;

    private final AuthRedisService authRedisService;

    public AuthClientService(IAuthClientProvider authClientProvider, AuthRedisService authRedisService) {
        this.authClientProvider = authClientProvider;
        this.authRedisService = authRedisService;
    }

    public void registerAuthClients() {
        List<AuthClient> authClientList = authClientProvider.getAuthClients();
        authClientList.stream().forEach(client -> {
            authRedisService.set(client.getClientId(), client);
        });
    }

    public AuthClient checkClientId(String clientId) {
        return authRedisService.get(clientId, AuthClient.class);
    }


    public AuthClient validateAuthClient(String clientId, String clientSecret) {
        AuthClient client = authRedisService.get(clientId, AuthClient.class);
        if (client != null && clientSecreteEquals(clientSecret, client.getClientSecret())) {
            return client;
        }
        return null;
    }


    private boolean clientSecreteEquals(String clientSecret, String clientSecretSaved) {
        return clientSecret == null || clientSecretSaved == null ? false : clientSecretSaved.equals(HashUtil.md5(clientSecret));
    }
}
