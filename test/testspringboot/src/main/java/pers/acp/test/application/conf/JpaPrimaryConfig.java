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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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

    private final HibernateProperties hibernateProperties;

    @Autowired
    public JpaPrimaryConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary() {
        HikariConfig hikariConfig = primaryConfig();
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(primaryDataSource());
        em.setPackagesToScan(hikariConfig.getDataSourceProperties().getProperty("scanpackage").split(","));

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        jpaProperties.getProperties().put("hibernate.dialect", hikariConfig.getDataSourceProperties().getProperty("dialect"));
        em.setJpaPropertyMap(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()));
        em.setPersistenceUnitName("persistenceUnitPrimary");
        return em;
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary() {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryPrimary().getObject()));
    }

}
