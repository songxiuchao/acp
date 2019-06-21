package pers.acp.spring.cloud.config.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pers.acp.spring.cloud.config.entity.Properties;

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
public interface PropertiesRepository extends JpaSpecificationExecutor<Properties>, JpaRepository<Properties, String> {
}
