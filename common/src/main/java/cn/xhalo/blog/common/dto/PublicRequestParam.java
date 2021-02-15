package cn.xhalo.blog.common.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 10:15 下午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicRequestParam implements Serializable {
    private String token;
    private String version;
    @JSONField(name = "session_id")
    private String sessionId;
    @JSONField(name = "req_data")
    private String reqData;
    @JSONField(name = "open_time")
    private Long openTime;

    public <T> T parseData(Class<T> tClass) {
        if (StringUtils.isEmpty(reqData)) {
            return null;
        }
        return JSONObject.parseObject(reqData, tClass);
    }
}
