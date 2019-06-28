package pers.acp.spring.boot.conf;

/**
 * Socket监听配置
 */
public class SocketListenerConfiguration {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
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

    public String getMessageDecoder() {
        return messageDecoder;
    }

    public void setMessageDecoder(String messageDecoder) {
        this.messageDecoder = messageDecoder;
    }

    public String getHandleBean() {
        return handleBean;
    }

    public void setHandleBean(String handleBean) {
        this.handleBean = handleBean;
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

    /**
     * 名称
     */
    private String name;

    /**
     * 线程数
     */
    private int threadNumber = 0;

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 是否长连接
     */
    private boolean keepAlive = false;

    /**
     * 空闲等待时间（单位毫秒）
     */
    private long idletime = 10000;

    /**
     * 监听端口
     */
    private int port;

    /**
     * 是否16进制报文
     */
    private boolean hex = false;

    /**
     * 消息解码器
     */
    private String messageDecoder;

    /**
     * 消息处理实例（类名）
     */
    private String handleBean;

    /**
     * 是否需要响应
     */
    private boolean responsable = true;

    /**
     * 字符编码
     */
    private String charset;

}
