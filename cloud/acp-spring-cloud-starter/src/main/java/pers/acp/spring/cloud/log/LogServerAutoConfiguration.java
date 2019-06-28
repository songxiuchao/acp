package pers.acp.spring.cloud.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;
import pers.acp.spring.cloud.log.consumer.DefaultLogProcess;
import pers.acp.spring.cloud.log.consumer.LogConsumer;
import pers.acp.spring.cloud.log.consumer.LogInput;
import pers.acp.spring.cloud.log.consumer.LogProcess;
import pers.acp.spring.cloud.conf.LogServerConfiguration;

import javax.annotation.PostConstruct;

/**
 * 日志服务自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Configuration
@ConditionalOnExpression("'${acp.cloud.log-server.enabled}'.equals('true')")
@AutoConfigureBefore(BindingServiceConfiguration.class)
@EnableBinding(LogInput.class)
public class LogServerAutoConfiguration {

    private final LogServerConfiguration logServerConfiguration;

    private final BindingServiceProperties bindings;

    @Autowired
    public LogServerAutoConfiguration(LogServerConfiguration logServerConfiguration, BindingServiceProperties bindings) {
        this.logServerConfiguration = logServerConfiguration;
        this.bindings = bindings;
    }

    /**
     * 初始化日志消息消费者
     */
    @PostConstruct
    public void init() {
        if (logServerConfiguration.isEnabled()) {
            BindingProperties inputBinding = this.bindings.getBindings().get(LogConstant.INPUT);
            if (inputBinding == null) {
                this.bindings.getBindings().put(LogConstant.INPUT, new BindingProperties());
            }
            BindingProperties input = this.bindings.getBindings().get(LogConstant.INPUT);
            if (input.getDestination() == null || input.getDestination().equals(LogConstant.INPUT)) {
                input.setDestination(logServerConfiguration.getDestination());
            }
            input.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            input.setGroup(logServerConfiguration.getGroupId());
        }
    }

    @Bean
    public LogConsumer logConsumer(ObjectMapper objectMapper, LogProcess logProcess) {
        return new LogConsumer(objectMapper, logProcess);
    }

    @Bean
    @ConditionalOnMissingBean(LogProcess.class)
    public LogProcess logProcess() {
        return new DefaultLogProcess();
    }

}
