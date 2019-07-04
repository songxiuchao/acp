package pers.acp.client.http;

import okhttp3.*;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.base.CookieStore;
import pers.acp.client.http.base.HttpInterceptor;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.packet.http.HttpPacket;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * @author zhang by 01/07/2019
 * @since JDK 11
 */
public class AcpClient {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private OkHttpClient.Builder builder;

    private OkHttpClient client = null;

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }

    public AcpClient cookieStore(CookieStore cookieStore) {
        this.builder.cookieJar(cookieStore);
        return this;
    }

    public AcpClient httpInterceptor(HttpInterceptor httpInterceptor) {
        this.builder.addInterceptor(httpInterceptor);
        return this;
    }

    /**
     * http 构造函数
     *
     * @param builder              构造器
     * @param disableSslValidation 无效ssl验证
     */
    AcpClient(OkHttpClient.Builder builder, boolean disableSslValidation, String sslType) throws HttpException {
        this.builder = builder;
        if (disableSslValidation) {
            try {
                X509TrustManager disabledTrustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };
                TrustManager[] trustManagers = new TrustManager[1];
                trustManagers[0] = disabledTrustManager;
                SSLContext sslContext = SSLContext.getInstance(sslType);
                sslContext.init(null, trustManagers, new java.security.SecureRandom());
                SSLSocketFactory disabledSSLSocketFactory = sslContext.getSocketFactory();
                this.builder.sslSocketFactory(disabledSSLSocketFactory, disabledTrustManager);
                this.builder.hostnameVerifier((hostname, session) -> true);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                log.error(e.getMessage(), e);
                throw new HttpException("Error setting SSLSocketFactory in OKHttpClient: " + e.getMessage());
            }
        }
    }

    private void basicAuth(RequestParam requestParam) {
        if (!CommonTools.isNullStr(requestParam.getBasicUsername()) && !CommonTools.isNullStr(requestParam.getBasicPassword())) {
            this.builder.authenticator((route, response) -> {
                String credential = Credentials.basic(requestParam.getBasicUsername(), requestParam.getBasicPassword());
                return response.request().newBuilder().header("Authorization", credential).build();
            });
        }
    }

    /**
     * GET请求
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    public ResponseResult doGet(RequestParam requestParam) throws HttpException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(HttpPacket.buildGetParam(requestParam.getUrl(), requestParam.getParams(), requestParam.getClientCharset()));
        return doRequest(requestParam, requestBuilder);
    }

    /**
     * GET请求
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    public void doGetAsync(RequestParam requestParam, HttpCallBack httpCallBack) throws HttpException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(HttpPacket.buildGetParam(requestParam.getUrl(), requestParam.getParams(), requestParam.getClientCharset()));
        doRequestAsync(requestParam, requestBuilder, httpCallBack);
    }

    /**
     * POST请求
     * 键值对
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    public ResponseResult doPost(RequestParam requestParam) throws HttpException {
        FormBody.Builder body = new FormBody.Builder();
        requestParam.getParams().forEach(body::add);
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body.build());
        return doRequest(requestParam, requestBuilder);
    }

    /**
     * POST请求
     * 键值对
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    public void doPostAsync(RequestParam requestParam, HttpCallBack httpCallBack) throws HttpException {
        FormBody.Builder body = new FormBody.Builder();
        requestParam.getParams().forEach(body::add);
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body.build());
        doRequestAsync(requestParam, requestBuilder, httpCallBack);
    }

    /**
     * POST请求发送XML
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    public ResponseResult doPostXml(RequestParam requestParam) throws HttpException {
        RequestBody body = RequestBody.create(MediaType.parse("application/xml;charset=" + requestParam.getClientCharset()),
                requestParam.getBodyString());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        return doRequest(requestParam, requestBuilder);
    }

    /**
     * POST请求发送XML
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    public void doPostXmlAsync(RequestParam requestParam, HttpCallBack httpCallBack) throws HttpException {
        RequestBody body = RequestBody.create(MediaType.parse("application/xml;charset=" + requestParam.getClientCharset()),
                requestParam.getBodyString());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        doRequestAsync(requestParam, requestBuilder, httpCallBack);
    }

    /**
     * POST请求发送JSON字符串
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    public ResponseResult doPostJson(RequestParam requestParam) throws HttpException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=" + requestParam.getClientCharset()),
                requestParam.getBodyString());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        return doRequest(requestParam, requestBuilder);
    }

    /**
     * POST请求发送JSON字符串
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    public void doPostJsonAsync(RequestParam requestParam, HttpCallBack httpCallBack) throws HttpException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=" + requestParam.getClientCharset()),
                requestParam.getBodyString());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        doRequestAsync(requestParam, requestBuilder, httpCallBack);
    }

    /**
     * POST请求发送SOAP报文数据
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    public ResponseResult doPostSoap(RequestParam requestParam) throws HttpException {
        RequestBody body = RequestBody.create(MediaType.parse("application/soap+xml;charset=" + requestParam.getClientCharset()),
                requestParam.getBodyString());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        return doRequest(requestParam, requestBuilder);
    }

    /**
     * POST请求发送SOAP报文数据
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    public void doPostSoapAsync(RequestParam requestParam, HttpCallBack httpCallBack) throws HttpException {
        RequestBody body = RequestBody.create(MediaType.parse("application/soap+xml;charset=" + requestParam.getClientCharset()),
                requestParam.getBodyString());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        doRequestAsync(requestParam, requestBuilder, httpCallBack);
    }

    /**
     * POST请求发送字节数组数据
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    public ResponseResult doPostBytes(RequestParam requestParam) throws HttpException {
        if (requestParam.getBodyBytes() == null) {
            throw new HttpException("the bodyBytes is null");
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"),
                requestParam.getBodyBytes());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        return doRequest(requestParam, requestBuilder);
    }

    /**
     * POST请求发送字节数组数据
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    public void doPostBytesAsync(RequestParam requestParam, HttpCallBack httpCallBack) throws HttpException {
        if (requestParam.getBodyBytes() == null) {
            throw new HttpException("the bodyBytes is null");
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"),
                requestParam.getBodyBytes());
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl())
                .post(body);
        doRequestAsync(requestParam, requestBuilder, httpCallBack);
    }

    /**
     * 自定义请求
     *
     * @param method       请求类型
     * @param requestParam 请求参数（body不能为空）
     * @return 响应信息
     */
    public ResponseResult doRequest(String method, RequestParam requestParam) throws HttpException {
        if (requestParam.getBody() == null) {
            throw new HttpException("the body is null");
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl()).method(method, requestParam.getBody());
        return doRequest(requestParam, requestBuilder);
    }

    /**
     * 自定义请求
     *
     * @param method       请求类型
     * @param requestParam 请求参数（body不能为空）
     * @param httpCallBack 回调对象
     */
    public void doRequestAsync(String method, RequestParam requestParam, HttpCallBack httpCallBack) throws HttpException {
        if (requestParam.getBody() == null) {
            throw new HttpException("the body is null");
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestParam.getUrl()).method(method.toUpperCase(), requestParam.getBody());
        doRequestAsync(requestParam, requestBuilder, httpCallBack);
    }

    /**
     * 执行同步请求
     *
     * @param requestParam   请求参数
     * @param requestBuilder 请求对象构造器
     * @return 响应
     */
    private ResponseResult doRequest(RequestParam requestParam, Request.Builder requestBuilder) throws HttpException {
        try {
            if (client == null) {
                basicAuth(requestParam);
                client = builder.build();
            }
            requestParam.getRequestHeaders().forEach(requestBuilder::addHeader);
            Response response = client.newCall(requestBuilder.build()).execute();
            return parseResponseResult(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new HttpException(e.getMessage());
        }
    }

    /**
     * 执行异步请求
     *
     * @param requestParam   请求参数
     * @param requestBuilder 请求对象构造器
     * @param httpCallBack   回调对象
     */
    private void doRequestAsync(RequestParam requestParam, Request.Builder requestBuilder, HttpCallBack httpCallBack) throws HttpException {
        try {
            if (client == null) {
                basicAuth(requestParam);
                client = builder.build();
            }
            requestParam.getRequestHeaders().forEach(requestBuilder::addHeader);
            client.newCall(requestBuilder.build()).enqueue(httpCallBack);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new HttpException(e.getMessage());
        }
    }

    /**
     * 处理响应
     *
     * @param response 响应对象
     * @return 转换后的响应对象
     * @throws IOException 异常
     */
    static ResponseResult parseResponseResult(Response response) throws IOException {
        ResponseResult responseResult = ResponseResultBuilder.build()
                .response(response)
                .headers(response.headers())
                .status(response.code());
        if (response.body() != null) {
            responseResult.body(response.body().string());
        }
        return responseResult;
    }

}
