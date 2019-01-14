package pers.acp.springboot.core.socket.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class ListenConfig {

    @XStreamAsAttribute
    @XStreamAlias("name")
    private String name;

    @XStreamAsAttribute
    @XStreamAlias("enabled")
    private boolean enabled;

    @XStreamAsAttribute
    @XStreamAlias("keepAlive")
    private boolean keepAlive;

    @XStreamAsAttribute
    @XStreamAlias("idletime")
    private long idletime;

    @XStreamAsAttribute
    @XStreamAlias("port")
    private int port;

    @XStreamAsAttribute
    @XStreamAlias("isHex")
    private boolean isHex;

    @XStreamAsAttribute
    @XStreamAlias("responseBean")
    private String responseBean;

    @XStreamAsAttribute
    @XStreamAlias("responsable")
    private boolean responsable;

    @XStreamAsAttribute
    @XStreamAlias("charset")
    private String charset;

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public long getIdletime() {
        return idletime;
    }

    public int getPort() {
        return port;
    }

    public boolean isHex() {
        return isHex;
    }

    public String getResponseBean() {
        return responseBean;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isResponsable() {
        return responsable;
    }

}
