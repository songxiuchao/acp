package pers.acp.springboot.common.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pers.acp.springboot.common.interfaces.IListener;
import pers.acp.springboot.common.interfaces.ITimerTaskScheduler;
import pers.acp.springboot.core.daemon.DaemonServiceManager;
import pers.acp.springboot.core.interfaces.IDaemonService;
import pers.acp.springboot.core.tools.SpringBeanFactory;
import pers.acp.core.log.LogFactory;

import java.util.Map;

/**
 * @author zhangbin by 2018-1-20 21:24
 * @since JDK1.8
 */
@Component
@Scope("singleton")
public class SystemControl implements IDaemonService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final TimerTaskScheduler timerTaskScheduler;

    @Autowired
    public SystemControl(TimerTaskScheduler timerTaskScheduler) {
        this.timerTaskScheduler = timerTaskScheduler;
    }

    public void initialization() {
        try {
            start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        DaemonServiceManager.addService(this);
    }

    public void start() throws Exception {
        Map<String, IListener> listenerMap = SpringBeanFactory.getApplicationContext().getBeansOfType(IListener.class);
        listenerMap.forEach((key, listener) -> {
            log.info("开始启动监听：" + key + " 【" + listener.getClass().getCanonicalName() + "】");
            listener.startListener();
        });
        timerTaskScheduler.controlSchedule(ITimerTaskScheduler.START);
    }

    public void stop() {
        new Thread(() -> {
            try {
                Map<String, IListener> listenerMap = SpringBeanFactory.getApplicationContext().getBeansOfType(IListener.class);
                listenerMap.forEach((key, listener) -> {
                    log.info("开始停止监听：" + key + " 【" + listener.getClass().getCanonicalName() + "】");
                    listener.stopListener();
                });
                timerTaskScheduler.controlSchedule(ITimerTaskScheduler.STOP);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }).start();
    }

    @Override
    public String getServiceName() {
        return "系统控制服务";
    }

    @Override
    public void stopService() {
        stop();
    }
}
