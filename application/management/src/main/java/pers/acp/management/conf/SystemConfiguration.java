package pers.acp.management.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangbin by 2018-1-20 21:13
 * @since JDK1.8
 */
@Configuration
@ConfigurationProperties(prefix = "system")
public class SystemConfiguration {

    public long getUserLoginTimeOut() {
        return userLoginTimeOut;
    }

    public void setUserLoginTimeOut(long userLoginTimeOut) {
        this.userLoginTimeOut = userLoginTimeOut;
    }

    private long userLoginTimeOut;

}
