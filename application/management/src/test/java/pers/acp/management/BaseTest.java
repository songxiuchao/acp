package pers.acp.management;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * @author zhangbin by 2018-2-22 16:59
 * @since JDK1.8
 */
//@SpringBootTest(classes = MainApplication.class)
//@WebAppConfiguration
public class BaseTest {

    TestRestTemplate restTemplate = new TestRestTemplate();

}
