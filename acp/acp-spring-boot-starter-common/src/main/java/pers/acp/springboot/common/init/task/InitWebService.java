package pers.acp.springboot.common.init.task;

import pers.acp.springboot.core.soap.base.IWebService;
import pers.acp.springboot.core.soap.config.WSConfig;
import pers.acp.core.log.LogFactory;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;

public class InitWebService {

    private static final LogFactory log = LogFactory.getInstance(InitWebService.class);// 日志对象

    public static void publishWebService() {
        try {
            WSConfig wsConfig = WSConfig.getInstance();
            if (wsConfig != null) {
                ArrayList<WSConfig.Server> servers = (ArrayList<WSConfig.Server>) wsConfig.getServer();
                if (servers != null) {
                    for (WSConfig.Server server : servers) {
                        String classname = server.getClassName();
                        String href = server.getHref();
                        Class<?> cls = Class.forName(classname);
                        Object instance = cls.newInstance();
                        IWebService ws = (IWebService) instance;
                        String name = ws.getServiceName();
                        href = href + "/" + name;
                        Endpoint.publish(href, instance);
                        log.info("publish webservice [" + name + "] success:[" + classname + "] [" + href + "]");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
