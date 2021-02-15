package cn.xhalo.blog.services.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 9:48 下午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class UserDO implements Serializable {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private Date lastLoginTime;
    private Date createTime;
    @TableField(exist = false)
    private UserDetailDO userDetail;
}
