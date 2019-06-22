package pers.acp.spring.cloud.annotation;

import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.*;

/**
 * @author zhang by 14/01/2019 16:13
 * @since JDK 11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringCloudApplication
@EnableFeignClients
@EnableHystrix
@EnableSwagger2
@EnableGlobalMethodSecurity(prePostEnabled = true)
public @interface AcpCloudOauthServerApplication {
}
