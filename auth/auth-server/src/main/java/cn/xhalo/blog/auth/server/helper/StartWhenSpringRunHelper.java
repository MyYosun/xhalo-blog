package cn.xhalo.blog.auth.server.helper;

import cn.xhalo.blog.auth.server.service.AuthClientService;
import cn.xhalo.blog.auth.server.util.RSAUtil;
import cn.xhalo.blog.auth.server.util.TokenUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 12:13 下午
 * @Description:
 */
public class StartWhenSpringRunHelper implements ApplicationRunner {
    private final AuthClientService authClientService;

    public StartWhenSpringRunHelper(AuthClientService authClientService) {
        this.authClientService = authClientService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TokenUtil.init();
        RSAUtil.init();
        authClientService.registerAuthClients();
    }
}
