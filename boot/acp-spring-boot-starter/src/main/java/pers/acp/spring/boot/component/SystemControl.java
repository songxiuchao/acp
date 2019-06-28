package pers.acp.spring.boot.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pers.acp.spring.boot.daemon.DaemonServiceManager;
import pers.acp.spring.boot.interfaces.IListener;
import pers.acp.spring.boot.interfaces.ITimerTaskScheduler;
import pers.acp.core.interfaces.IDaemonService;
import pers.acp.core.log.LogFactory;

import java.util.Map;

/**
 * 系统控制器
 *
 * @author zhangbin by 2018-1-20 21:24
 * @since JDK 11
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SystemControl implements IDaemonService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final Map<String, IListener> listenerMap;

    private final TimerTaskScheduler timerTaskScheduler;

    @Autowired(required = false)
    public SystemControl(Map<String, IListener> listenerMap, TimerTaskScheduler timerTaskScheduler) {
        this.listenerMap = listenerMap;
        this.timerTaskScheduler = timerTaskScheduler;
    }

    /**
     * 系统初始化
     */
    public void initialization() {
        try {
            start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        DaemonServiceManager.addService(this);
    }

    /**
     * 系统启动
     *
     * @throws Exception 异常
     */
    public void start() throws Exception {
        if (listenerMap != null && listenerMap.size() > 0) {
            log.info("start listener begin ...");
            listenerMap.forEach((key, listener) -> {
                log.info("开始启动监听：" + key + " 【" + listener.getClass().getCanonicalName() + "】");
                listener.startListener();
            });
            log.info("start listener finished!");
        }
        timerTaskScheduler.controlSchedule(ITimerTaskScheduler.START);
    }

    /**
     * 系统停止
     */
    public void stop() {
        try {
            if (listenerMap != null && listenerMap.size() > 0) {
                log.info("stop listener begin ...");
                listenerMap.forEach((key, listener) -> {
                    log.info("开始停止监听：" + key + " 【" + listener.getClass().getCanonicalName() + "】");
                    listener.stopListener();
                });
                log.info("stop listener finished!");
            }
            timerTaskScheduler.controlSchedule(ITimerTaskScheduler.STOP);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
