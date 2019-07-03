package pers.acp.client.http;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import pers.acp.client.exceptions.HttpException;

import java.util.concurrent.TimeUnit;

/**
 * http 客户端
 */
public class HttpClientBuilder {

    /**
     * 是否验证无效的SSL
     */
    private boolean disableSslValidation = false;

    /**
     * 最大链接数
     */
    private int maxTotalConn = 1000;

    /**
     * 连接超时时间，单位毫秒
     */
    private int timeOut = 60000;

    /**
     * 链接空闲时间，单位毫秒
     */
    private long timeToLive = 900000;

    /**
     * 时间单位
     */
    private TimeUnit timeToLiveTimeUnit = TimeUnit.MILLISECONDS;

    /**
     * 跟踪重定向
     */
    private boolean followRedirects = true;

    /**
     * SSL链接类型
     */
    private String sslProtocolVersion = "SSL";

    /**
     * 请求失败时是否重试
     */
    private boolean retryOnConnectionFailure = false;

    public HttpClientBuilder maxTotalConn(int maxTotalConn) {
        this.maxTotalConn = maxTotalConn;
        return this;
    }

    public HttpClientBuilder timeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public HttpClientBuilder timeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    public HttpClientBuilder timeToLiveTimeUnit(TimeUnit timeToLiveTimeUnit) {
        this.timeToLiveTimeUnit = timeToLiveTimeUnit;
        return this;
    }

    public HttpClientBuilder disableSslValidation(boolean disableSslValidation) {
        this.disableSslValidation = disableSslValidation;
        return this;
    }

    public HttpClientBuilder followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    public HttpClientBuilder sslProtocolVersion(String sslType) {
        this.sslProtocolVersion = sslType;
        return this;
    }

    public HttpClientBuilder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
        return this;
    }

    /**
     * 创建客户端实例
     */
    public AcpClient build() throws HttpException {
        return new AcpClient(new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .followRedirects(followRedirects)
                .followSslRedirects(followRedirects)
                .connectionPool(new ConnectionPool(maxTotalConn, timeToLive, timeToLiveTimeUnit))
                .retryOnConnectionFailure(retryOnConnectionFailure)
                .cookieJar(new DefaultCookieJar()), disableSslValidation, sslProtocolVersion);
    }

}
