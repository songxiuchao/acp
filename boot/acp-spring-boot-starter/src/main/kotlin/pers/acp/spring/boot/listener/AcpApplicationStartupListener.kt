package pers.acp.spring.boot.listener

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import pers.acp.spring.boot.base.BaseInitialization
import pers.acp.core.log.LogFactory

import java.util.*

/**
 * SpringBoot 应用初始化
 *
 * @author zhangbin by 2018-1-31 12:50
 * @since JDK 11
 */
@Component
class AcpApplicationStartupListener : ApplicationListener<ContextRefreshedEvent> {

    private val log = LogFactory.getInstance(this.javaClass)

    /**
     * 监听 ContextRefreshedEvent 事件
     *
     * @param event 事件对象
     */
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        sortMap(event.applicationContext.getBeansOfType(BaseInitialization::class.java)).forEach { entry ->
            log.info("start system initialization[" + entry.value.order + "] : " + entry.value.name)
            entry.value.start()
        }
    }

    private fun sortMap(srcMap: MutableMap<String, BaseInitialization>): List<MutableMap.MutableEntry<String, BaseInitialization>> {
        if (srcMap.isEmpty()) {
            return listOf()
        }
        return srcMap.entries.toList().sortedBy { it.value.order }
    }

}
