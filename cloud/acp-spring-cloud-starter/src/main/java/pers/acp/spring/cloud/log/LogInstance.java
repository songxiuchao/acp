package pers.acp.spring.cloud.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.spring.boot.tools.SpringBeanFactory;
import pers.acp.spring.cloud.enums.LogLevel;
import pers.acp.spring.cloud.log.producer.LogProducer;
import pers.acp.spring.cloud.conf.LogServerClientConfiguration;

import java.util.Arrays;

/**
 * 日志实例
 *
 * @author zhangbin by 11/07/2018 13:36
 * @since JDK 11
 */
@Component
public class LogInstance {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final ObjectMapper objectMapper;

    private final LogServerClientConfiguration logServerClientConfiguration;

    @Autowired
    public LogInstance(ObjectMapper objectMapper, LogServerClientConfiguration logServerClientConfiguration) {
        this.objectMapper = objectMapper;
        this.logServerClientConfiguration = logServerClientConfiguration;
    }

    private LogInfo generateLogInfo() {
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            String logType = logServerClientConfiguration.getLogType();
            if (CommonTools.isNullStr(logType)) {
                logType = LogConstant.DEFAULT_TYPE;
            }
            logInfo.setLogType(logType);
        }
        return logInfo;
    }

    private void sendToLogServer(LogInfo logInfo) {
        logInfo.setServerTime(CommonTools.getNowDateTime().toDate().getTime());
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        int lineno = 0;
        String className = "";
        if (stacks.length >= 4) {
            lineno = stacks[3].getLineNumber();
            className = stacks[3].getClassName();
        }
        logInfo.setLineno(lineno);
        logInfo.setClassName(className);
        try {
            if (logServerClientConfiguration.isEnabled()) {
                LogProducer logProducer = SpringBeanFactory.getBean(LogProducer.class);
                if (logProducer != null) {
                    logProducer.getLogOutput().sendMessage().send(MessageBuilder.withPayload(objectMapper.writeValueAsString(logInfo)).build());
                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void info(String message) {
        log.info(message);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.INFO.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void info(String message, Object... var) {
        log.info(message, var);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.INFO.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void info(String message, Throwable t) {
        log.info(message, t);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.INFO.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void debug(String message) {
        log.debug(message);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.DEBUG.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void debug(String message, Object... var) {
        log.debug(message, var);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.DEBUG.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void debug(String message, Throwable t) {
        log.debug(message, t);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.DEBUG.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void warn(String message) {
        log.warn(message);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.WARN.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void warn(String message, Object... var) {
        log.warn(message, var);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.WARN.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void warn(String message, Throwable t) {
        log.warn(message, t);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.WARN.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void error(String message) {
        log.error(message);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.ERROR.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void error(String message, Object... var) {
        log.error(message, var);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.ERROR.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void error(String message, Throwable t) {
        log.error(message, t);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.ERROR.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void trace(String message) {
        log.trace(message);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.TRACE.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void trace(String message, Object... var) {
        log.trace(message, var);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.TRACE.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void trace(String message, Throwable t) {
        log.trace(message, t);
        LogInfo logInfo = generateLogInfo();
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.TRACE.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

}
