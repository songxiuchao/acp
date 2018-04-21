package pers.acp.springcloud.common.conf;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangbin by 12/04/2018 10:13
 * @since JDK1.8
 */
@Configuration
public class FeignOauthRequestInterceptorConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            if (sra != null) {
                HttpServletRequest request = sra.getRequest();
                template.header("Authorization", request.getHeader("Authorization"));
            }
        };
    }

}
