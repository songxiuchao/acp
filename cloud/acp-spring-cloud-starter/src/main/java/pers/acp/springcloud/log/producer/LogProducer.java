package pers.acp.springcloud.log.producer;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhang by 14/01/2019 17:08
 * @since JDK 11
 */
public class LogProducer {

    public LogOutput getLogOutput() {
        return logOutput;
    }

    private final LogOutput logOutput;

    @Autowired
    public LogProducer(LogOutput logOutput) {
        this.logOutput = logOutput;
    }

}
