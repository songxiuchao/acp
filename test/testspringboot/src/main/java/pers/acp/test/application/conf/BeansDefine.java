package pers.acp.test.application.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;

/**
 * @author zhangbin by 2018-3-2 0:41
 * @since JDK 11
 */
@Configuration
public class BeansDefine {

    @Bean
    @ConditionalOnMissingBean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

}
