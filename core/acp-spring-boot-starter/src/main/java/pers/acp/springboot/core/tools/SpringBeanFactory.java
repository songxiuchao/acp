package pers.acp.springboot.core.tools;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pers.acp.core.log.LogFactory;

/**
 * Create by zhangbin on 2017-8-11 9:45
 * spring-boot 获取bean工具组件
 */
@Component
public class SpringBeanFactory implements ApplicationContextAware {

    private static final LogFactory log = LogFactory.getInstance(SpringBeanFactory.class);

    private static WebApplicationContext webApplicationContext;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringBeanFactory.applicationContext == null) {
            SpringBeanFactory.applicationContext = applicationContext;
        }
        log.info("Spring Boot ApplicationContext configuration success, can be used in a normal class");
    }

    public static void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        if (SpringBeanFactory.webApplicationContext == null) {
            SpringBeanFactory.webApplicationContext = webApplicationContext;
        }
        log.info("Spring Boot WebApplicationContext configuration success, can be used in a normal class");
    }

    /**
     * 获取applicationContext
     *
     * @return applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取applicationContext
     *
     * @return applicationContext
     */
    public static WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name 名称
     * @return bean实例
     */
    public static Object getBean(String name) {
        if (applicationContext != null) {
            return applicationContext.getBean(name);
        }
        if (webApplicationContext != null) {
            return webApplicationContext.getBean(name);
        }
        return null;
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz 类
     * @return bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext != null) {
            return applicationContext.getBean(clazz);
        }
        if (webApplicationContext != null) {
            return webApplicationContext.getBean(clazz);
        }
        return null;
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  名称
     * @param clazz 类
     * @return bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext != null) {
            return applicationContext.getBean(name, clazz);
        }
        if (webApplicationContext != null) {
            return webApplicationContext.getBean(name, clazz);
        }
        return null;
    }

    /**
     * 通过name和构造函数参数返回指定的Bean
     *
     * @param name 名称
     * @param args 参数
     * @return bean实例
     */
    public static Object getBean(String name, Object... args) {
        if (applicationContext != null) {
            return applicationContext.getBean(name, args);
        }
        if (webApplicationContext != null) {
            return webApplicationContext.getBean(name, args);
        }
        return null;
    }

    /**
     * 通过Clazz和构造函数参数返回指定的Bean
     *
     * @param clazz 类
     * @param args  参数
     * @return bean实例
     */
    public static <T> T getBean(Class<T> clazz, Object... args) {
        if (applicationContext != null) {
            return applicationContext.getBean(clazz, args);
        }
        if (webApplicationContext != null) {
            return webApplicationContext.getBean(clazz, args);
        }
        return null;
    }

}
