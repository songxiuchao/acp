package pers.acp.management.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.base.BaseSpringBootScheduledTask;
import pers.acp.management.domain.OnlineUserDomain;
import pers.acp.core.log.LogFactory;
import pers.acp.management.conf.SystemConfiguration;

/**
 * @author zhangbin by 2018-1-20 20:53
 * @since JDK1.8
 */
@Component("onlineUserClean")
@Scope("singleton")
public class OnlineUserClean extends BaseSpringBootScheduledTask {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final SystemConfiguration systemConfiguration;

    private final OnlineUserDomain onlineUserDomain;

    @Autowired
    public OnlineUserClean(SystemConfiguration systemConfiguration, OnlineUserDomain onlineUserDomain) {
        this.systemConfiguration = systemConfiguration;
        this.onlineUserDomain = onlineUserDomain;
        setTaskName("清理登录超时用户任务");
    }

    @Override
    public boolean beforeExcuteFun() {
        log.debug("开始执行清理登录超时用户任务");
        return true;
    }

    @Override
    public Object excuteFun() {
        try {
            onlineUserDomain.doClean(systemConfiguration.getUserLoginTimeOut());
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterExcuteFun(Object result) {
        log.debug("清理登录超时用户完成");
    }

}
