package cn.xhalo.blog.auth.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: xhalo
 * @Date: 2021/5/19 10:59 上午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientToken implements Serializable {
    String token;
    String userId;
    String clientId;
    String scope;
}
