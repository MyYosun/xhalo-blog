package cn.xhalo.blog.auth.server.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 12:08 下午
 * @Description:
 */
@Slf4j
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        T bean = null;
        if (null == applicationContext) {
            return bean;
        }

        try {
            bean = applicationContext.getBean(clazz);
        } catch (BeansException e) {
            log.error("获取Spring容器中的Bean失败,获取的Bean的class为: {}", clazz.getName());
        }

        return bean;
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        T bean = null;
        if (null == applicationContext) {
            return bean;
        }

        try {
            bean = applicationContext.getBean(name, clazz);
        } catch (BeansException e) {
            log.error("获取Spring容器中的Bean失败,获取的Bean的name为: {}, class为: {}",
                    name, clazz.getName());
        }

        return bean;
    }
}
