package pers.acp.test.application.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.acp.springboot.core.datasource.AcpDataSource;

import javax.persistence.EntityManager;
import java.util.Objects;

/**
 * @author zhangbin by 2018-1-15 15:29
 * @since JDK1.8
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"pers.acp.test.application.repo.pg"},
        entityManagerFactoryRef = "entityManagerFactoryPg",
        transactionManagerRef = "transactionManagerPg")
public class JpaPgConfig {

    private final JpaProperties jpaProperties;

    @Autowired
    public JpaPgConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.pg")
    public AcpDataSource pgDataSource() {
        return new AcpDataSource();
    }

    @Bean(name = "entityManagerFactoryPg")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        AcpDataSource pgDS = pgDataSource();
        jpaProperties.getProperties().put("hibernate.dialect", pgDS.getDialect());
        return builder.dataSource(pgDS)
                .packages(pgDS.getScanpackage().split(","))
                .persistenceUnit("persistenceUnitPg")
                .properties(jpaProperties.getHibernateProperties(new HibernateSettings()))
                .build();
    }

    @Bean(name = "entityManagerPg")
    public EntityManager entityManagerPg(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactoryPrimary(builder).getObject()).createEntityManager();
    }

    @Bean(name = "transactionManagerPg")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryPrimary(builder).getObject()));
    }

}
