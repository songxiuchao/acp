package pers.acp.webservice.conf;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.base.BaseXml;
import pers.acp.core.log.LogFactory;

import java.util.List;

@XStreamAlias("webservice-config")
public class WSConfig extends BaseXml {

    private static final LogFactory log = LogFactory.getInstance(WSConfig.class);

    public static WSConfig getInstance() {
        try {
            return (WSConfig) Load(WSConfig.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public List<WSServer> getServer() {
        return server;
    }

    public void setServer(List<WSServer> server) {
        this.server = server;
    }

    @XStreamImplicit(itemFieldName = "server")
    private List<WSServer> server;

}
