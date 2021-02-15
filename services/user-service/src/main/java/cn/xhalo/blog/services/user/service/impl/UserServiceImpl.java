package cn.xhalo.blog.services.user.service.impl;

import cn.xhalo.blog.api.user.service.UserService;
import cn.xhalo.blog.common.constant.BaseConstant;
import cn.xhalo.blog.common.dto.PublicRequestParam;
import cn.xhalo.blog.common.dto.PublicResponseParam;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 11:18 下午
 * @Description:
 */
@DubboService(version = BaseConstant.DUBBO_DEFAULT_VERSION, timeout = BaseConstant.DUBBO_DEFAULT_TIMEOUT)
public class UserServiceImpl implements UserService {

    @Override
    public PublicResponseParam demo(PublicRequestParam publicRequestParam) {
        return PublicResponseParam.success();
    }
}
