package cn.xhalo.blog.common.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xhalo
 * @Date: 2021/5/19 11:02 上午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientTokenRes implements Serializable {
    private String accessToken;
    private String refreshToken;
    private Date expireAt;
    private Long expireAtTimestamp;
}
