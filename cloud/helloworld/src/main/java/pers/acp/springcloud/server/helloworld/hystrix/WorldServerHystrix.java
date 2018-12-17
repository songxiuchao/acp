package pers.acp.springcloud.server.helloworld.hystrix;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import pers.acp.core.log.LogFactory;
import pers.acp.springcloud.server.helloworld.feign.WorldServer;

/**
 * @author zhangbin by 2018-3-7 23:49
 * @since JDK 11
 */
@Component
public class WorldServerHystrix implements FallbackFactory<WorldServer> {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    @Override
    public WorldServer create(Throwable cause) {
        if (!(cause instanceof RuntimeException)) {
            log.error("WorldServerHystrix hystrix caused by: " + cause.getMessage(), cause);
        }
        return name -> "World service Hystrix[" + name + "]";
    }

}
