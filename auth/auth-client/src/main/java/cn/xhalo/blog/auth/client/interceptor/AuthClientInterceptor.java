package cn.xhalo.blog.auth.client.interceptor;

import cn.xhalo.blog.auth.client.config.AuthClientProperties;
import cn.xhalo.blog.auth.client.exception.AuthClientException;
import cn.xhalo.blog.auth.client.service.AuthClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: xhalo
 * @Date: 2021/5/20 1:40 下午
 * @Description:
 */
@Slf4j
public class AuthClientInterceptor<U> implements HandlerInterceptor {

    private final AuthClientProperties authClientProperties;
    private final AuthClientService<U> authClientService;
    private static List<String> notFilterInclude = new ArrayList<>();
    private static List<String> notFilterEndWith = new ArrayList<>();

    public AuthClientInterceptor(AuthClientProperties authClientProperties, AuthClientService<U> authClientService) {
        this.authClientProperties = authClientProperties;
        this.authClientService = authClientService;
        String excludeFilterPath = this.authClientProperties.getExcludeFilterPath();
        String excludeFilterPathEndWith = this.authClientProperties.getExcludeFilterPathEndWith();
        if (StringUtils.isNotEmpty(excludeFilterPath)) {
            notFilterInclude.addAll(Arrays.asList(excludeFilterPath.split(",")));
        }
        if (StringUtils.isNotEmpty(excludeFilterPathEndWith)) {
            notFilterEndWith.addAll(Arrays.asList(excludeFilterPathEndWith.split(",")));
        }
    }

    private boolean skipAuth(HttpServletRequest request) {
        if (!authClientProperties.getOpenFilter()) {
            return true;
        }
        String requestUri = request.getRequestURI();
        boolean ifInclude = notFilterInclude.stream().anyMatch(reg -> StringUtils.contains(requestUri, reg));
        boolean ifEndWith = notFilterEndWith.stream().anyMatch(reg -> StringUtils.endsWith(requestUri, reg));
        return ifInclude || ifEndWith
                || StringUtils.equals(requestUri, request.getContextPath() + "/");
    }

    /**
     * 各client自己实现
     * @param userInfo
     */
    protected void processUserInfo(U userInfo) {

    }

    /**
     * 在请求映射前拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (skipAuth(request)) {
            return true;
        }
        boolean checkToken = authClientService.checkClientToken(request);
        if (checkToken) {
            //检查是否需要刷新token，若需要则根据配置（是否在操作后刷新token）来刷新
            authClientService.checkAndProcessRefreshAllToken(request, response);
            U baseUserInfo = authClientService.getBaseUserByClientToken(request);
            processUserInfo(baseUserInfo);
            return true;
        } else {
            try {
                authClientService.afterTokenCheckFalse(request, response);
                return true;
            } catch (AuthClientException e) {
                log.error(e.toString());
                return false;
            }
        }
    }

    /**
     * 在请求执行后拦截
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在请求完成后拦截
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
