package pers.acp.springboot.core.socket.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.config.base.BaseXML;
import pers.acp.core.log.LogFactory;

import java.util.List;

@XStreamAlias("udp-config")
public class UdpConfig extends BaseXML {

    private static final LogFactory log = LogFactory.getInstance(UdpConfig.class);

    public static UdpConfig getInstance() {
        try {
            return (UdpConfig) Load(UdpConfig.class);
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
