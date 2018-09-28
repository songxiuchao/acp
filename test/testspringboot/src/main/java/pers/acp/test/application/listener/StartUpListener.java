package pers.acp.test.application.listener;

import org.springframework.stereotype.Component;
import pers.acp.springboot.common.interfaces.IListener;
import pers.acp.webservice.InitWebService;

/**
 * @author zhangbin by 28/09/2018 14:41
 * @since JDK1.8
 */
@Component
public class StartUpListener implements IListener {

    @Override
    public void startListener() {
        InitWebService.publishWebService();
    }

    @Override
    public void stopListener() {

    }

}
