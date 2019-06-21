package pers.acp.spring.boot.ws.conf;

import pers.acp.webservice.conf.WSServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public class WebServiceConfiguration {

    public List<WSServer> getServer() {
        return server;
    }

    public void setServer(List<WSServer> server) {
        this.server = server;
    }

    private List<WSServer> server = new ArrayList<>();

}
