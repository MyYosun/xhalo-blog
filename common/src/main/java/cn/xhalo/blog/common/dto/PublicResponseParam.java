package cn.xhalo.blog.common.dto;

import cn.xhalo.blog.common.constant.BaseConstant;
import cn.xhalo.blog.common.enums.BaseCodeConst;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 10:16 下午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicResponseParam implements Serializable {
    private String code;
    private String message;
    private String version;
    @JSONField(name = "open_time")
    private Long openTime;
    private Object data;

    public static PublicResponseParam success() {
        PublicResponseParam response = new PublicResponseParam();
        response.setCode(BaseCodeConst.SUCCESS.code());
        response.setMessage(BaseCodeConst.SUCCESS.message());
        response.setOpenTime(System.currentTimeMillis());
        response.setVersion(BaseConstant.VERSION);
        return response;
    }

    public static PublicResponseParam success(Object data) {
        PublicResponseParam response = new PublicResponseParam();
        response.setCode(BaseCodeConst.SUCCESS.code());
        response.setMessage(BaseCodeConst.SUCCESS.message());
        response.setData(data);
        response.setOpenTime(System.currentTimeMillis());
        response.setVersion(BaseConstant.VERSION);
        return response;
    }

    public PublicResponseParam code(String code) {
        this.code = code;
        return this;
    }

    public PublicResponseParam message(String message) {
        this.message = message;
        return this;
    }

    public PublicResponseParam data(Object data) {
        this.data = data;
        return this;
    }

    public PublicResponseParam openTime(Long openTime) {
        this.openTime = openTime;
        return this;
    }

    public PublicResponseParam version(String version) {
        this.version = version;
        return this;
    }
}
