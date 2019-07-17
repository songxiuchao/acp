package pers.acp.spring.boot.tools

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext
import pers.acp.core.log.LogFactory

/**
 * Create by zhangbin on 2017-8-11 9:45
 * spring-boot 获取bean工具组件
 */
@Component
class SpringBeanFactory : ApplicationContextAware {

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        if (SpringBeanFactory.applicationContext == null) {
            SpringBeanFactory.applicationContext = applicationContext
        }
        log.info("Spring Boot ApplicationContext configuration Success, can be used in a normal class")
    }

    companion object {

        private val log = LogFactory.getInstance(SpringBeanFactory::class.java)

        private var webApplicationContext: WebApplicationContext? = null

        private var applicationContext: ApplicationContext? = null

        @JvmStatic
        fun setWebApplicationContext(webApplicationContext: WebApplicationContext) {
            if (SpringBeanFactory.webApplicationContext == null) {
                SpringBeanFactory.webApplicationContext = webApplicationContext
            }
            log.info("Spring Boot WebApplicationContext configuration Success, can be used in a normal class")
        }

        /**
         * 获取applicationContext
         *
         * @return applicationContext
         */
        @JvmStatic
        fun getApplicationContext(): ApplicationContext? {
            return applicationContext
        }

        /**
         * 获取applicationContext
         *
         * @return applicationContext
         */
        @JvmStatic
        fun getWebApplicationContext(): WebApplicationContext? {
            return webApplicationContext
        }

        /**
         * 通过name获取 Bean.
         *
         * @param name 名称
         * @return bean实例
         */
        @JvmStatic
        fun getBean(name: String): Any? {
            if (applicationContext != null) {
                return applicationContext!!.getBean(name)
            }
            return if (webApplicationContext != null) {
                webApplicationContext!!.getBean(name)
            } else null
        }

        /**
         * 通过class获取Bean.
         *
         * @param clazz 类
         * @return bean实例
         */
        @JvmStatic
        fun <T> getBean(clazz: Class<T>): T? {
            if (applicationContext != null) {
                return applicationContext!!.getBean(clazz)
            }
            return if (webApplicationContext != null) {
                webApplicationContext!!.getBean(clazz)
            } else null
        }

        /**
         * 通过name,以及Clazz返回指定的Bean
         *
         * @param name  名称
         * @param clazz 类
         * @return bean实例
         */
        @JvmStatic
        fun <T> getBean(name: String, clazz: Class<T>): T? {
            if (applicationContext != null) {
                return applicationContext!!.getBean(name, clazz)
            }
            return if (webApplicationContext != null) {
                webApplicationContext!!.getBean(name, clazz)
            } else null
        }

        /**
         * 通过name和构造函数参数返回指定的Bean
         *
         * @param name 名称
         * @param args 参数
         * @return bean实例
         */
        @JvmStatic
        fun getBean(name: String, vararg args: Any): Any? {
            if (applicationContext != null) {
                return applicationContext!!.getBean(name, *args)
            }
            return if (webApplicationContext != null) {
                webApplicationContext!!.getBean(name, *args)
            } else null
        }

        /**
         * 通过Clazz和构造函数参数返回指定的Bean
         *
         * @param clazz 类
         * @param args  参数
         * @return bean实例
         */
        @JvmStatic
        fun <T> getBean(clazz: Class<T>, vararg args: Any): T? {
            if (applicationContext != null) {
                return applicationContext!!.getBean(clazz, *args)
            }
            return if (webApplicationContext != null) {
                webApplicationContext!!.getBean(clazz, *args)
            } else null
        }
    }

}
