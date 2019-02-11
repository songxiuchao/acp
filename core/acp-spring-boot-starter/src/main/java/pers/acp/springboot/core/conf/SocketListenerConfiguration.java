package pers.acp.springboot.core.conf;

public class SocketListenerConfiguration {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isHex() {
        return hex;
    }

    public void setHex(boolean hex) {
        this.hex = hex;
    }

    public long getIdletime() {
        return idletime;
    }

    public void setIdletime(long idletime) {
        this.idletime = idletime;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getResponseBean() {
        return responseBean;
    }

    public void setResponseBean(String responseBean) {
        this.responseBean = responseBean;
    }

    public boolean isResponsable() {
        return responsable;
    }

    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    private String name;

    private boolean enabled = false;

    private boolean keepAlive = false;

    private long idletime = 10000;

    private int port;

    private boolean hex = false;

    private String responseBean;

    private boolean responsable = true;

    private String charset;

}
