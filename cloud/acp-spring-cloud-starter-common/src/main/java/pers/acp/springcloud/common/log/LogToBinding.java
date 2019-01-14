package pers.acp.springcloud.common.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.kafka.config.KafkaBinderConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author zhang by 14/01/2019 17:08
 * @since JDK 11
 */
@Component
@ConditionalOnClass(KafkaBinderConfiguration.class)
@EnableBinding(LogOutput.class)
public class LogToBinding {

    public LogOutput getLogOutput() {
        return logOutput;
    }

    private final LogOutput logOutput;

    @Autowired
    public LogToBinding(LogOutput logOutput) {
        this.logOutput = logOutput;
    }

}
