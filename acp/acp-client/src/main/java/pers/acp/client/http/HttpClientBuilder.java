package pers.acp.client.http;

/**
 * http 客户端
 */
public class HttpClientBuilder extends ClientBuilder {

    public HttpClientBuilder() {
        setHttps(false);
    }

    public HttpClientBuilder maxperRoute(int maxperRoute) {
        setMaxperRoute(maxperRoute);
        return this;
    }

    public HttpClientBuilder timeOut(int timeOut) {
        setTimeOut(timeOut);
        return this;
    }

}
