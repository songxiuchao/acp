package pers.acp.spring.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pers.acp.spring.boot.tools.PackageTools;

/**
 * @author zhangbin by 2018-1-15 0:37
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.springboot")
public class AcpAutoConfiguration {

    @Primary
    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    @ConditionalOnBean({JacksonProperties.class})
    public ObjectMapper jacksonObjectMapper(JacksonProperties jacksonProperties) {
        return PackageTools.buildJacksonObjectMapper(jacksonProperties);
    }

}
