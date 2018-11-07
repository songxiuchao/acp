package pers.acp.test.application.conf;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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

    private final HibernateProperties hibernateProperties;

    @Autowired
    public JpaPgConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary() {
        HikariConfig hikariConfig = pgConfig();
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pgDataSource());
        em.setPackagesToScan(hikariConfig.getDataSourceProperties().getProperty("scanpackage").split(","));

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        jpaProperties.getProperties().put("hibernate.dialect", hikariConfig.getDataSourceProperties().getProperty("dialect"));
        em.setJpaPropertyMap(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()));
        em.setPersistenceUnitName("persistenceUnitPg");
        return em;
    }

    @Bean(name = "transactionManagerPg")
    public PlatformTransactionManager transactionManagerPrimary() {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryPrimary().getObject()));
    }

}
