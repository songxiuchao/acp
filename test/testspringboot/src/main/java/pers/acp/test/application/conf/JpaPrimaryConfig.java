package pers.acp.test.application.conf;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author zhangbin by 2018-1-15 16:03
 * @since JDK1.8
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"pers.acp.test.application.repo.primary"},
        entityManagerFactoryRef = "entityManagerFactoryPrimary",
        transactionManagerRef = "transactionManagerPrimary")
public class JpaPrimaryConfig {

    private final JpaProperties jpaProperties;

    @Autowired
    public JpaPrimaryConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public HikariConfig primaryConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        HikariConfig hikariConfig = primaryConfig();
        jpaProperties.getProperties().put("hibernate.dialect", hikariConfig.getDataSourceProperties().getProperty("dialect"));
        return builder.dataSource(primaryDataSource())
                .packages(hikariConfig.getDataSourceProperties().getProperty("scanpackage").split(","))
                .persistenceUnit("persistenceUnitPrimary")
                .properties(jpaProperties.getHibernateProperties(new HibernateSettings()))
                .build();
    }

    @Primary
    @Bean(name = "entityManagerPrimary")
    public EntityManager entityManagerPrimary(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactoryPrimary(builder).getObject()).createEntityManager();
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryPrimary(builder).getObject()));
    }

}
