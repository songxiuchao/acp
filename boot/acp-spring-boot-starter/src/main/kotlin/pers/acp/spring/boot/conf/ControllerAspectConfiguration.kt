package pers.acp.spring.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Controller 切面日志配置
 *
 * @author zhangbin by 14/04/2018 00:36
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.controller-aspect")
class ControllerAspectConfiguration {

    /**
     * 是否启用
     */
    var enabled = true

    /**
     * 不进行日志记录的 url 正则表达式
     * no log uri regular
     */
    var noLogUriRegular: MutableList<String> = mutableListOf()

}
