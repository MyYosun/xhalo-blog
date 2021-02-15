package cn.xhalo.blog.services.article.api.fallback;

import cn.xhalo.blog.services.article.api.UserAPI;
import org.springframework.stereotype.Component;

/**
 * @Author: xhalo
 * @Date: 2021/1/20 4:42 下午
 * @Description:
 */
@Component
public class UserAPIFallback implements UserAPI {
    @Override
    public String test() {
        return "hello, i am temp service";
    }
}
