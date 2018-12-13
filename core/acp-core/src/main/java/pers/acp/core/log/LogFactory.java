package pers.acp.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Create by zhangbin on 2017-11-8 15:47
 */
public class LogFactory {

    private final Logger logger;

    private LogFactory(Class<?> clas) {
        logger = LoggerFactory.getLogger(clas);
    }

    private LogFactory(String name) {
        logger = LoggerFactory.getLogger(name);
    }

    public static LogFactory getInstance(Class<?> clas) {
        return new LogFactory(clas);
    }

    public static LogFactory getInstance(String name) {
        return new LogFactory(name);
    }

    public void info(String message) {
        setCustomerParams();
        logger.info(message);
    }

    public void info(String message, Object... var) {
        setCustomerParams();
        logger.info(message, var);
    }

    public void info(String message, Throwable t) {
        setCustomerParams();
        logger.info(message, t);
    }

    public void debug(String message) {
        setCustomerParams();
        logger.debug(message);
    }

    public void debug(String message, Object... var) {
        setCustomerParams();
        logger.debug(message, var);
    }

    public void debug(String message, Throwable t) {
        setCustomerParams();
        logger.debug(message, t);
    }

    public void warn(String message) {
        setCustomerParams();
        logger.warn(message);
    }

    public void warn(String message, Object... var) {
        setCustomerParams();
        logger.warn(message, var);
    }

    public void warn(String message, Throwable t) {
        setCustomerParams();
        logger.warn(message, t);
    }

    public void error(String message) {
        setCustomerParams();
        logger.error(message);
    }

    public void error(String message, Object... var) {
        setCustomerParams();
        logger.debug(message, var);
    }

    public void error(String message, Throwable t) {
        setCustomerParams();
        logger.error(message, t);
    }

    public void trace(String message) {
        setCustomerParams();
        logger.trace(message);
    }

    public void trace(String message, Object... var) {
        setCustomerParams();
        logger.trace(message, var);
    }

    public void trace(String message, Throwable t) {
        setCustomerParams();
        logger.trace(message, t);
    }

    private void setCustomerParams() {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        int lineno = 0;
        if (stacks.length >= 4) {
            lineno = stacks[3].getLineNumber();
        }
        MDC.put("lineno", lineno + "");
    }

}
