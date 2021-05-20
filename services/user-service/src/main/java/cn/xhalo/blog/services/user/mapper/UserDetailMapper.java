package cn.xhalo.blog.services.user.mapper;

import cn.xhalo.blog.services.user.model.entity.UserDetailDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: xhalo
 * @Date: 2021/5/20 2:43 下午
 * @Description:
 */
@Mapper
public interface UserDetailMapper extends BaseMapper<UserDetailDO> {
}
