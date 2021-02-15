package cn.xhalo.blog.services.user.mapper;

import cn.xhalo.blog.services.user.model.entity.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 9:55 下午
 * @Description:
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
