package pers.acp.spring.boot.ws.init;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import pers.acp.spring.boot.base.BaseInitialization;
import pers.acp.spring.boot.ws.conf.WebServiceConfiguration;
import pers.acp.webservice.InitWebService;
import pers.acp.webservice.conf.WSConfig;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Component
@ConditionalOnClass(InitWebService.class)
public class WebServiceInitialization extends BaseInitialization {

    private final WebServiceConfiguration webServiceConfiguration;

    public WebServiceInitialization(WebServiceConfiguration webServiceConfiguration) {
        this.webServiceConfiguration = webServiceConfiguration;
    }

    @Override
    public String getName() {
        return "webservice setup server";
    }

    @Override
    public void start() {
        WSConfig wsConfig = new WSConfig();
        wsConfig.setServer(webServiceConfiguration.getServer());
        InitWebService.publishWebService(wsConfig);
    }

    @Override
    public void stop() {

    }

    @Override
    public int getOrder() {
        return 1;
    }

}
