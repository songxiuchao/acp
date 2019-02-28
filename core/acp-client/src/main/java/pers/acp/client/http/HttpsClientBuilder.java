package pers.acp.client.http;

/**
 * https 客户端
 */
public class HttpsClientBuilder extends ClientBuilder {

    public HttpsClientBuilder() {
        setHttps(true);
    }


    public HttpsClientBuilder maxTotalConn(int maxTotalConn) {
        setMaxTotalConn(maxTotalConn);
        return this;
    }

    public HttpsClientBuilder maxPerRoute(int maxPerRoute) {
        setMaxPerRoute(maxPerRoute);
        return this;
    }

    public HttpsClientBuilder timeOut(int timeOut) {
        setTimeOut(timeOut);
        return this;
    }

}
