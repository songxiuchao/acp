package pers.acp.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pers.acp.management.init.InitData;
import pers.acp.springboot.core.tools.SpringBeanFactory;

/**
 * Created by zhangbin on 2017/6/8.
 * spring boot 服务入口
 */
@SpringBootApplication
public class ManageTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageTestApplication.class, args);

//        InitData initData = SpringBeanFactory.getBean(InitData.class);
//        if (initData != null) {
//            initData.doInit();
//        }
    }

}
