package pers.acp.spring.cloud.annotation;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

import java.lang.annotation.*;

/**
 * Cloud 原子服务注解
 *
 * @author zhangbin by 12/04/2018 12:47
 * @since JDK 11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AcpCloudOauthServerApplication
@EnableOAuth2Sso
public @interface AcpCloudAtomApplication {
}
