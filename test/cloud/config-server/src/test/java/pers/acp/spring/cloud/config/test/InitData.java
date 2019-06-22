package pers.acp.spring.cloud.config.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.spring.cloud.config.ConfigServerApplication;
import pers.acp.spring.cloud.config.entity.Properties;
import pers.acp.spring.cloud.config.repo.PropertiesRepository;

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConfigServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InitData {

    @Autowired
    private PropertiesRepository propertiesRepository;

    @Test
    @Transactional
    @Rollback(false)
    void doInitData() {
        Properties properties = new Properties();
        properties.setConfigApplication("atomic-helloworld");
        properties.setConfigProfile("prod");
        properties.setConfigLabel("master");
        properties.setConfigKey("acp.test.properties");
        properties.setConfigValue("啊啊得到答复12312313");
        properties.setEnabled(true);
        propertiesRepository.save(properties);
    }

}
