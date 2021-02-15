package cn.xhalo.blog.services.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 9:53 下午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_detail")
public class UserDetailDO implements Serializable {
    @TableId
    private Long uid;
    private String headImg;
    private String introduction;
}
