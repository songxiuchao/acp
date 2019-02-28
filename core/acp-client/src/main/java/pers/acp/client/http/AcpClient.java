package pers.acp.client.http;

import org.apache.http.client.HttpClient;
import pers.acp.client.exceptions.HttpException;
import pers.acp.core.CommonTools;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by zhangbin on 2017-8-16 15:53
 */
public class AcpClient {

    private String url = null;

    private Map<String, String> params;

    private String clientCharset;

    private boolean sendXML = false;

    private String rootNameXML = null;

    private boolean sendJSONStr = false;

    private String jsonString;

    private boolean sendBytes = false;

    private byte[] bytes;

    private boolean sendSOAP = false;

    private String username = null;

    private String password = null;

    private ClientSender client;

    private Map<String, String> headers = new HashMap<>();

    public HttpClient getHttpClient() {
        return client.getHttpClient();
    }

    public boolean isHttps() {
        return client.isHttps();
    }

    private void post(boolean post) {
        client = client.post(post);
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getClientCharset() {
        return clientCharset;
    }

    public int getTimeOut() {
        return client.getTimeOut();
    }

    public int getMaxTotalConn() {
        return client.getMaxTotalConn();
    }

    public int getMaxperRoute() {
        return client.getMaxPerRoute();
    }

    public String getUserAgent() {
        return headers.get("User-Agent");
    }

    public String getCookie() {
        return headers.get("Cookie");
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public String getUsername() {
        return username;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String getPassword() {
        return password;
    }

    /**
     * 伪装跳转地址
     *
     * @return 跳转地址
     */
    public String getRefer() {
        return headers.get("Referer");
    }

    public AcpClient url(String url) {
        this.url = url;
        return this;
    }

    public AcpClient clientCharset(String clientCharset) {
        this.clientCharset = clientCharset;
        return this;
    }

    /**
     * 伪装跳转地址
     *
     * @param referer 跳转地址
     */
    public AcpClient referer(String referer) {
        headers.put("Referer", referer);
        return this;
    }

    public AcpClient userAgent(String userAgent) {
        headers.put("User-Agent", userAgent);
        return this;
    }

    public AcpClient cookie(String cookie) {
        headers.put("Cookie", cookie);
        return this;
    }

    public AcpClient username(String username) {
        this.username = username;
        return this;
    }

    public AcpClient password(String password) {
        this.password = password;
        return this;
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }

    private void setParams(Map<String, String> params) {
        this.params = params;
    }

    private void setSendXML(boolean sendXML) {
        this.sendXML = sendXML;
    }

    private void setRootNameXML(String rootNameXML) {
        this.rootNameXML = rootNameXML;
    }

    private void setSendJSONStr(boolean sendJSONStr) {
        this.sendJSONStr = sendJSONStr;
    }

    private void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    private void setSendBytes(boolean sendBytes) {
        this.sendBytes = sendBytes;
    }

    private void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    private void setSendSOAP(boolean sendSOAP) {
        this.sendSOAP = sendSOAP;
    }

    /**
     * http 构造函数
     *
     * @param isHttps      是否https请求，默认false
     * @param maxTotalConn 最大链接数，默认1000
     * @param maxPerRoute  路由并发数，默认50
     * @param timeOut      超时时间
     */
    AcpClient(boolean isHttps, int maxTotalConn, int maxPerRoute, int timeOut) throws HttpException {
        super();
        client = new ClientSender(isHttps, maxTotalConn, maxPerRoute, timeOut);
    }

    /**
     * GET请求
     *
     * @return 响应信息
     */
    public ResponseResult doGet() throws HttpException {
        return doGet(null);
    }

    /**
     * GET请求
     *
     * @param params 请求参数
     * @return 响应信息
     */
    public ResponseResult doGet(Map<String, String> params) throws HttpException {
        post(false);
        setParams(params);
        return doRequest();
    }

    /**
     * POST请求
     *
     * @param params 参数
     * @return 响应信息
     */
    public ResponseResult doPost(Map<String, String> params) throws HttpException {
        post(true);
        setSendXML(false);
        setSendJSONStr(false);
        setSendBytes(false);
        setSendSOAP(false);
        setParams(params);
        return doRequest();
    }

    /**
     * POST请求发送XML
     *
     * @param params 参数
     * @return 响应信息
     */
    public ResponseResult doPostXML(Map<String, String> params) throws HttpException {
        return doPostXML(null, params);
    }

    /**
     * POST请求发送XML
     *
     * @param rootNameXML xml跟标签名
     * @param params      参数
     * @return 响应信息
     */
    public ResponseResult doPostXML(String rootNameXML, Map<String, String> params) throws HttpException {
        post(true);
        setSendXML(true);
        setSendJSONStr(false);
        setSendBytes(false);
        setSendSOAP(false);
        setRootNameXML(rootNameXML);
        setParams(params);
        setBytes(null);
        return doRequest();
    }

    /**
     * POST请求发送XML
     *
     * @param bytes xml内容
     * @return 响应信息
     */
    public ResponseResult doPostXML(byte[] bytes) throws HttpException {
        post(true);
        setSendXML(true);
        setSendJSONStr(false);
        setSendBytes(false);
        setSendSOAP(false);
        setRootNameXML(null);
        setParams(null);
        setBytes(bytes);
        return doRequest();
    }

    /**
     * POST请求发送JSON字符串
     *
     * @param jSONString json字符串
     * @return 响应信息
     */
    public ResponseResult doPostJSONStr(String jSONString) throws HttpException {
        post(true);
        setSendXML(false);
        setSendJSONStr(true);
        setSendBytes(false);
        setSendSOAP(false);
        setJsonString(jSONString);
        return doRequest();
    }

    /**
     * POST请求发送字节数组数据
     *
     * @param bytes 报文字节数组
     * @return 响应信息
     */
    public ResponseResult doPostBytes(byte[] bytes) throws HttpException {
        post(true);
        setSendXML(false);
        setSendJSONStr(false);
        setSendBytes(true);
        setSendSOAP(false);
        setBytes(bytes);
        return doRequest();
    }

    /**
     * POST请求发送SOAP报文数据
     *
     * @param bytes 报文字节数组
     * @return 响应信息
     */
    public ResponseResult doPostSOAP(byte[] bytes) throws HttpException {
        post(true);
        setSendXML(false);
        setSendJSONStr(false);
        setSendBytes(false);
        setSendSOAP(true);
        setBytes(bytes);
        return doRequest();
    }

    /**
     * 执行请求
     *
     * @return 响应
     */
    private ResponseResult doRequest() throws HttpException {
        Map<String, String> contextParam = null;
        if (!CommonTools.isNullStr(username)) {
            contextParam = new HashMap<>();
            contextParam.put("username", username);
            contextParam.put("password", password);
        }
        return client.params(params)
                .clientCharset(clientCharset)
                .headers(headers)
                .sendXML(sendXML)
                .rootName(rootNameXML)
                .sendJSONStr(sendJSONStr)
                .jsonString(jsonString)
                .sendBytes(sendBytes)
                .bytes(bytes)
                .sendSOAP(sendSOAP)
                .contextParams(contextParam)
                .doRequest(url);
    }

}
