package cn.xhalo.blog.services.user.service.impl;

import cn.xhalo.blog.api.user.dto.GlobalUserDTO;
import cn.xhalo.blog.api.user.service.UserService;
import cn.xhalo.blog.auth.server.service.IAuthUserProvider;
import cn.xhalo.blog.common.constant.BaseConstant;
import cn.xhalo.blog.common.dto.PublicRequestParam;
import cn.xhalo.blog.common.dto.PublicResponseParam;
import cn.xhalo.blog.services.user.mapper.UserDetailMapper;
import cn.xhalo.blog.services.user.mapper.UserMapper;
import cn.xhalo.blog.services.user.model.entity.UserDO;
import cn.xhalo.blog.services.user.model.entity.UserDetailDO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 11:18 下午
 * @Description:
 */
@DubboService(version = BaseConstant.DUBBO_DEFAULT_VERSION, timeout = BaseConstant.DUBBO_DEFAULT_TIMEOUT, retries = BaseConstant.DUBBO_RETRY_TIMES)
public class UserServiceImpl implements UserService, IAuthUserProvider<GlobalUserDTO> {

    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;

    public UserServiceImpl(UserMapper userMapper, UserDetailMapper userDetailMapper) {
        this.userMapper = userMapper;
        this.userDetailMapper = userDetailMapper;
    }

    @Override
    public PublicResponseParam demo(PublicRequestParam publicRequestParam) {
        return PublicResponseParam.success();
    }

    @Override
    public GlobalUserDTO getBaseUserByUserId(String userId) {
        UserDO user = userMapper.selectOne(Wrappers.<cn.xhalo.blog.services.user.model.entity.UserDO>lambdaQuery()
                .eq(cn.xhalo.blog.services.user.model.entity.UserDO::getId, userId));
        if (user == null) {
            return null;
        }
        GlobalUserDTO globalUserDTO = JSONObject.parseObject(JSONObject.toJSONString(user), GlobalUserDTO.class);
        return globalUserDTO;
    }

    @Override
    public GlobalUserDTO getDetailUserByUserId(String userId) {
        UserDO baseUser = userMapper.selectOne(Wrappers.<UserDO>lambdaQuery()
                .eq(UserDO::getId, userId));
        if (baseUser == null) {
            return null;
        }
        GlobalUserDTO globalUserDTO = JSONObject.parseObject(JSONObject.toJSONString(baseUser), GlobalUserDTO.class);
        UserDetailDO detailUser = userDetailMapper.selectOne(Wrappers.<UserDetailDO>lambdaQuery()
                .eq(UserDetailDO::getUid, userId));
        if (detailUser != null) {
            globalUserDTO.setHeadImg(detailUser.getHeadImg());
            globalUserDTO.setIntroduction(detailUser.getIntroduction());
        }
        return globalUserDTO;
    }
}
