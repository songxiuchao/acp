package pers.acp.springcloud.server.helloworld.hystrix;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import pers.acp.core.log.LogFactory;
import pers.acp.springcloud.server.helloworld.feign.HelloServer;

/**
 * @author zhangbin by 2018-3-7 23:47
 * @since JDK1.8
 */
@Component
public class HelloServerHystrix implements FallbackFactory<HelloServer> {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    @Override
    public HelloServer create(Throwable cause) {
        if (!(cause instanceof RuntimeException)) {
            log.error("HelloServer hystrix caused by: " + cause.getMessage(), cause);
        }
        return name -> "Hello service Hystrix[" + name + "]";
    }

}
