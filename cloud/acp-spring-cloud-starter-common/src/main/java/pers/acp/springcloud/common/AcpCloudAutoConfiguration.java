package pers.acp.springcloud.common;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.springcloud.common")
@ServletComponentScan({"pers.acp.springcloud.common"})
@AutoConfigureBefore(BindingServiceConfiguration.class)
public class AcpCloudAutoConfiguration {
}
