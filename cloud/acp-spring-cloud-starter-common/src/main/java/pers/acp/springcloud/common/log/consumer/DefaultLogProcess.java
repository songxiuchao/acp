package pers.acp.springcloud.common.log.consumer;

import pers.acp.core.CommonTools;
import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;
import pers.acp.springcloud.common.enums.LogLevel;
import pers.acp.springcloud.common.log.LogInfo;

/**
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
public class DefaultLogProcess implements LogProcess {

    @Override
    public void process(LogInfo logInfo) {
        // 每个日志类型启动一个线程池，池中仅有一个线程，保证每个类型的消息顺序处理
        ThreadPoolService threadPoolService = ThreadPoolService.getInstance(logInfo.getLogType() + "_log", 3000, 0, 1, Integer.MAX_VALUE);
        threadPoolService.addTask(new BaseThreadTask(logInfo.getLogType() + "_log") {
            @Override
            public boolean beforeExecuteFun() {
                return true;
            }

            @Override
            public Object executeFun() {
                doLog(logInfo);
                return true;
            }

            @Override
            public void afterExecuteFun(Object result) {

            }
        });
    }

    private void doLog(LogInfo logInfo) {
        LogFactory logFactory = LogFactory.getInstance(logInfo.getLogType());
        StringBuilder message = new StringBuilder();
        LogLevel logLevel;
        try {
            logLevel = LogLevel.getEnum(logInfo.getLogLevel());
        } catch (EnumValueUndefinedException e) {
            logFactory.error(e.getMessage(), e);
            logLevel = LogLevel.OTHER;
        }
        message.append("[ ").append(CommonTools.strFillIn(logLevel.getName(), 5, 1, " ")).append(" ] ")
                .append("[").append(logInfo.getServerName()).append("] ")
                .append("[").append(logInfo.getServerIp()).append("] ")
                .append("[").append(logInfo.getServerPort()).append("] ")
                .append("[").append(logInfo.getClassName()).append("] ")
                .append("[ ").append(logInfo.getLineno()).append(" ] - ")
                .append(logInfo.getMessage());
        Throwable throwable = logInfo.getThrowable();
        switch (logLevel) {
            case DEBUG:
                logFactory.debug(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.debug(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case WARN:
                logFactory.warn(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.warn(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case ERROR:
                logFactory.error(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.error(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case TRACE:
                logFactory.trace(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.trace(message.toString(), logInfo.getParams().toArray());
                }
                break;
            default:
                logFactory.info(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.info(message.toString(), logInfo.getParams().toArray());
                }
                break;
        }
    }

}
