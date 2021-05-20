package cn.xhalo.blog.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 10:00 上午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlobalUserDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private Date lastLoginTime;
    private Date createTime;
    private String headImg;
    private String introduction;
}
