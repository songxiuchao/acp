package pers.acp.client.http;

import pers.acp.client.exceptions.HttpException;

/**
 * @author zhangbin by 13/04/2018 18:09
 * @since JDK 11
 */
public class ClientBuilder {

    private boolean https = false;

    private int maxperRoute = 100;

    private int timeOut = 60000;

    public boolean isHttps() {
        return https;
    }

    public int getMaxperRoute() {
        return maxperRoute;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public void setMaxperRoute(int maxperRoute) {
        this.maxperRoute = maxperRoute;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * 创建客户端实例
     */
    public AcpClient build() throws HttpException {
        return new AcpClient(false, maxperRoute, timeOut);
    }

}
