package pers.acp.client.http;

/**
 * https 客户端
 */
public class HttpsClientBuilder extends ClientBuilder {

    public HttpsClientBuilder() {
        setHttps(true);
    }

    public HttpsClientBuilder maxperRoute(int maxperRoute) {
        setMaxperRoute(maxperRoute);
        return this;
    }

    public HttpsClientBuilder timeOut(int timeOut) {
        setTimeOut(timeOut);
        return this;
    }

}
