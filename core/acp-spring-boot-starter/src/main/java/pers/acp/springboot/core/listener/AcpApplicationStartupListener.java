package pers.acp.springboot.core.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.base.BaseInitialization;
import pers.acp.core.log.LogFactory;

import java.util.*;

/**
 * @author zhangbin by 2018-1-31 12:50
 * @since JDK1.8
 */
@Component
public class AcpApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, BaseInitialization> initializationMap = event.getApplicationContext().getBeansOfType(BaseInitialization.class);
        List<Map.Entry<String, BaseInitialization>> initializationSortList = sortMap(initializationMap);
        initializationSortList.forEach(entry -> {
            log.info("start system initialization[" + entry.getValue().getOrder() + "] : " + entry.getValue().getName());
            entry.getValue().start();
        });
    }

    private List<Map.Entry<String, BaseInitialization>> sortMap(Map<String, BaseInitialization> srcMap) {
        if (srcMap == null || srcMap.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map.Entry<String, BaseInitialization>> list = new ArrayList<>(srcMap.entrySet());
        list.sort(Comparator.comparingInt(o -> o.getValue().getOrder()));
        return list;
    }

}
