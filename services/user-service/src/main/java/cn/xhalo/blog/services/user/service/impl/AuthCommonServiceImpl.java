package cn.xhalo.blog.services.user.service.impl;

import cn.xhalo.blog.api.user.dto.GlobalUserDTO;
import cn.xhalo.blog.api.user.service.AuthCommonService;
import cn.xhalo.blog.auth.server.config.AuthServerProperties;
import cn.xhalo.blog.auth.server.service.AuthClientService;
import cn.xhalo.blog.auth.server.service.AuthRedisService;
import cn.xhalo.blog.auth.server.service.DefaultAuthCommonService;
import cn.xhalo.blog.auth.server.service.IAuthUserProvider;
import cn.xhalo.blog.common.constant.BaseConstant;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: xhalo
 * @Date: 2021/5/20 2:48 下午
 * @Description:
 */
@DubboService(version = BaseConstant.DUBBO_DEFAULT_VERSION, timeout = BaseConstant.DUBBO_DEFAULT_TIMEOUT, retries = BaseConstant.DUBBO_RETRY_TIMES, interfaceClass = AuthCommonService.class)
public class AuthCommonServiceImpl extends DefaultAuthCommonService<GlobalUserDTO> implements AuthCommonService<GlobalUserDTO> {

    public AuthCommonServiceImpl(AuthClientService authClientService,
                                 AuthRedisService authServerRedisService,
                                 IAuthUserProvider authUserProvider,
                                 AuthServerProperties authServerProperties) {
        super(authClientService, authServerRedisService, authUserProvider,
                authServerProperties);
    }
}
