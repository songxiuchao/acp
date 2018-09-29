package pers.acp.springboot.core.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.base.BaseInitialization;
import pers.acp.springboot.core.component.SystemControl;

/**
 * Created by zhangbin on 2017-6-16.
 * listener 系统初始化监听
 */
@Component
public class SystemInitialization extends BaseInitialization {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final SystemControl systemControl;

    @Autowired
    public SystemInitialization(SystemControl systemControl) {
        this.systemControl = systemControl;
    }

    @Override
    public String getName() {
        return "system initialization";
    }

    @Override
    public void start() {
        log.info("****************** system is starting... ******************");
        /* 启动初始化服务 */
        InitServer.startNow();
        systemControl.initialization();
    }

    @Override
    public void stop() {
        systemControl.stop();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
