package cn.xhalo.blog.services.user.service.impl;

import cn.xhalo.blog.api.user.service.AuthService;
import cn.xhalo.blog.common.constant.BaseConstant;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 10:06 上午
 * @Description:
 */
@DubboService(version = BaseConstant.DUBBO_DEFAULT_VERSION, timeout = BaseConstant.DUBBO_DEFAULT_TIMEOUT, retries = BaseConstant.DUBBO_RETRY_TIMES)
public class AuthServiceImpl implements AuthService {
}
