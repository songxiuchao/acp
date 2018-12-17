package pers.acp.test.application.component;

import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * @author zhangbin by 2018-3-2 0:41
 * @since JDK 11
 */
@Component
public class BeansDefine {

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

}
