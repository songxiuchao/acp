package pers.acp.client.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import pers.acp.client.exceptions.HttpException;
import pers.acp.core.CommonTools;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import pers.acp.core.log.LogFactory;
import pers.acp.packet.http.HttpPacket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ClientSender {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private static TrustManager manager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    };

    private CloseableHttpClient client = null;

    /**
     * 构造函数
     *
     * @param isHttps     是否https请求，默认false
     * @param maxperRoute 连接池最大连接数，默认100
     * @param timeOut     超时时间
     */
    ClientSender(boolean isHttps, int maxperRoute, int timeOut) throws HttpException {
        this.https = isHttps;
        this.maxperRoute = maxperRoute;
        this.timeOut = timeOut;
        createHttpClient();
    }

    HttpClient getHttpClient() {
        return client;
    }

    private boolean https;

    private int maxperRoute;

    private int timeOut;

    private boolean post;

    private Map<String, String> params;

    private Map<String, String> headers;

    private String clientCharset = CommonTools.getDefaultCharset();

    private boolean sendXML;

    private String rootName;

    private boolean sendJSONStr;

    private String jsonString;

    private boolean sendBytes;

    private boolean sendSOAP;

    private Map<String, String> contextParams;

    private byte[] bytes;

    boolean isHttps() {
        return https;
    }

    int getTimeOut() {
        return timeOut;
    }

    int getMaxperRoute() {
        return maxperRoute;
    }

    /**
     * 是否post请求
     *
     * @param post 是否post请求
     */
    ClientSender post(boolean post) {
        this.post = post;
        return this;
    }

    /**
     * 参数
     *
     * @param params 参数
     */
    ClientSender params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    /**
     * 字符集
     *
     * @param clientCharset 字符集
     */
    ClientSender clientCharset(String clientCharset) {
        if (!CommonTools.isNullStr(clientCharset))
            this.clientCharset = clientCharset;
        return this;
    }

    /**
     * 请求头信息
     *
     * @param headers 请求头信息
     */
    ClientSender headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 是否发送xml报文
     *
     * @param sendXML 是否发送xml报文
     */
    ClientSender sendXML(boolean sendXML) {
        this.sendXML = sendXML;
        return this;
    }

    /**
     * xml报文根节点名称
     *
     * @param rootName xml报文根节点名称
     */
    ClientSender rootName(String rootName) {
        this.rootName = rootName;
        return this;
    }

    /**
     * 是否发送json报文
     *
     * @param sendJSONStr 是否发送json报文
     */
    ClientSender sendJSONStr(boolean sendJSONStr) {
        this.sendJSONStr = sendJSONStr;
        return this;
    }

    /**
     * json字符串
     *
     * @param jsonString json字符串
     */
    ClientSender jsonString(String jsonString) {
        this.jsonString = jsonString;
        return this;
    }

    /**
     * 是否发送字节数组
     *
     * @param sendBytes 是否发送字节数组
     */
    ClientSender sendBytes(boolean sendBytes) {
        this.sendBytes = sendBytes;
        return this;
    }

    /**
     * 是否发送SOAP报文
     *
     * @param sendSOAP 是否发送SOAP报文
     */
    ClientSender sendSOAP(boolean sendSOAP) {
        this.sendSOAP = sendSOAP;
        return this;
    }

    /**
     * context参数
     *
     * @param contextParams context参数
     */
    ClientSender contextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
        return this;
    }

    /**
     * 字节数据
     *
     * @param bytes 字节数据
     */
    ClientSender bytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    /**
     * 创建 httpClient
     */
    private void createHttpClient() throws HttpException {
        try {
            if (client == null) {
                RequestConfig requestConfig;
                Registry<ConnectionSocketFactory> socketFactoryRegistry;
                RequestConfig.Builder configBuilder = RequestConfig.custom();
                int MAX_TIMEOUT = 3600000;
                if (timeOut < MAX_TIMEOUT) {
                    configBuilder.setConnectTimeout(timeOut);
                    configBuilder.setSocketTimeout(timeOut);
                    configBuilder.setConnectionRequestTimeout(timeOut);
                } else {
                    configBuilder.setConnectTimeout(MAX_TIMEOUT);
                    configBuilder.setSocketTimeout(MAX_TIMEOUT);
                    configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
                }
                configBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT);
                configBuilder.setContentCompressionEnabled(true);
                BasicCookieStore cookieStore = new BasicCookieStore();
                if (https) {
                    configBuilder.setExpectContinueEnabled(true);
                    configBuilder.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST));
                    configBuilder.setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC));
                    requestConfig = configBuilder.build();
                    SSLContext context = SSLContext.getInstance("TLSv1");
                    context.init(null, new TrustManager[]{manager}, new SecureRandom());
                    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
                    socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
                } else {
                    requestConfig = configBuilder.build();
                    socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).build();
                }
                PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                connectionManager.setMaxTotal(maxperRoute);
                connectionManager.setDefaultMaxPerRoute(connectionManager.getMaxTotal());
                client = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new HttpException("Create Http Client Exception:" + e.getMessage());
        }
    }

    /**
     * 执行请求
     *
     * @param url 请求url
     * @return 响应信息
     */
    ResponseResult doRequest(String url) throws HttpException {
        if (!CommonTools.isNullStr(url)) {
            HttpRequestBase request = null;
            CloseableHttpResponse response = null;
            try {
                createHttpClient();
                HttpClientContext context = generateContext(url);
                if (post) {
                    HttpRequestBase origRequest = new HttpPost(url);
                    request = initHeader(origRequest, headers);
                    if (sendXML) {
                        if ((params == null || params.isEmpty()) && (bytes == null || bytes.length == 0)) {
                            log.error("model is sendXML,but content is null!");
                            throw new HttpException("model is sendXML,but content is null!");
                        }
                        request.addHeader("content-Type", "application/xml;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        if (params != null && !params.isEmpty()) {
                            String xmlData = HttpPacket.buildPostXMLParam(params, rootName, clientCharset);
                            ((HttpPost) request).setEntity(new StringEntity(xmlData, clientCharset));
                        } else {
                            ((HttpPost) request).setEntity(new ByteArrayEntity(bytes));
                        }
                    } else if (sendJSONStr) {
                        if (CommonTools.isNullStr(jsonString)) {
                            log.error("model is sendJSONStr,but JSONString is null!");
                            throw new HttpException("model is sendJSONStr,but JSONString is null!");
                        }
                        request.addHeader("content-Type", "application/json;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new StringEntity(jsonString, clientCharset));
                    } else if (sendBytes) {
                        if (bytes == null || bytes.length == 0) {
                            log.error("model is sendBytes,but bytes is null!");
                            throw new HttpException("model is sendBytes,but bytes is null!");
                        }
                        request.addHeader("content-Type", "application/octet-stream;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new ByteArrayEntity(bytes));
                    } else if (sendSOAP) {
                        if (bytes == null || bytes.length == 0) {
                            log.error("model is sendSOAP,but bytes is null!");
                            throw new HttpException("model is sendSOAP,but bytes is null!");
                        }
                        request.addHeader("content-Type", "application/soap+xml;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new ByteArrayEntity(bytes));
                    } else {
                        List<NameValuePair> pairList = buildPostParam(params);
                        request.addHeader("content-Type", "application/x-www-form-urlencoded;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new UrlEncodedFormEntity(pairList, clientCharset));
                    }
                    response = client.execute(request, context);
                } else {
                    url = HttpPacket.buildGetParam(url, params, clientCharset);
                    HttpRequestBase origRequest = new HttpGet(url);
                    request = initHeader(origRequest, headers);
                    request.addHeader("content-Type", "text/html; charset=" + clientCharset);
                    response = client.execute(request, context);
                }
                return convertResponse(response);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new HttpException("Http Client doRequest Exception:" + e.getMessage());
            } finally {
                if (request != null) {
                    request.abort();
                }
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } else {
            throw new HttpException("Http Client doRequest Exception: The param url is null or empty!");
        }
    }

    /**
     * 转换请求结果
     *
     * @param response 相应对象
     * @return 请求结果
     */
    private ResponseResult convertResponse(HttpResponse response) throws HttpException, IOException {
        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            if (code == 301 || code == 302) {
                Header[] headers = response.getHeaders("Location");
                if (headers.length > 0) {
                    return doRequest(headers[0].getValue());
                }
            } else {
                String charset = getContentCharset(response);
                HttpEntity ety = response.getEntity();
                String recvStr = EntityUtils.toString(ety, charset);
                log.error("Http Client doRequest Exception , status code : " + code + " , message : " + recvStr);
            }
        }
        String charset = getContentCharset(response);
        HttpEntity ety = response.getEntity();
        String recvStr = EntityUtils.toString(ety, charset);
        EntityUtils.consume(ety);
        return ResponseResultBuilder.build().status(code).headers(response.getAllHeaders()).body(recvStr).response(response);
    }

    /**
     * 生成请求上下文
     *
     * @param url 请求地址
     * @return 上下文对象
     */
    private HttpClientContext generateContext(String url) throws URISyntaxException {
        HttpClientContext context = null;
        if (contextParams != null) {
            context = HttpClientContext.create();
            URI uri = new URI(url);
            int port = uri.getPort();
            if (port == -1) {
                if (StringUtils.startsWithIgnoreCase(url, "https://")) {
                    port = 443;
                } else if (StringUtils.startsWithIgnoreCase(url, "http://")) {
                    port = 80;
                }
            }
            if (contextParams.containsKey("username")) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new AuthScope(uri.getHost(), port), new UsernamePasswordCredentials(contextParams.get("username"), contextParams.get("password")));
                AuthCache authCache = new BasicAuthCache();
                context.setCredentialsProvider(credsProvider);
                context.setAuthCache(authCache);
            }
        }
        return context;
    }

    /**
     * 设置请求报文头信息
     *
     * @param requestBase 请求对象
     * @param header      头信息
     * @return 请求对象
     */
    private HttpRequestBase initHeader(HttpRequestBase requestBase, Map<String, String> header) {
        header.entrySet().stream().filter(entry -> !CommonTools.isNullStr(entry.getValue())).forEach(entry -> requestBase.addHeader(entry.getKey(), entry.getValue()));
        return requestBase;
    }

    /**
     * 获取服务器响应字符编码
     *
     * @param response 响应对象
     * @return 响应字符编码
     */
    private String getContentCharset(HttpResponse response) {
        String charset = CommonTools.getDefaultCharset();
        Header header = response.getEntity().getContentType();
        if (header != null) {
            String s = header.getValue();
            if (matcher(s, "(charset)\\s?=\\s?(utf-?8)")) {
                charset = "utf-8";
            } else if (matcher(s, "(charset)\\s?=\\s?(gbk)")) {
                charset = "gbk";
            } else if (matcher(s, "(charset)\\s?=\\s?(gb2312)")) {
                charset = "gb2312";
            }
        }
        if (CommonTools.isNullStr(charset)) {
            charset = "ISO-8859-1";
        }
        return charset;
    }

    private boolean matcher(String s, String pattern) {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
        Matcher matcher = p.matcher(s);
        return matcher.find();
    }

    /**
     * 构建POST请求参数
     *
     * @param params 参数
     * @return 参数List
     */
    private List<NameValuePair> buildPostParam(Map<String, String> params) {
        try {
            if (params != null) {
                List<NameValuePair> pairList = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    pairList.add(pair);
                }
                return pairList;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    void close() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
