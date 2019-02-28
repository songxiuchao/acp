package pers.acp.client.http;

/**
 * http 客户端
 */
public class HttpClientBuilder extends ClientBuilder {

    public HttpClientBuilder() {
        setHttps(false);
    }

    public HttpClientBuilder maxTotalConn(int maxTotalConn) {
        setMaxTotalConn(maxTotalConn);
        return this;
    }

    public HttpClientBuilder maxPerRoute(int maxPerRoute) {
        setMaxPerRoute(maxPerRoute);
        return this;
    }

    public HttpClientBuilder timeOut(int timeOut) {
        setTimeOut(timeOut);
        return this;
    }

}
