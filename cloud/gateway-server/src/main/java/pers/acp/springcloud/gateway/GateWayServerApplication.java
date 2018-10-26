package pers.acp.springcloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author zhangbin by 2018-3-10 20:45
 * @since JDK1.8
 */
@SpringCloudApplication
public class GateWayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateWayServerApplication.class, args);
    }

}
