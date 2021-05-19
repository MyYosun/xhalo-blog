package cn.xhalo.blog.auth.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 1:28 下午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthClient implements Serializable {
    private String clientId;
    private String clientName;
    private String clientSecret;
    private String scope;
}
