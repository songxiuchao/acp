package pers.acp.test.application.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pers.acp.test.application.TestSpringBootTestApplication;

/**
 * @author zhangbin by 28/04/2018 15:49
 * @since JDK1.8
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestSpringBootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseTest {

    @Autowired
    TestRestTemplate testRestTemplate;

}
