package cn.xhalo.blog.services.article.api;

import cn.xhalo.blog.services.article.api.fallback.UserAPIFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: xhalo
 * @Date: 2021/1/20 4:37 下午
 * @Description:
 */
@FeignClient(value = "user-service", fallback = UserAPIFallback.class)
public interface UserAPI {
    @GetMapping("/test/01")
    String test();
}
