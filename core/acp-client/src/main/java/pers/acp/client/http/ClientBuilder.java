package pers.acp.client.http;

import pers.acp.client.exceptions.HttpException;

/**
 * @author zhangbin by 13/04/2018 18:09
 * @since JDK 11
 */
public class ClientBuilder {

    private boolean https = false;

    private int maxTotalConn = 1000;

    private int maxPerRoute = 50;

    private int timeOut = 60000;

    public boolean isHttps() {
        return https;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public int getTimeOut() {
        return timeOut;
    }

    void setHttps(boolean https) {
        this.https = https;
    }

    public int getMaxTotalConn() {
        return maxTotalConn;
    }

    void setMaxTotalConn(int maxTotalConn) {
        this.maxTotalConn = maxTotalConn;
    }

    void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * 创建客户端实例
     */
    public AcpClient build() throws HttpException {
        return new AcpClient(false, maxTotalConn, maxPerRoute, timeOut);
    }

}
