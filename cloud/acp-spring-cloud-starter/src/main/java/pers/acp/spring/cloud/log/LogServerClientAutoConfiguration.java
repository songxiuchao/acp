package pers.acp.spring.cloud.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;
import pers.acp.spring.cloud.log.producer.LogOutput;
import pers.acp.spring.cloud.log.producer.LogProducer;
import pers.acp.spring.cloud.conf.LogServerClientConfiguration;
import pers.acp.spring.cloud.conf.LogServerConfiguration;

import javax.annotation.PostConstruct;

/**
 * 日志服务客户端自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Configuration
@ConditionalOnExpression("'${acp.cloud.log-server.client.enabled}'.equals('true')")
@AutoConfigureBefore(BindingServiceConfiguration.class)
@EnableBinding(LogOutput.class)
public class LogServerClientAutoConfiguration {

    private final LogServerConfiguration logServerConfiguration;

    private final LogServerClientConfiguration logServerClientConfiguration;

    private final BindingServiceProperties bindings;

    @Autowired
    public LogServerClientAutoConfiguration(LogServerConfiguration logServerConfiguration, LogServerClientConfiguration logServerClientConfiguration, BindingServiceProperties bindings) {
        this.logServerConfiguration = logServerConfiguration;
        this.logServerClientConfiguration = logServerClientConfiguration;
        this.bindings = bindings;
    }

    /**
     * 初始化日志消息生产者
     */
    @PostConstruct
    public void init() {
        if (logServerClientConfiguration.isEnabled()) {
            BindingProperties outputBinding = this.bindings.getBindings().get(LogConstant.OUTPUT);
            if (outputBinding == null) {
                this.bindings.getBindings().put(LogConstant.OUTPUT, new BindingProperties());
            }
            BindingProperties output = this.bindings.getBindings().get(LogConstant.OUTPUT);
            if (output.getDestination() == null || output.getDestination().equals(LogConstant.OUTPUT)) {
                output.setDestination(logServerConfiguration.getDestination());
            }
            output.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
    }

    @Bean
    public LogProducer logProducer(LogOutput logOutput) {
        return new LogProducer(logOutput);
    }

}
