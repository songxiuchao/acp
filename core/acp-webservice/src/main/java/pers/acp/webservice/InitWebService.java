package pers.acp.webservice;

import pers.acp.core.log.LogFactory;
import pers.acp.webservice.base.IWebService;
import pers.acp.webservice.conf.WSConfig;

import javax.xml.ws.Endpoint;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class InitWebService {

    private static final LogFactory log = LogFactory.getInstance(InitWebService.class);// 日志对象

    public static void publishWebService() {
        log.info("publish webservice begin ...");
        try {
            WSConfig wsConfig = WSConfig.getInstance();
            doPublish(wsConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("publish webservice finished!");
        }
    }

    public static void publishWebService(WSConfig wsConfig) {
        log.info("publish webservice begin ...");
        try {
            doPublish(wsConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("publish webservice finished!");
        }
    }

    private static void doPublish(WSConfig wsConfig) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (wsConfig != null) {
            ArrayList<WSConfig.Server> servers = (ArrayList<WSConfig.Server>) wsConfig.getServer();
            if (servers != null) {
                for (WSConfig.Server server : servers) {
                    String classname = server.getClassName();
                    String href = server.getHref();
                    Class<?> cls = Class.forName(classname);
                    Object instance = cls.getDeclaredConstructor().newInstance();
                    IWebService ws = (IWebService) instance;
                    String name = ws.getServiceName();
                    href = href + "/" + name;
                    Endpoint.publish(href, instance);
                    log.info("publish webservice [" + name + "] success:[" + classname + "] [" + href + "]");
                }
            }
        }
    }

}
