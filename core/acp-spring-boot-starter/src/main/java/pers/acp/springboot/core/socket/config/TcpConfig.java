package pers.acp.springboot.core.socket.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.config.base.BaseXML;
import pers.acp.core.log.LogFactory;

import java.util.List;

@XStreamAlias("tcp-config")
public class TcpConfig extends BaseXML {

    private static final LogFactory log = LogFactory.getInstance(TcpConfig.class);

    public static TcpConfig getInstance() {
        try {
            return (TcpConfig) Load(TcpConfig.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @XStreamImplicit(itemFieldName = "listen")
    private List<ListenConfig> listen;

    public List<ListenConfig> getListen() {
        return listen;
    }

}
