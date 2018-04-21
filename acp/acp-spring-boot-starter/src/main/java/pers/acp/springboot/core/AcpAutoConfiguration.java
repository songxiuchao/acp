package pers.acp.springboot.core;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pers.acp.springboot.core.conf.ControllerAspectConfiguration;

/**
 * @author zhangbin by 2018-1-15 0:37
 * @since JDK1.8
 */
@Configuration
@ComponentScan("pers.acp.springboot.core")
@ServletComponentScan({"pers.acp.springboot.core"})
public class AcpAutoConfiguration {

    @Bean
    public ControllerAspectConfiguration controllerAspectConfiguration() {
        return new ControllerAspectConfiguration();
    }

}
