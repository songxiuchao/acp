package pers.acp.springcloud.log;

import org.springframework.boot.SpringApplication;
import pers.acp.springcloud.common.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK1.8
 */
@AcpCloudAtomApplication
public class LogServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogServerApplication.class, args);
    }

}
