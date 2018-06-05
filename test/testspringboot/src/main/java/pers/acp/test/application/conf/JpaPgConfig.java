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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
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
    public DataSource pgDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.pg")
    public HikariConfig pgConfig() {
        return new HikariConfig();
    }

    @Bean(name = "entityManagerFactoryPg")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        HikariConfig hikariConfig = pgConfig();
        jpaProperties.getProperties().put("hibernate.dialect", hikariConfig.getDataSourceProperties().getProperty("dialect"));
        return builder.dataSource(pgDataSource())
                .packages(hikariConfig.getDataSourceProperties().getProperty("scanpackage").split(","))
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
