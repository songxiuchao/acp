package pers.acp.springboot.base;

import pers.acp.core.log.LogFactory;

/**
 * @author zhangbin by 2018-1-31 13:04
 * @since JDK 11
 */
public abstract class BaseInitialization {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    public boolean isRunning() {
        return running;
    }

    public abstract String getName();

    public abstract void start();

    public abstract void stop();

    public abstract int getOrder();

    public void startInitialization() {
        if (!running) {
            start();
        } else {
            log.info(getName() + " is already running!");
        }
    }

    public void stopInitialization() {
        if (running) {
            stop();
        } else {
            log.info(getName() + " is not running!");
        }
    }

    private boolean running = false;

}
