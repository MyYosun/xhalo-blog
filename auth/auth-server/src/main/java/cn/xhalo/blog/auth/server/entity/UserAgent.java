package cn.xhalo.blog.auth.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 10:40 上午
 * @Description: 用户代理，如浏览器，移动设备
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAgent implements Serializable {
}
