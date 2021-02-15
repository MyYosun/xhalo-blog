package cn.xhalo.blog.common.enums;

/**
 * @Author: xhalo
 * @Date: 2021/2/15 10:33 下午
 * @Description:
 */
public enum BaseCodeConst {
    SUCCESS("0", "请求成功");

    private final String code;
    private final String message;

    BaseCodeConst(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
