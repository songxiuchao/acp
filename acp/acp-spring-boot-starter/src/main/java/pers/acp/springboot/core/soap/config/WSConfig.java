package pers.acp.springboot.core.soap.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.config.base.BaseXML;
import pers.acp.core.log.LogFactory;

import java.util.List;

@XStreamAlias("webservice-config")
public class WSConfig extends BaseXML {

    private static final LogFactory log = LogFactory.getInstance(WSConfig.class);

    public static WSConfig getInstance() {
        try {
            return (WSConfig) Load(WSConfig.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @XStreamImplicit(itemFieldName = "server")
    private List<Server> server;

    public List<Server> getServer() {
        return server;
    }

    public class Server {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String className;

        @XStreamAsAttribute
        @XStreamAlias("href")
        private String href;

        public String getClassName() {
            return className;
        }

        public String getHref() {
            return href;
        }
    }

}
