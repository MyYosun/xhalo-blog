package cn.xhalo.blog.auth.server.enums;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 2:09 下午
 * @Description:
 */
public enum ErrorInfoEnum {
    INIT_REDIS_ERROR("101", "身份认证服务初始化redis错误，请检查是否已配置相关配置"),
    TOKEN_EXPIRE("102", "token已过期"),
    TOKEN_INVALID("103", "token无效");

    private String code;
    private String message;

    ErrorInfoEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
