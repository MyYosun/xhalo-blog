package cn.xhalo.blog.auth.client.exception;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 2:00 下午
 * @Description:
 */
@Data
public class AuthClientException extends RuntimeException {
    protected String errorCode;
    protected String errorMsg;
    protected Throwable ex;

    public AuthClientException() {
        super();
    }

    public AuthClientException(String code, String message) {
        super(message);
        this.setErrorCode(code);
        this.setErrorMsg(message);
    }

    public AuthClientException(String message) {
        super(message);
        this.setErrorMsg(message);
    }

    public AuthClientException(String message, Throwable cause) {
        super(message, cause);
        this.setErrorMsg(message);
        this.setEx(cause);
    }

    public AuthClientException(Throwable cause) {
        super(cause);
        this.setEx(cause);
    }

    public AuthClientException(String code, String message, Throwable cause) {
        super(message, cause);
        this.setErrorCode(code);
        this.setErrorMsg(message);
        this.setEx(cause);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Auth Server Error");
        sb.append("\n");
        if (StringUtils.isNotEmpty(errorCode)) {
            sb.append("Error Code: ");
            sb.append(errorCode);
            sb.append("\n");
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            sb.append("Error Message: ");
            sb.append(errorMsg);
            sb.append("\n");
        }
        if (ex != null) {
            sb.append("Origin Exception: ");
            sb.append(ex.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
