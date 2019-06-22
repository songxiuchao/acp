package pers.acp.spring.boot.ws;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pers.acp.spring.boot.ws.conf.WebServiceConfiguration;
import pers.acp.webservice.InitWebService;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.spring.boot.ws")
public class AcpWebServiceAutoConfiguration {

    @Bean
    @ConditionalOnClass(InitWebService.class)
    @ConfigurationProperties(prefix = "acp.ws-server")
    public WebServiceConfiguration acpWebServiceConfiguration() {
        return new WebServiceConfiguration();
    }

}
